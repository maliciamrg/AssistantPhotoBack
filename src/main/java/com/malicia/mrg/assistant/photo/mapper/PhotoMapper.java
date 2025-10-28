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

    // Convert from Entity to DTO
    public static PhotoDTO toDTO(Photo photo) {
        if (photo == null) return null;
        logger.trace("photo: \n" + photo.toString());

        return new PhotoDTO(photo);
    }
}
