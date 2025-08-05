package com.malicia.mrg.assistant.photo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "photo_exif")
public class PhotoExifData {

    @Id
    @GeneratedValue
    private UUID id;

    private String dateTimeOriginal;
    private String make;
    private String model;
    private String iso;
    private String focalLength;
    private String aperture;
    private String exposureTime;
    private String orientation;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Photo photo;

    @Override
    public String toString() {
        return "PhotoExifData{" +
                "id=" + id +
                ", dateTimeOriginal='" + dateTimeOriginal + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", iso='" + iso + '\'' +
                ", focalLength='" + focalLength + '\'' +
                ", aperture='" + aperture + '\'' +
                ", exposureTime='" + exposureTime + '\'' +
                ", orientation='" + orientation + '\'' +
                ", photoId=" + (photo != null ? photo.getId() : null) +
                '}';
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getDateTimeOriginal() {
        return dateTimeOriginal;
    }

    public void setDateTimeOriginal(String dateTimeOriginal) {
        this.dateTimeOriginal = dateTimeOriginal;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    // Getters/setters
}
