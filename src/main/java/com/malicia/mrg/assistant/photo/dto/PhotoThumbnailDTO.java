package com.malicia.mrg.assistant.photo.dto;

import java.io.Serializable;

public class PhotoThumbnailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private byte[] data;

    public PhotoThumbnailDTO() {}

    public PhotoThumbnailDTO(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
