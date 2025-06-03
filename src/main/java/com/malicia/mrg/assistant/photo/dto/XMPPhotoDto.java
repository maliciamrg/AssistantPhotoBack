package com.malicia.mrg.assistant.photo.dto;


import java.util.Arrays;

public class XMPPhotoDto {
    private String make;
    private String model;
    private String dateTimeOriginal;
    private String createDate;
    private Integer rating;
    private String label;
    private String[] keywords;
    private Integer pick;

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

    public String getDateTimeOriginal() {
        return dateTimeOriginal;
    }

    public void setDateTimeOriginal(String dateTimeOriginal) {
        this.dateTimeOriginal = dateTimeOriginal;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public Integer getPick() {
        return pick;
    }

    public void setPick(Integer pick) {
        this.pick = pick;
    }

    @Override
    public String toString() {
        return "XMPPhotoDto{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", dateTimeOriginal='" + dateTimeOriginal + '\'' +
                ", createDate='" + createDate + '\'' +
                ", rating=" + rating +
                ", label='" + label + '\'' +
                ", keywords=" + Arrays.toString(keywords) +
                ", pick=" + pick +
                '}';
    }
}
