package com.malicia.mrg.assistant.photo.service;

import com.adobe.internal.xmp.XMPException;
import com.malicia.mrg.assistant.photo.pojo.XMPPhoto;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.malicia.mrg.assistant.photo.file.WorkWithFile.generateThumbnail;

@Service
public class PhotoService {

    public List<Photo> saveAllPhotos(List<Photo> photos, boolean writeXmp) {
        for (Photo photo : photos) {

            generateThumbnail(photo);

            if (writeXmp) {
                try {
                    XMPPhoto xmpPhoto = new XMPPhoto();
                    xmpPhoto.setRating(photo.getRating());
                    xmpPhoto.setPick(photo.getPick());
                    xmpPhoto.setCreateDate(photo.getCreatedDate());
                    xmpPhoto.setKeywords(photo.getKeywords());
                    XMPService.storeMetadata(xmpPhoto,photo.getPath()+".xmp");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (XMPException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return photos;
    }
}
