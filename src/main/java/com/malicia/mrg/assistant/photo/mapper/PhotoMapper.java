package com.malicia.mrg.assistant.photo.mapper;

import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.entity.Photo;
import com.malicia.mrg.assistant.photo.service.ThumbnailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

public class PhotoMapper {
    private static final Logger logger = LoggerFactory.getLogger(PhotoMapper.class);

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
    public static PhotoDTO toDTO(Photo photo) {
        if (photo == null) return null;
        logger.debug("photo: \n" + photo.toString());

        return new PhotoDTO(photo);
    }
}
