package com.malicia.mrg.assistant.photo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.parameter.RepertoireOfType;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.file.WorkWithFile;
import com.malicia.mrg.assistant.photo.repertoire.GroupOfPhotos;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import org.springframework.stereotype.Component;

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

@Component
public class RootRepertoire {

    private final MyConfig config;

    public RootRepertoire(MyConfig config) {
        this.config = config;
        controlConfig(config);
    }

    public static int moveGroupToAssistantWork(String destinationFolder, GroupOfPhotos groupOfPhotoFrom, boolean dryRun) {
        AtomicInteger ret = new AtomicInteger();
        String folderNameDatePart = groupOfPhotoFrom.getPhotos().get(0).getExifDate().split(" ")[0].replace(":", "_");
        String folderNameNumPart = String.format("%05d", groupOfPhotoFrom.size());
        String folderWor = folderNameDatePart + "_(" + folderNameNumPart + ")";
        groupOfPhotoFrom.forEach(photo -> {
            String src = photo.getPath();
            String newName = WorkWithFile.sanitizeFileName(photo.getRelativeToPath());
            String dest = WorkWithFile.getNormalizedPath(destinationFolder + "\\" + folderWor + "\\" + newName);
            try {
                if (WorkWithFile.moveFileWithTimestamp(src, dest, dryRun)) {
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
        try {
            if (config.getRootPath().isEmpty()) {
                throw new CustomException("Root Path is empty");
            }

//            int size = getAllSeanceRepertoire(SeanceTypeEnum.ASSISTANT_WORK).size();
//            if (size > 1) {
//                throw new CustomException("ASSISTANT_WORK has more than one element.");
//            }
//            if (size == 0) {
//                throw new CustomException("ASSISTANT_WORK has no element.");
//            }

        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SeanceRepertoire> getAllSeanceRepertoire(SeanceTypeEnum typeOfSceance) {
        List<SeanceRepertoire> expectedList = new ArrayList<>();

        // Using enhanced for-each loop
        for (RepertoireOfType repertoireOfType : config.getRepertoireOfType()) {
            if (repertoireOfType.getSeanceType() == typeOfSceance) {
                for (SeanceRepertoire seanceRepertoire : repertoireOfType.getRepertoire()){
                    expectedList.addAll(getAllSeanceRepertoire(seanceRepertoire));
                }
            }
        }
        return expectedList;
    }

    public List<Photo> getAllPhotoFromSeanceRepertoire(SeanceRepertoire repertoire) {
        List<Photo> expectedList = new ArrayList<>();

//        String pathToScan = config.getRootPath() + repertoire.getPath();
        String pathToScan = repertoire.getPath();
        List<String> includeTypeFile = config.getFileExtensionsToWorkWith();

        try {
            List<Path> listPath = WorkWithFile.getAllFilesFromFolderAndSubFolderWithType(pathToScan, includeTypeFile);
            expectedList = WorkWithFile.convertPathsToPhotos(pathToScan, listPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return expectedList;
    }

    public List<Photo> getAllPhotoFromSeanceRepertoire(List<SeanceRepertoire> repertoires) {
        List<Photo> expectedList = new ArrayList<>();
        for (SeanceRepertoire repertoire : repertoires) {
            expectedList.addAll(getAllPhotoFromSeanceRepertoire(repertoire));
        }

        return expectedList;
    }

    public List<String> getAllPathFromSeanceRepertoire(List<SeanceRepertoire> repertoires) {
        List<String> expectedList = new ArrayList<>();
        for (SeanceRepertoire repertoire : repertoires) {
            expectedList.add(Paths.get(config.getRootPath() + repertoire.getPath()).toString());
        }
        return expectedList;
    }

    public List<Photo> getAllPhotoFromSeanceRepertoireToJson(List<SeanceRepertoire> repertoires, String jsonDest) {
        List<Photo> expectedList = getAllPhotoFromSeanceRepertoire(repertoires);

        WorkWithFile.putIntoJsonFile(expectedList, jsonDest);

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

    public List<GroupOfPhotos> getGroupOfPhotoFrom(List<Photo> allPhotos) {
        List<GroupOfPhotos> groupedPhotos = new ArrayList<>();

        // Sort photos based on exifDate (ascending), using fake LocalDateTime for invalid dates
        allPhotos.sort((p1, p2) -> {
            LocalDateTime p1Date = parseDate(p1.getExifDate());
            LocalDateTime p2Date = parseDate(p2.getExifDate());
            return p1Date.compareTo(p2Date);
        });

        GroupOfPhotos currentGroup = new GroupOfPhotos();
        GroupOfPhotos nullExifGroup = new GroupOfPhotos(); // Separate group for photos with null or invalid exifDate

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
                currentGroup = new GroupOfPhotos();
                currentGroup.add(photo); // Start a new group with the current photo
            }
        }

        // Add the last group if there are any
        if (!currentGroup.empty()) {
            groupedPhotos.add(currentGroup);
        }

        // Now group all groups with less than 5 photos into a big group
        GroupOfPhotos bigGroup = new GroupOfPhotos();
        Iterator<GroupOfPhotos> iterator = groupedPhotos.iterator();

        while (iterator.hasNext()) {
            GroupOfPhotos group = iterator.next();
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

    public List<SeanceRepertoire> getAllSeanceRepertoire(SeanceRepertoire seanceRepertoire) {
        List<SeanceRepertoire> expectedList = new ArrayList<>();

        String pathToScan = config.getRootPath() + seanceRepertoire.getPath();
        //String pathToScan = seanceRepertoire.getPath();

        try {
            List<Path> listPath = WorkWithFile.getAllFolder(pathToScan);
            expectedList = WorkWithFile.convertPathsToSeanceRepertoire(pathToScan, listPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return expectedList;
    }

    public List<Photo> getAllPhotoFromPhotoRepertoire(SeanceRepertoire SeanceRepertoire) {
        List<Photo> expectedList = new ArrayList<>();

        String pathToScan = SeanceRepertoire.getPath();
        List<String> includeTypeFile = config.getFileExtensionsToWorkWith();

        try {
            List<Path> listPath = WorkWithFile.getAllFilesFromFolderAndSubFolderWithType(pathToScan, includeTypeFile);
            expectedList = WorkWithFile.convertPathsToPhotos(pathToScan, listPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return expectedList;
    }
}

class CustomException extends Exception {
    public CustomException(String message) {
        super(message);
    }
}
