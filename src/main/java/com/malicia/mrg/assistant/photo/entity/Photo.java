package com.malicia.mrg.assistant.photo.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "photos")
public class Photo implements Serializable {
    public static final String T_09_15_00_Z = "2024-03-12T09:15:00Z";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private UUID id;

    private String hash;

    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private PhotoFileSystem fileSystem;

    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private PhotoExifData exif;
    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private PhotoMetadata photoMetadata;
    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private PhotoThumbnail thumbnail;

    public Photo() {
    }

    public PhotoMetadata getPhotoMetadata() {
        return photoMetadata;
    }

    public void setPhotoMetadata(PhotoMetadata photoMetadata) {
        this.photoMetadata = photoMetadata;
    }

    public PhotoFileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(PhotoFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public PhotoExifData getExif() {
        return exif;
    }

    public void setExif(PhotoExifData exif) {
        this.exif = exif;
    }

    public PhotoThumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(PhotoThumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", fileSystem=" + fileSystem +
                ", exif=" + exif +
                ", photoMetadata=" + photoMetadata +
         //       ", thumbnail=" + thumbnail +
                '}';
    }
}
