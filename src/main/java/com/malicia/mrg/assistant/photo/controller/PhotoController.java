package com.malicia.mrg.assistant.photo.controller;

import com.adobe.internal.xmp.XMPException;
import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.dto.PhotoMetadataDTO;
import com.malicia.mrg.assistant.photo.entity.Photo;
import com.malicia.mrg.assistant.photo.service.PhotoService;
import com.malicia.mrg.assistant.photo.service.XMPService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photo")
@Validated
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    // GET all photos
    @GetMapping
    public ResponseEntity<List<Photo>> getAllPhotos() {
        return ResponseEntity.ok(photoService.getAllPhotos());
    }

    // GET photo by ID
    @GetMapping("/{id}")
    public ResponseEntity<Photo> getPhotoById(@PathVariable UUID id) {
        return photoService.getPhotoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    // POST create photo
//    @PostMapping
//    public ResponseEntity<Photo> createPhoto(@RequestBody Photo photo) {
//        Photo createdPhoto = photoService.savePhoto(photo);
//        return ResponseEntity.ok(createdPhoto);
//    }
//
//    // PUT update photo
//    @PutMapping("/{id}")
//    public ResponseEntity<Photo> updatePhoto(@PathVariable UUID id, @RequestBody Photo photo) {
//        return photoService.updatePhoto(id, photo)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // DELETE photo
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePhoto(@PathVariable UUID id) {
//        if (photoService.deletePhoto(id)) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.notFound().build();
//    }

    @PutMapping("/{id}/metadata")
    public ResponseEntity<PhotoDTO> updatePhotoMetadata(@PathVariable UUID id, @RequestBody PhotoMetadataDTO metadataDTO) {
        Photo updatedPhoto = photoService.updatePhotoMetadata(id, metadataDTO);
        return getPhotoDTOResponseEntity(updatedPhoto);
    }

    @PutMapping("/{id}/star/{nbStar}")
    public ResponseEntity<PhotoDTO> updatePhotoStar(@PathVariable UUID id, @PathVariable @Min(0) @Max(5)  Integer nbStar) {
        Photo updatedPhoto = photoService.updatePhotoStar(id, nbStar);
        return getPhotoDTOResponseEntity(updatedPhoto);
    }

    @PutMapping("/{id}/pick/{valuePick}")
    public ResponseEntity<PhotoDTO> updatePhotoPick(@PathVariable UUID id, @PathVariable @Min(-1) @Max(1)  Integer valuePick) {
        Photo updatedPhoto = photoService.updatePhotoPick(id, valuePick);
        return getPhotoDTOResponseEntity(updatedPhoto);
    }

    private ResponseEntity<PhotoDTO> getPhotoDTOResponseEntity(Photo updatedPhoto) {
        PhotoDTO photoDTO = new PhotoDTO(updatedPhoto);
        try {
            XMPService.storeMetadata(photoDTO, photoDTO.getPath() + ".xmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XMPException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(photoDTO);
    }

}
