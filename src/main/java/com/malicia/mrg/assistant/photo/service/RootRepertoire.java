package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.pojo.PhotoGroup;
import com.malicia.mrg.assistant.photo.pojo.Photoshoot;
import com.malicia.mrg.assistant.photo.pojo.PhotoshootRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RootRepertoire {

    private static final Logger logger = LoggerFactory.getLogger(RootRepertoire.class);
    private final MyConfig config;
    private final PhotoService photoService;

    public RootRepertoire(MyConfig config, PhotoService photoService) {
        this.config = config;
        this.photoService = photoService;
        controlConfig(config);
    }

    public static int moveGroupToDestinationFolder(String destinationFolder, PhotoGroup photoGroupFrom, boolean addAutoSubFolder, boolean dryRun) {
        AtomicInteger ret = new AtomicInteger();

        String folderWor = "";
        if (addAutoSubFolder) {
            String folderNameDatePart = photoGroupFrom.getPhotos().get(0).getTakenDate().split(" ")[0].replace(":", "_");
            String folderNameNumPart = String.format("%05d", photoGroupFrom.size());
            folderWor = folderNameDatePart + "_(" + folderNameNumPart + ")" + "\\";
        }

        String finalFolderWor = folderWor;
        photoGroupFrom.forEach(photo -> {
            String src = photo.getPath();
            String newName = FileSystemService.sanitizeFileName(photo.getFilename());
            String dest = FileSystemService.getNormalizedPath(destinationFolder + "\\" + finalFolderWor + newName);
            try {
                if (FileSystemService.moveFileWithTimestamp(src, dest, dryRun)) {
                    ret.incrementAndGet();
                    logger.debug(ret + " : " + src + " ==> " + dest);
                }
            } catch (IOException e) {
                logger.debug(e.getMessage());
            }
        });

        return ret.get();
    }

    public static String movePhotoshootToNewFolder(Photoshoot photoshoot, String destinationFolder, boolean dryRun) {
        StringBuilder errors = new StringBuilder();

        // Move all non-picked photos to a "rejet" subdirectory
        photoshoot.getGroupOfPhoto().forEach(photo -> {
            if (photo.getPick() == -1) {
                String src = photo.getPath();
                String dest = FileSystemService.getNormalizedPath(photo.getRootDir() + File.separator + "rejet" + File.separator + photo.getFilename());
                try {
                    if (FileSystemService.moveFileWithTimestamp(src, dest, dryRun)) {
                        logger.debug("Moved to rejet: {} => {}", src, dest);
                    }
                } catch (IOException e) {
                    logger.debug("Error moving file: {}", e.getMessage());
                    errors.append("File move error: ").append(e.getMessage()).append("\n");
                }
            }
        });

        // Move the entire photoshoot directory
        try {
            FileSystemService.moveDirectory(photoshoot.getPath(), destinationFolder, dryRun);
        } catch (IOException e) {
            logger.debug("Error moving directory: {}", e.getMessage());
            errors.append("Directory move error: ").append(e.getMessage()).append("\n");
        }

        return errors.isEmpty() ? "done" : errors.toString();
    }

    private void controlConfig(MyConfig config) {
        if (config.getRootPath() == null || config.getRootPath().isEmpty()) {
            throw new IllegalArgumentException("Root Path is empty");
        }
    }

    public List<Photoshoot> getPhotoshootList(PhotoshootRoot photoshootRoot) {
        String pathToScan = FileSystemService.getNormalizedPath(config.getRootPath() + photoshootRoot.getPath());

        try {
            List<Path> listPath = FileSystemService.getAllFolder(pathToScan);
            return FileSystemService.convertPathsToPhotoshoot(pathToScan, listPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public PhotoGroup getAllPhotoFromListPhotoshoot(List<Photoshoot> repertoires) {
//        PhotoGroup photoGroup = new PhotoGroup();
//        for (Photoshoot repertoire : repertoires) {
//            photoGroup.addAll(getAllPhotoFromPhotoshoot(repertoire));
//        }
//
//        return photoGroup;
//    }

//    public PhotoGroup getAllPhotoFromSeanceRepertoireToJson(List<Photoshoot> repertoires, String jsonDest) {
//        PhotoGroup expectedList = getAllPhotoFromListPhotoshoot(repertoires);
//
//        FileSystemService.putIntoJsonFile(expectedList, jsonDest);
//
//        return expectedList;
//    }

//    public List<PhotoGroup> getGroupOfPhotoFrom(List<Photo> allPhotos) {
//        List<PhotoGroup> groupedPhotos = new ArrayList<>();
//
//        // Sort photos based on exifDate (ascending), using fake Date for invalid dates
//        allPhotos.sort((p1, p2) -> {
//            Date p1Date = parseDate(p1.getExif().getDateTimeOriginal());
//            Date p2Date = parseDate(p2.getExif().getDateTimeOriginal());
//            return p1Date.compareTo(p2Date);
//        });
//
//        PhotoGroup currentGroup = new PhotoGroup();
//        PhotoGroup nullExifGroup = new PhotoGroup(); // Separate group for photos with null or invalid exifDate
//
//        for (Photo photo : allPhotos) {
//            if (photo.getExif().getDateTimeOriginal() == null || photo.getExif().getDateTimeOriginal().equals("Unknown")) {
//                nullExifGroup.add(photo); // Add to separate group for null or invalid exifDate
//                continue; // Skip the rest of the loop for invalid exifDate photos
//            }
//
//            boolean addedToGroup = false;
//            Date photoExifDate = parseDate(photo.getExif().getDateTimeOriginal());
//
//            // Check if the photo can be added to the current group
//            for (PhotoDTO groupPhoto : currentGroup) {
//                Date groupExifDate = parseDate(groupPhoto.getExifDate());
//                // Calculate the difference in minutes
//                long diffInMinutes = Duration.between(groupExifDate, photoExifDate).toMinutes();
//
//                // Check if the difference is within 10 minutes
//                if (Math.abs(diffInMinutes) <= config.getGroupPhoto().getEcartEnMinutes()) {
//                    currentGroup.add(photo);
//                    addedToGroup = true;
//                    break;
//                }
//            }
//
//            // If not added to any group, start a new group
//            if (!addedToGroup) {
//                if (!currentGroup.empty()) {
//                    groupedPhotos.add(currentGroup); // Save the current group
//                }
//                currentGroup = new PhotoGroup();
//                currentGroup.add(photo); // Start a new group with the current photo
//            }
//        }
//
//        // Add the last group if there are any
//        if (!currentGroup.empty()) {
//            groupedPhotos.add(currentGroup);
//        }
//
//        // Now group all groups with less than 5 photos into a big group
//        PhotoGroup bigGroup = new PhotoGroup();
//        Iterator<PhotoGroup> iterator = groupedPhotos.iterator();
//
//        while (iterator.hasNext()) {
//            PhotoGroup group = iterator.next();
//            if (group.size() < config.getGroupPhoto().getPhotoMin()) {
//                bigGroup.addAll(group); // Add small group to the big group
//                iterator.remove(); // Remove the small group from the list
//            }
//        }
//
//        // If there are any small groups, add the big group to the result
//        if (!bigGroup.empty()) {
//            groupedPhotos.add(bigGroup);
//        }
//        // Add null or invalid exifDate group to the result if it has any photos
//        if (!nullExifGroup.empty()) {
//            groupedPhotos.add(nullExifGroup);
//        }
//
//        return groupedPhotos;
//    }

//    // Method to parse exifDate (example format: "yyyy-MM-dd HH:mm:ss")
//    private Date parseDate(String exifDate) {
//        // If the exifDate is invalid, return null
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
//        try {
//            return Date.parse(exifDate);
//        } catch (Exception e) {
//            return Date.MIN;
//        }
//    }

    public List<Path> getPathList(String pathToScan) {
        List<String> includeTypeFile = config.getFileExtensionsToWorkWith();
        List<Path> listPath = FileSystemService.getAllFilesFromFolderWithType(pathToScan, includeTypeFile, false);
        return listPath;
    }

//    public List<Photoshoot> getPhotoshootList(List<PhotoshootRoot> photoshootRootList) {
//        List<Photoshoot> photoshootList = new ArrayList<>();
//        for (PhotoshootRoot photoshootRoot : photoshootRootList) {
//            photoshootList.addAll(getPhotoshootList(photoshootRoot));
//        }
//
//        return photoshootList;
//    }
}

