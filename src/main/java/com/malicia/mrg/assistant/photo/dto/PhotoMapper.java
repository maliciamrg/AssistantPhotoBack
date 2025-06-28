package com.malicia.mrg.assistant.photo.dto;

import com.malicia.mrg.assistant.photo.entity.Photo;

import java.util.ArrayList;

public class PhotoMapper {

    public static PhotoDTO toDto(Photo photo) {
        if (photo == null) return null;

        return new PhotoDTO(
                photo.getId(),
                photo.getHash(),
                photo.getPath(),
                photo.getRelativeToPath(),
                photo.getFilename(),
                photo.getExtension(),
                photo.getCreatedDate(),
                photo.getExifDate(),
                photo.getDateTaken(),
                photo.getRating(),
                photo.getLabel(),
                photo.getPick(),
                new ArrayList<>(photo.getKeywords())
        );
    }

}
