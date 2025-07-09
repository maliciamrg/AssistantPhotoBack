package com.malicia.mrg.assistant.photo.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Table(name = "photo_thumbnail")
public class PhotoThumbnail implements Serializable {
    @Override
    public String toString() {
        return "PhotoThumbnail{" +
                "id=" + id +
                ", data=" + Arrays.toString(data) +
                ", photo=" + photo +
                '}';
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private UUID id;  // Will use UUID.randomUUID() or Hibernate strategy

    private byte[] data;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id", nullable = false, unique = true)
    private Photo photo;

    // Constructors

    public PhotoThumbnail() {}

    public PhotoThumbnail(Photo photo, byte[] data) {
        this.photo = photo;
        this.data = data;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
