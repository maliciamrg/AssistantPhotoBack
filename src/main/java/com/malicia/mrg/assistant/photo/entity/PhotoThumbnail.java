package com.malicia.mrg.assistant.photo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Table(name = "photo_thumbnail")
public class PhotoThumbnail implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private UUID id;  // Will use UUID.randomUUID() or Hibernate strategy
    private byte[] data;
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Photo photo;

    public PhotoThumbnail() {
    }

    // Constructors

    public PhotoThumbnail(Photo photo, byte[] data) {
        this.photo = photo;
        this.data = data;
    }

    @Override
    public String toString() {
        return "PhotoThumbnail{" +
                "id=" + id +
                ", data=" + Arrays.toString(data) +
                ", photoId=" + (photo != null ? photo.getId() : null) +
                '}';
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
