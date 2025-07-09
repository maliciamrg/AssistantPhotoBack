package com.malicia.mrg.assistant.photo.mapper;

import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.entity.Photo;

import java.util.ArrayList;
import java.util.Collections;

public class PhotoMapper {

    // Convert from DTO to Entity
    public static Photo toEntity(PhotoDTO dto) {
        if (dto == null) return null;

        Photo entity = new Photo();
        entity.setId(dto.getId());
        entity.setHash(dto.getHash());
        entity.getFileSystem().setPath(dto.getPath());
        entity.getFileSystem().setRelativeToPath(dto.getRelativeToPath());
        entity.getFileSystem().setFilename(dto.getFilename());
        entity.getFileSystem().setExtension(dto.getExtension());
        entity.getFileSystem().setCreatedDate(dto.getCreatedDate());
        entity.getExif().setDateTimeOriginal(dto.getExifDate());
        entity.getPhotoMetadata().setCreateDate(dto.getCreateDate());
        entity.getPhotoMetadata().setRating(dto.getRating());
        entity.getPhotoMetadata().setLabel(dto.getLabel());
        entity.getPhotoMetadata().setPick(dto.getPick());
        entity.getPhotoMetadata().setKeywords(dto.getKeywords() != null ? new ArrayList<>(dto.getKeywords()) : new ArrayList<>());
        return entity;
    }

    // Convert from Entity to DTO
    public static PhotoDTO toDTO(Photo entity) {
        if (entity == null) return null;
        System.out.println("photo: \n" + entity.toString());

        return new PhotoDTO(
                entity.getId(),
                entity.getHash(),
                entity.getFileSystem().getPath(),
                entity.getFileSystem().getRelativeToPath(),
                entity.getFileSystem().getFilename(),
                entity.getFileSystem().getExtension(),
                entity.getFileSystem().getCreatedDate(),
                entity.getExif().getDateTimeOriginal(),
                entity.getPhotoMetadata().getCreateDate(),
                entity.getPhotoMetadata().getRating(),
                entity.getPhotoMetadata().getLabel(),
                entity.getPhotoMetadata().getPick(),
                entity.getPhotoMetadata() != null && entity.getPhotoMetadata().getKeywords() != null
                        ? new ArrayList<>(entity.getPhotoMetadata().getKeywords())
                        : Collections.emptyList()
        );
    }
}
