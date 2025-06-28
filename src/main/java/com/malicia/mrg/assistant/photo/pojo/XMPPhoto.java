package com.malicia.mrg.assistant.photo.pojo;


import java.util.Arrays;
import java.util.List;

public class XMPPhoto {
    private String make;
    private String model;
    private String dateTimeOriginal;
    private String createDate;
    private Integer rating;
    private String label;
    private List<String> keywords;
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

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
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
        return "XMPPhoto{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", dateTimeOriginal='" + dateTimeOriginal + '\'' +
                ", createDate='" + createDate + '\'' +
                ", rating=" + rating +
                ", label='" + label + '\'' +
                ", keywords=" + keywords.toString() +
                ", pick=" + pick +
                '}';
    }
}
