package com.malicia.mrg.assistant.photo.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.malicia.mrg.assistant.photo.exception.CustomException;
import com.malicia.mrg.assistant.photo.pojo.Photoshoot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class FileSystemService {

    private FileSystemService() {
    }

    public static List<Path> getAllFilesFromFolderWithType(String rootDir, List<String> authorizedExtensions, boolean includeSubdirectories) {
        Path rootPath = Paths.get(rootDir);
        List<Path> matchingFiles = new ArrayList<>();

        try {
            if (includeSubdirectories) {
                try (Stream<Path> paths = Files.walk(rootPath)) {
                    paths
                            .filter(Files::isRegularFile)
                            .filter(path -> hasAuthorizedExtension(path, authorizedExtensions))
                            .forEach(matchingFiles::add);
                }
            } else {
                // Only scan top-level directory (non-recursive)
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootPath)) {
                    for (Path path : stream) {
                        if (Files.isRegularFile(path) && hasAuthorizedExtension(path, authorizedExtensions)) {
                            matchingFiles.add(path);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new CustomException(e);
        }

        return matchingFiles;
    }

    private static boolean hasAuthorizedExtension(Path file, List<String> authorizedExtensions) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) return false;

        String extension = fileName.substring(dotIndex + 1).toLowerCase();
        return authorizedExtensions.contains(extension);
    }


    public static String getNormalizedPath(String rawPath) {
        // Step 1: Replace backslashes with the system separator (especially useful on Linux when user types Windows-style paths)
        String cleaned = rawPath.replace("\\", File.separator);

        // Step 2: Remove or replace characters illegal in Windows file paths (safe fallback)
        // You can tweak this if you're 100% Linux
        cleaned = cleaned.replaceAll("(?<!^[a-zA-Z]):|[<>\\\"|?*]", "_");

        // Step 3: Normalize the path to remove redundant components like `.` or `..`
        Path normalized = Paths.get(cleaned).normalize();

        return normalized.toString();
    }

    public static String sanitizeFileName(String input) {
        // Replace invalid characters for Windows and Unix-based systems
        String sanitized = input.replaceAll("[\\\\/:*?\"<>|]", "_");

        // Remove leading and trailing spaces or dots (optional)
        sanitized = sanitized.replaceAll("^[\\.\\s]+$|^[\\.\\s]+$", "");

        // Optionally, you can limit the length of the file name (max 255 chars for most systems)
        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }

        return sanitized;
    }

    // Helper method to get the file extension
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return ""; // No extension found
        }
        return fileName.substring(dotIndex + 1).toLowerCase(); // Get extension and convert to lowercase
    }

    // Helper method to get file creation date
    public static String getFileCreatedDate(Path path) {
        try {
            FileTime fileTime = (FileTime) Files.getAttribute(path, "creationTime");
            Date date = new Date(fileTime.toMillis());
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (IOException e) {
            return "Unknown"; // If creation date can't be fetched
        }
    }

    public static void putIntoJsonFile(Object expectedList, String jsonDest) {
        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        // Enable pretty print
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Write the object to a file (output.json)
        File file = new File(jsonDest);
        try {
            objectMapper.writeValue(file, expectedList);  // This writes the JSON to the file
        } catch (IOException e) {
            throw new CustomException(e);
        }
    }

    public static boolean moveFileWithTimestamp(String sourcePathStr, String destinationPathStr, boolean dryRun) throws IOException {
        Path sourcePath = Paths.get(sourcePathStr);
        Path sourcePathXmp = Paths.get(sourcePathStr + ".xmp");
        Path destinationPath = Paths.get(destinationPathStr);
        Path destinationPathXmp = Paths.get(destinationPathStr + ".xmp");

        // Check if source file exists
        if (!Files.exists(sourcePath)) {
            throw new IOException("Source file does not exist: " + sourcePathStr);
        }

        if (dryRun) {
            System.out.println("Dry run mode:");
            System.out.println("Source path: " + sourcePath.toString());
            if (Files.exists(sourcePathXmp)) {
                System.out.println("Source XMP path: " + sourcePathXmp.toString());
            }
            System.out.println("Destination path: " + destinationPath.toString());

        }else{
            // Ensure the destination directory exists
            Files.createDirectories(destinationPath.getParent());

            moveFileAction(sourcePath, destinationPath);
            // Check if xmp file exists
            if (Files.exists(sourcePathXmp)) {
                moveFileAction(sourcePathXmp, destinationPathXmp);
            }
        }

        return true;
    }

    private static void moveFileAction(Path sourcePath, Path destinationPath) throws IOException {
        // Retrieve the last modified time of the source file
        BasicFileAttributes attrs = Files.readAttributes(sourcePath, BasicFileAttributes.class);
        FileTime lastModifiedTime = attrs.lastModifiedTime();

        // Move the file
        Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

        // Set the last modified time of the destination file to match the source file
        Files.setLastModifiedTime(destinationPath, lastModifiedTime);
    }

    public static List<Path> getAllFolder(String pathToScan) throws IOException {
        Path rootPath = Paths.get(pathToScan);
        List<Path> subdirectories = new ArrayList<>();

        // Walk through the directory tree
        Files.walk(rootPath, 1)
                .filter(Files::isDirectory)  // Only directories
                .filter(p -> !p.equals(rootPath))  // Exclude the root path
                .forEach(subdirectories::add);

        return subdirectories;
    }

    public static List<Photoshoot> convertPathsToPhotoshoot(String pathToScan, List<Path> listPath) {
        List<Photoshoot> photoshoots = new ArrayList<>();
        Path rootPath = Paths.get(pathToScan);
        for (Path path : listPath) {
            // Create a new Photoshoot object
            Photoshoot photoshoot = new Photoshoot();
            photoshoot.setName(path.toString().replace(rootPath + File.separator, ""));
            photoshoot.setPath(path.toString());
            photoshoot.setPhotoshootRoot(pathToScan + File.separator);

            photoshoots.add(photoshoot);
        }

        return photoshoots;
    }

    public static byte[] getReadAllBytes(Path imagePath) throws IOException {
        return Files.readAllBytes(imagePath);
    }

    public static BufferedImage getBufferedImage(String path) throws IOException {
        File imgFile = new File(path);
        return ImageIO.read(imgFile);
    }

    public static Metadata getMetadata(Path path) throws ImageProcessingException, IOException {
        File file = path.toFile();
        return ImageMetadataReader.readMetadata(file);
    }

    public static void moveDirectory(String sourcePathStr, String destinationPathStr, boolean dryRun) throws IOException {
        Path sourcePath = Paths.get(sourcePathStr);
        Path destinationPath = Paths.get(destinationPathStr);

        if (!Files.exists(sourcePath) || !Files.isDirectory(sourcePath)) {
            throw new IOException("Source directory does not exist or is not a directory: " + sourcePathStr);
        }

        if (Files.exists(destinationPath)) {
            throw new IOException("Destination already exists; cannot proceed: " + destinationPathStr);
        }

        if (dryRun) {
            System.out.println("Dry run mode - Planned move from: " + sourcePath + " to " + destinationPath);
            Files.walk(sourcePath).forEach(path -> {
                Path relative = sourcePath.relativize(path);
                Path target = destinationPath.resolve(relative);
                System.out.println("Would move: " + path + " -> " + target);
            });
        } else {
            // Move the entire directory (sourcePath becomes destinationPath)
            Files.move(sourcePath, destinationPath, StandardCopyOption.ATOMIC_MOVE);
        }
    }
}


