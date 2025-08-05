package com.malicia.mrg.assistant.photo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "photo_keywords", joinColumns = @JoinColumn(name = "photo_id"))
    @Column(name = "keyword")
    private List<String> keywords;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Photo photo;

    @Override
    public String toString() {
        return "PhotoMetadata{" +
                "id=" + id +
                ", rating=" + rating +
                ", label='" + label + '\'' +
                ", createDate='" + createDate + '\'' +
                ", pick=" + pick +
                ", keywords=" + keywords +
                ", photoId=" + (photo != null ? photo.getId() : null) +
                '}';
    }

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

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public <E> void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    // Getters/setters
}
