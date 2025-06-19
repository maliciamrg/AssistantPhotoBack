package com.malicia.mrg.assistant.photo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.pojo.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RootRepertoire {

    private final MyConfig config;

    public RootRepertoire(MyConfig config) {
        this.config = config;
        controlConfig(config);
    }

    public static int moveGroupToDestinationFolder(String destinationFolder, PhotoGroup photoGroupFrom, boolean addAutoSubFolder, boolean dryRun) {
        AtomicInteger ret = new AtomicInteger();

        String folderWor = "";
        if (addAutoSubFolder) {
            String folderNameDatePart = photoGroupFrom.getPhotos().get(0).getExifDate().split(" ")[0].replace(":", "_");
            String folderNameNumPart = String.format("%05d", photoGroupFrom.size());
            folderWor = folderNameDatePart + "_(" + folderNameNumPart + ")" + "\\";
        }

        String finalFolderWor = folderWor;
        photoGroupFrom.forEach(photo -> {
            String src = photo.getPath();
            String newName = FileSystemService.sanitizeFileName(photo.getRelativeToPath());
            String dest = FileSystemService.getNormalizedPath(destinationFolder + "\\" + finalFolderWor + newName);
            try {
                if (FileSystemService.moveFileWithTimestamp(src, dest, dryRun)) {
                    ret.incrementAndGet();
                    System.out.println(ret + " : " + src + " ==> " + dest);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        });

        return ret.get();
    }

    private void controlConfig(MyConfig config) {
        if (config.getRootPath() == null || config.getRootPath().isEmpty()) {
            throw new IllegalArgumentException("Root Path is empty");
        }
    }

    public List<Photoshoot> getPhotoshootList(PhotoshootTypeEnum photoshootTypEnum) {
        List<Photoshoot> expectedList = new ArrayList<>();

        for (PhotoshootType photoshootType : config.getPhotoshootType()) {
            if (photoshootTypEnum.name().equals(photoshootType.getPhotoshootTypeEnum().name())) {
                for (Photoshoot photoshoot : photoshootType.getPhotoshootList()) {

                    String pathToScan = config.getRootPath() + photoshoot.getPath();

                    try {
                        List<Path> listPath = FileSystemService.getAllFolder(pathToScan);
                        expectedList.addAll(FileSystemService.convertPathsToSeanceRepertoire(pathToScan, listPath));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
        return expectedList;
    }

    public PhotoGroup getAllPhotoFromSeanceRepertoire(Photoshoot photoshoot) {
        PhotoGroup expectedList = new PhotoGroup();

//        String pathToScan = config.getRootPath() + photoshoot.getPath();
        String pathToScan = photoshoot.getPath();
        List<String> includeTypeFile = config.getFileExtensionsToWorkWith();

        try {
            List<Path> listPath = FileSystemService.getAllFilesFromFolderAndSubFolderWithType(pathToScan, includeTypeFile);
            expectedList = FileSystemService.convertPathsToPhotos(pathToScan, listPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return expectedList;
    }

    public PhotoGroup getAllPhotoFromSeanceRepertoire(List<Photoshoot> repertoires) {
        PhotoGroup expectedList = new PhotoGroup();
        for (Photoshoot repertoire : repertoires) {
            expectedList.addAll(getAllPhotoFromSeanceRepertoire(repertoire));
        }

        return expectedList;
    }

    public List<String> getAllPathFromSeanceRepertoire(List<Photoshoot> repertoires) {
        List<String> expectedList = new ArrayList<>();
        for (Photoshoot repertoire : repertoires) {
            expectedList.add(Paths.get(config.getRootPath() + repertoire.getPath()).toString());
        }
        return expectedList;
    }

    public PhotoGroup getAllPhotoFromSeanceRepertoireToJson(List<Photoshoot> repertoires, String jsonDest) {
        PhotoGroup expectedList = getAllPhotoFromSeanceRepertoire(repertoires);

        FileSystemService.putIntoJsonFile(expectedList, jsonDest);

        return expectedList;
    }

    public List<Photo> getAllPhotoFromJson(String jsonDest) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(jsonDest);
        List<Photo> allPhotoFromSeanceRepertoireFromFile = new ArrayList<>();
        try {
            allPhotoFromSeanceRepertoireFromFile = objectMapper.readValue(file, new TypeReference<List<Photo>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allPhotoFromSeanceRepertoireFromFile;
    }

    public List<PhotoGroup> getGroupOfPhotoFrom(List<Photo> allPhotos) {
        List<PhotoGroup> groupedPhotos = new ArrayList<>();

        // Sort photos based on exifDate (ascending), using fake LocalDateTime for invalid dates
        allPhotos.sort((p1, p2) -> {
            LocalDateTime p1Date = parseDate(p1.getExifDate());
            LocalDateTime p2Date = parseDate(p2.getExifDate());
            return p1Date.compareTo(p2Date);
        });

        PhotoGroup currentGroup = new PhotoGroup();
        PhotoGroup nullExifGroup = new PhotoGroup(); // Separate group for photos with null or invalid exifDate

        for (Photo photo : allPhotos) {
            if (photo.getExifDate() == null || photo.getExifDate().equals("Unknown")) {
                nullExifGroup.add(photo); // Add to separate group for null or invalid exifDate
                continue; // Skip the rest of the loop for invalid exifDate photos
            }

            boolean addedToGroup = false;
            LocalDateTime photoExifDate = parseDate(photo.getExifDate());

            // Check if the photo can be added to the current group
            for (Photo groupPhoto : currentGroup) {
                LocalDateTime groupExifDate = parseDate(groupPhoto.getExifDate());
                // Calculate the difference in minutes
                long diffInMinutes = Duration.between(groupExifDate, photoExifDate).toMinutes();

                // Check if the difference is within 10 minutes
                if (Math.abs(diffInMinutes) <= config.getGroupPhoto().getEcartEnMinutes()) {
                    currentGroup.add(photo);
                    addedToGroup = true;
                    break;
                }
            }

            // If not added to any group, start a new group
            if (!addedToGroup) {
                if (!currentGroup.empty()) {
                    groupedPhotos.add(currentGroup); // Save the current group
                }
                currentGroup = new PhotoGroup();
                currentGroup.add(photo); // Start a new group with the current photo
            }
        }

        // Add the last group if there are any
        if (!currentGroup.empty()) {
            groupedPhotos.add(currentGroup);
        }

        // Now group all groups with less than 5 photos into a big group
        PhotoGroup bigGroup = new PhotoGroup();
        Iterator<PhotoGroup> iterator = groupedPhotos.iterator();

        while (iterator.hasNext()) {
            PhotoGroup group = iterator.next();
            if (group.size() < config.getGroupPhoto().getPhotoMin()) {
                bigGroup.addAll(group); // Add small group to the big group
                iterator.remove(); // Remove the small group from the list
            }
        }

        // If there are any small groups, add the big group to the result
        if (!bigGroup.empty()) {
            groupedPhotos.add(bigGroup);
        }
        // Add null or invalid exifDate group to the result if it has any photos
        if (!nullExifGroup.empty()) {
            groupedPhotos.add(nullExifGroup);
        }

        return groupedPhotos;
    }

    // Method to parse exifDate (example format: "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime parseDate(String exifDate) {
        // If the exifDate is invalid, return null
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
        try {
            return LocalDateTime.parse(exifDate, formatter);
        } catch (Exception e) {
            return LocalDateTime.MIN;
        }
    }

    public List<Photoshoot> getPhotoshootList(Photoshoot photoshoot) {
        List<Photoshoot> expectedList = new ArrayList<>();

        String pathToScan = config.getRootPath() + photoshoot.getPath();
        //String pathToScan = photoshoot.getPath();

        try {
            List<Path> listPath = FileSystemService.getAllFolder(pathToScan);
            expectedList = FileSystemService.convertPathsToSeanceRepertoire(pathToScan, listPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return expectedList;
    }

    public PhotoGroup getAllPhotoFromPhotoshoot(Photoshoot photoshoot) {
        PhotoGroup expectedList = new PhotoGroup();

        String pathToScan = photoshoot.getPath();
        List<String> includeTypeFile = config.getFileExtensionsToWorkWith();

        try {
            List<Path> listPath = FileSystemService.getAllFilesFromFolderAndSubFolderWithType(pathToScan, includeTypeFile);
            expectedList.addAll(FileSystemService.convertPathsToPhotos(pathToScan, listPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return expectedList;
    }
}

