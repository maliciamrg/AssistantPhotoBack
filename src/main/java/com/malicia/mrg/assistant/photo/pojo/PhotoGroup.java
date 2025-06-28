package com.malicia.mrg.assistant.photo.pojo;

import com.malicia.mrg.assistant.photo.dto.PhotoData;
import com.malicia.mrg.assistant.photo.entity.Photo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhotoGroup implements Iterable<PhotoData>, Serializable {

    private List<PhotoData> photos = new ArrayList();

    public PhotoGroup(List<PhotoData> photos) {
        this.photos = photos;
    }

    public PhotoGroup() {
    }

    public List<PhotoData> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoData> photos) {
        this.photos = photos;
    }

    public void add(Photo photo) {

        photos.add(new PhotoData(photo.getId(),photo.getHash(),photo.getPath(),photo.getRelativeToPath(),photo.getFilename(),photo.getExtension(),photo.getCreatedDate(),photo.getExifDate(),photo.getDateTaken(),photo.getRating(),photo.getLabel(),photo.getPick(),photo.getKeywords()));
    }

    public boolean empty() {
        return photos.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        photos.forEach(photo -> result.append(photo.toString()).append("\n"));
        return result.toString();

    }

    public int size() {
        return photos.size();
    }

    public void addAll(PhotoGroup group) {
        photos.addAll(group.photos);
    }

    @Override
    public Iterator<PhotoData> iterator() {
        return photos.iterator();
    }

}
