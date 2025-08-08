package com.malicia.mrg.assistant.photo.service;

import com.adobe.internal.xmp.XMPException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.dto.PhotoMetadataDTO;
import com.malicia.mrg.assistant.photo.entity.*;
import com.malicia.mrg.assistant.photo.mapper.PhotoMapper;
import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PhotoService {

    private static final Logger logger = LoggerFactory.getLogger(PhotoService.class);
    private final PhotoRepository photoRepository;
    private final ThumbnailService thumbnailService;


    public PhotoService(PhotoRepository photoRepository, ThumbnailService thumbnailService) {
        this.photoRepository = photoRepository;
        this.thumbnailService = thumbnailService;
    }

    public boolean deletePhoto(UUID id) {
        return false;
    }

    public Optional<Photo> updatePhoto(UUID id, Photo photo) {
        return null;
    }

    public Photo savePhoto(Photo photo) {
        return null;
    }

    public Optional<Photo> getPhotoById(UUID id) {
        return null;
    }

    public List<Photo> getAllPhotos() {
        return null;
    }

    @CacheEvict(value = "getAllPhotoFromPhotoshoot", allEntries = true)
    public Photo updatePhotoMetadata(UUID photoId, PhotoMetadataDTO metadataDTO) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found with id: " + photoId));

        PhotoMetadata metadata = photo.getPhotoMetadata();
        if (metadata == null) {
            metadata = new PhotoMetadata();
        }


        metadata.setRating(metadataDTO.getRating());
        metadata.setPick(metadataDTO.getPick());
        metadata.setLabel(metadataDTO.getLabel());
        metadata.setKeywords(metadataDTO.getKeywords() != null ? new ArrayList<>(metadataDTO.getKeywords()) : new ArrayList<>());
        metadata.setPhoto(photo);
        photo.setPhotoMetadata(metadata);
        photoRepository.save(photo);
        return photo;
    }

    @CacheEvict(value = "getAllPhotoFromPhotoshoot", allEntries = true)
    public Photo updatePhotoStar(UUID photoId, @Min(0) @Max(5) Integer nbStar) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found with id: " + photoId));

        PhotoMetadata metadata = photo.getPhotoMetadata();
        if (metadata == null) {
            metadata = new PhotoMetadata();
        }

        metadata.setRating(nbStar);
        metadata.setPhoto(photo);
        photo.setPhotoMetadata(metadata);
        photoRepository.save(photo);
        return photo;
    }

    @CacheEvict(value = "getAllPhotoFromPhotoshoot", allEntries = true)
    public Photo updatePhotoPick(UUID photoId, @Min(-1) @Max(1) Integer valuePick) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found with id: " + photoId));

        PhotoMetadata metadata = photo.getPhotoMetadata();
        if (metadata == null) {
            metadata = new PhotoMetadata();
        }

        metadata.setPick(valuePick);
        metadata.setPhoto(photo);
        photo.setPhotoMetadata(metadata);
        photoRepository.save(photo);
        return photo;
    }

    public PhotoDTO getPhotoDataFromPath(String rootDir, Path path) {
        logger.debug("getPhotoDataFromPath");

        // Create a new Photo object
        Photo photo = new Photo();

        AddPhotoHashFromFile(path, photo);

        try {
            Optional<Photo> existingPhoto = photoRepository.findByHash(photo.getHash());
            if (existingPhoto.isPresent()) {
                Photo photoRetrieve = existingPhoto.get();
                if (photoRetrieve.getFileSystem() != null && photoRetrieve.getFileSystem().getPath().toString().compareTo(path.toString())!=0) {
                    logger.info("Deleting existing photo hash: {} \n[(old) {} ]\n[(new) {} ]\n", photoRetrieve.getHash(), photoRetrieve.getFileSystem().getPath() ,path );
                    photoRepository.cleanupPhotoData(photoRetrieve.getId());
//                    photoRepository.delete(photo);
                } else {
                    photo = photoRetrieve;
                }
            }

            if (photo.getFileSystem() == null) {
                logger.debug("calculate getFileSystem");
                PhotoFileSystem fileSystemDataOfPhoto = getFileSystemDataOfPhoto(rootDir, path);
                fileSystemDataOfPhoto.setPhoto(photo);
                photo.setFileSystem(fileSystemDataOfPhoto);
            }

            if (photo.getExif() == null) {
                logger.debug("calculate getExif");
                PhotoExifData exifDataOfPhoto = getExifDataOfPhoto(path);
                exifDataOfPhoto.setPhoto(photo);
                photo.setExif(exifDataOfPhoto);
            }

            if (photo.getPhotoMetadata() == null) {
                logger.debug("calculate getPhotoMetadata");
                PhotoMetadata xmpSidecarDataOfPhoto = getXmpSidecarDataOfPhoto(path);
                xmpSidecarDataOfPhoto.setPhoto(photo);
                photo.setPhotoMetadata(xmpSidecarDataOfPhoto);
            }

            if (photo.getThumbnail() == null) {
                logger.debug("calculate getThumbnail");
                PhotoThumbnail photoThumbnail = thumbnailService.generateThumbnail(photo);
                photoThumbnail.setPhoto(photo);
                photo.setThumbnail(photoThumbnail);
            }

            logger.debug("photoRepository.save(photo)");
            photoRepository.save(photo);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.debug("PhotoMapper.toDTO(photo)");
        return PhotoMapper.toDTO(photo);
    }

    private PhotoFileSystem getFileSystemDataOfPhoto(String rootDir, Path path) {
        PhotoFileSystem photoFileSystemDTO = new PhotoFileSystem();

        photoFileSystemDTO.setRootDir(rootDir);
        photoFileSystemDTO.setPath(path.toString());
        photoFileSystemDTO.setRelativeToPath(FileSystemService.getNormalizedPath(path.toString()).replace(FileSystemService.getNormalizedPath(rootDir), ""));

        // Extract filename and extension
        String filename = path.getFileName().toString();
        String extension = FileSystemService.getFileExtension(filename);
        photoFileSystemDTO.setFilename(filename);
        photoFileSystemDTO.setExtension(extension);
        photoFileSystemDTO.setCreatedDate(FileSystemService.getFileCreatedDate(path));

        return photoFileSystemDTO;

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

    private PhotoMetadata getXmpSidecarDataOfPhoto(Path path) throws IOException {
        PhotoMetadata photoMetadata = new PhotoMetadata();

        photoMetadata.setCreateDate("");
        photoMetadata.setRating(0);
        photoMetadata.setPick(0);
        photoMetadata.setLabel("");
        photoMetadata.setKeywords(new ArrayList<>());
        try {
            photoMetadata = XMPService.readMetadata(path + ".xmp");
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }

        return photoMetadata;
    }

    private PhotoExifData getExifDataOfPhoto(Path path) {
        PhotoExifData dto = new PhotoExifData();

        try {
            Metadata metadata = FileSystemService.getMetadata(path);

            // Exif SubIFD (most date/time + ISO + exposure info)
            ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (subIFD != null) {
                String dateTime = subIFD.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (dateTime != null) {
                    dateTime = dateTime.replaceFirst("^(\\d{4}):(\\d{2}):(\\d{2})", "$1-$2-$3");
                    dto.setDateTimeOriginal(dateTime);
                }

                dto.setIso(subIFD.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
                dto.setFocalLength(subIFD.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
                dto.setAperture(subIFD.getString(ExifSubIFDDirectory.TAG_FNUMBER));
                dto.setExposureTime(subIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
            }

            if (ifd0 != null) {
                dto.setMake(ifd0.getString(ExifIFD0Directory.TAG_MAKE));
                dto.setModel(ifd0.getString(ExifIFD0Directory.TAG_MODEL));
                dto.setOrientation(ifd0.getString(ExifIFD0Directory.TAG_ORIENTATION));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
}
