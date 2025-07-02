package com.malicia.mrg.assistant.photo.service;

import com.adobe.internal.xmp.XMPException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.dto.PhotoMapper;
import com.malicia.mrg.assistant.photo.entity.Photo;
import com.malicia.mrg.assistant.photo.pojo.PhotoGroup;
import com.malicia.mrg.assistant.photo.pojo.XMPPhoto;
import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.drew.metadata.exif.ExifDirectoryBase.TAG_DATETIME;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final ThumbnailService thumbnailService;

    public PhotoService(PhotoRepository photoRepository, ThumbnailService thumbnailService) {
        this.photoRepository = photoRepository;
        this.thumbnailService = thumbnailService;
    }

    public List<PhotoDTO> convertPathsToPhotos(String rootDir, List<Path> paths) {
        List<PhotoDTO> photoDTOList = new ArrayList<>();

        for (Path path : paths) {

            PhotoDTO photo = getPhotoDataFromPath(rootDir, path);

            photoDTOList.add(photo);

        }

        return photoDTOList;
    }

    @Cacheable(value = "getPhotoDataFromPath", key = "#path.toString()")
    public PhotoDTO getPhotoDataFromPath(String rootDir, Path path) {

        // Create a new Photo object
        Photo photo = new Photo();

        AddPhotoHashFromFile(path, photo);

        try {
            Optional<Photo> existingPhoto = photoRepository.findByHash(photo.getHash());
            if (existingPhoto.isPresent()) {
                photo = existingPhoto.get();
                if (photo.getPath().equals(path)) {
                    System.out.println("/!\\  photo with hash: " + photo.getHash() + " \n" + "[(existing) " + photo.getPath() + " \n" + "[(new)      " + path + " ]");
                }
            } else {

                addPhotoDataFromFile(rootDir, path, photo);

                // Reach xmp if exist

                addPhotoDataFromXmpSidecar(path, photo);


                photo.setThumbnail(thumbnailService.generateThumbnail(photo));

                photoRepository.save(photo);

                //   photoThumbnailRepository.save(generateThumbnail(photo));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return PhotoMapper.toDto(photo);
    }

    private void addPhotoDataFromFile(String rootDir, Path path, Photo photo) {
        photo.setPath(path.toString());
        photo.setRelativeToPath(FileSystemService.getNormalizedPath(path.toString()).replace(FileSystemService.getNormalizedPath(rootDir), ""));

        // Extract filename and extension
        String filename = path.getFileName().toString();
        String extension = FileSystemService.getFileExtension(filename);
        photo.setFilename(filename);
        photo.setExtension(extension);

        // Try to extract EXIF date (only if the file is an image)
        if (extension.equalsIgnoreCase("ARW") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
            photo.setExifDate(getExifDate(path));
        }

    }

    private void AddPhotoHashFromFile(Path path, Photo photo) {
        try {
            photo.setHash(generateImageHash(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateImageHash(Path imagePath) throws Exception {
        byte[] imageBytes = FileSystemService.getReadAllBytes(imagePath);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(imageBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private void addPhotoDataFromXmpSidecar(Path path, Photo photo) throws IOException {
        photo.setRating(0);
        photo.setPick(0);
        photo.setLabel("");
        photo.setKeywords(List.of(new String[0]));
        XMPPhoto xmpPhoto = new XMPPhoto();
        try {
            xmpPhoto = XMPService.readMetadata(path + ".xmp");

            Integer rating = xmpPhoto.getRating();
            if (rating != null) {
                photo.setRating(rating);
            }

            Integer pick = xmpPhoto.getPick();
            if (pick != null) {
                photo.setPick(pick);
            }

            List<String> keywords = xmpPhoto.getKeywords();
            if (keywords != null) {
                photo.setKeywords(keywords);
            }

            String label = xmpPhoto.getLabel();
            if (label != null) {
                photo.setLabel(label);
            }
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }


        // Get file creation date (from filesystem)
        photo.setCreatedDate(FileSystemService.getFileCreatedDate(path));
        if (xmpPhoto.getCreateDate() != null) {
            if (xmpPhoto.getCreateDate().compareTo("null") != 0) {
                photo.setCreatedDate(xmpPhoto.getCreateDate());
            }
        }

        // set file tags
        photo.setKeywords(List.of(new String[0]));
        if (xmpPhoto.getKeywords() != null) {
            if (xmpPhoto.getKeywords().size() > 0) {
                photo.setKeywords(xmpPhoto.getKeywords());
            }
        }
    }

    // Helper method to get EXIF date from an image file
    private String getExifDate(Path path) {
        try {
            // Read EXIF data if the file is an image
            Metadata metadata = FileSystemService.getMetadata(path);

            // Get the EXIF directory
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (directory != null) {
                // Access EXIF DateTimeOriginal tag directly (tag ID 0x9003)
                String exifDate = directory.getString(TAG_DATETIME);
                exifDate = exifDate.replaceFirst("^(\\d{4}):(\\d{2}):(\\d{2})", "$1-$2-$3");

                if (exifDate != null) {
                    return exifDate; // Return the EXIF datetime
                }
            }
        } catch (Exception e) {
            // If no EXIF data or error, return "Unknown"
            e.printStackTrace();
        }
        return "Unknown";
    }

    public PhotoGroup saveAllPhotos(PhotoGroup photos, boolean writeXmp) {
        for (PhotoDTO photo : photos) {

            if (writeXmp) {
                try {
                    XMPPhoto xmpPhoto = new XMPPhoto();
                    xmpPhoto.setRating(photo.getRating());
                    xmpPhoto.setPick(photo.getPick());
                    xmpPhoto.setCreateDate(photo.getCreatedDate());
                    xmpPhoto.setKeywords(photo.getKeywords());
                    XMPService.storeMetadata(xmpPhoto, photo.getPath() + ".xmp");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (XMPException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return photos;
    }
}
