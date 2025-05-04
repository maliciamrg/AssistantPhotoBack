package com.malicia.mrg.assistant.photo.service;

import com.adobe.internal.xmp.XMPException;
import com.malicia.mrg.assistant.photo.DTO.XMPPhotoDto;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.malicia.mrg.assistant.photo.file.WorkWithFile.generateThumbnail;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    @Autowired
    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public Photo savePhoto(Photo photo) {
        return photoRepository.save(photo);
    }

    @Transactional
    public List<Photo> saveAllPhotos(List<Photo> photos, boolean writeXmp) {
        for (Photo photo : photos) {
            // Check if a photo with the same path and filename already exists
            Optional<Photo> existingPhotoOpt = photoRepository.findByPathAndFilename(photo.getPath(), photo.getFilename());

            if (existingPhotoOpt.isPresent()) {
                // If photo exists, update it
                Photo existingPhoto = existingPhotoOpt.get();
                //existingPhoto.setThumbnail(photo.getThumbnail());
                existingPhoto.setRelativeToPath(photo.getRelativeToPath());
                existingPhoto.setExtension(photo.getExtension());
                existingPhoto.setCreatedDate(photo.getCreatedDate());
                existingPhoto.setExifDate(photo.getExifDate());
                existingPhoto.setDate_taken(photo.getDate_taken());
//                existingPhoto.setFlagType(photo.getFlagType());
//                existingPhoto.setFlagged(photo.getFlagged());
//                existingPhoto.setStarred(photo.getStarred());
                existingPhoto.setExifDate(photo.getExifDate());

                // Save the updated photo
                photoRepository.save(existingPhoto);
            } else {
                // If photo doesn't exist, save the new one
                generateThumbnail(photo);
                photoRepository.save(photo);
            }

            if (writeXmp) {
                try {
                    XMPPhotoDto xmpPhotoDto = new XMPPhotoDto();
                    xmpPhotoDto.setRating(photo.getFlagType().equals("reject")?-1:photo.getStarred());
                    XMPService.storeMetadata(xmpPhotoDto,photo.getPath()+".xmp");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (XMPException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return photos;
    }

    public List<Photo> getAllPhotosWithPathStartingWith(String prefix) {
        return photoRepository.findByPathStartingWith(prefix);
    }

    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    public Photo getPhotoById(UUID id) {
        return photoRepository.findById(id).orElse(null);
    }
}
