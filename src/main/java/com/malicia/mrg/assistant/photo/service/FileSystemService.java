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

public class FileSystemService {

    private FileSystemService() {
    }

    public static List<Path> getAllFilesFromFolderAndSubFolderWithType(String rootDir, List<String> authorizedExtensions) {
        Path rootPath = Paths.get(rootDir);
        List<Path> matchingFiles = new ArrayList<>();

        // Walk through the directory tree
        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Check if the file has an authorized extension
                    String fileName = file.getFileName().toString();
                    String extension = getFileExtension(fileName);

                    if (authorizedExtensions.contains(extension)) {
                        matchingFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    // Handle errors, such as permissions, here if needed
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new CustomException(e);
        }

        return matchingFiles;
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
        Path destinationPath = Paths.get(destinationPathStr);

        // Check if source file exists
        if (!Files.exists(sourcePath)) {
            throw new IOException("Source file does not exist: " + sourcePathStr);
        }

        if (!dryRun) {
            // Ensure the destination directory exists
            Files.createDirectories(destinationPath.getParent());

            // Move the file
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Retrieve the last modified time of the source file
            BasicFileAttributes attrs = Files.readAttributes(sourcePath, BasicFileAttributes.class);
            FileTime lastModifiedTime = attrs.lastModifiedTime();

            // Set the last modified time of the destination file to match the source file
            Files.setLastModifiedTime(destinationPath, lastModifiedTime);
        }

        return true;
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
}


