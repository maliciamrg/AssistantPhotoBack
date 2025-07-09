package com.malicia.mrg.assistant.photo.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "photo_metadata")
public class PhotoMetadata {

    @Id
    @GeneratedValue
    private UUID id;

    private int rating;
    private String label;
    private String createDate;
    private int pick;

    @Override
    public String toString() {
        return "PhotoMetadata{" +
                "id=" + id +
                ", rating=" + rating +
                ", label='" + label + '\'' +
                ", createDate='" + createDate + '\'' +
                ", pick=" + pick +
                ", keywords=" + keywords +
                ", photo=" + photo +
                '}';
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "photo_keywords", joinColumns = @JoinColumn(name = "photo_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @OneToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;


    public int getPick() {
        return pick;
    }

    public void setPick(int pick) {
        this.pick = pick;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public <E> void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    // Getters/setters
}
