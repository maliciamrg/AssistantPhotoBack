package com.malicia.mrg.assistant.photo.pojo;

import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.entity.Photo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhotoGroup implements Iterable<PhotoDTO>, Serializable {

    private List<PhotoDTO> photos = new ArrayList();

    public PhotoGroup(List<PhotoDTO> photos) {
        this.photos = photos;
    }

    public PhotoGroup() {
    }

    public List<PhotoDTO> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoDTO> photos) {
        this.photos = photos;
    }

    public void add(Photo photo) {
        photos.add(new PhotoDTO(photo.getId(),
                photo.getHash(),
                photo.getFileSystem().getPath(),
                photo.getFileSystem().getRelativeToPath(),
                photo.getFileSystem().getFilename(),
                photo.getFileSystem().getExtension(),
                photo.getFileSystem().getCreatedDate(),
                photo.getExif().getDateTimeOriginal(),
                photo.getPhotoMetadata().getCreateDate(),
                photo.getPhotoMetadata().getRating(),
                photo.getPhotoMetadata().getLabel(),
                photo.getPhotoMetadata().getPick(),
                new ArrayList<>(photo.getPhotoMetadata().getKeywords())
        ));
    }

    public void add(PhotoDTO photoDTO) {
        photos.add(photoDTO);
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
    public Iterator<PhotoDTO> iterator() {
        return photos.iterator();
    }

}
