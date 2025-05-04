package com.malicia.mrg.assistant.photo.repertoire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroupOfPhotos implements Iterable<Photo> {

    private List<Photo> photos = new ArrayList();

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void add(Photo photo) {
        photos.add(photo);
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

    public void addAll(GroupOfPhotos group) {
        photos.addAll(group.photos);
    }

    @Override
    public Iterator<Photo> iterator() {
        return photos.iterator();
    }

}
