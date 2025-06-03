package com.malicia.mrg.assistant.photo.service;


import com.adobe.internal.xmp.*;
import com.adobe.internal.xmp.impl.XMPMetaImpl;
import com.adobe.internal.xmp.impl.XMPSerializerHelper;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.adobe.internal.xmp.properties.XMPProperty;
import com.malicia.mrg.assistant.photo.dto.XMPPhotoDto;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class XMPService {

    public static void storeMetadata(XMPPhotoDto xmpPhotoDto, String xmpPath) throws IOException, XMPException {
        File xmpFile = new File(xmpPath);
        XMPMeta xmpMeta;

        // 1. Charger les métadonnées existantes si le fichier existe
        if (xmpFile.exists()) {
            try (InputStream inputStream = new FileInputStream(xmpFile)) {
                xmpMeta = XMPMetaFactory.parse(inputStream);
            }
        } else {
            xmpMeta = XMPMetaFactory.create();
        }

        // 2. Mettre à jour uniquement les propriétés modifiées ou nouvelles
//        if (xmpPhotoDto.getRating() != null)
            xmpMeta.setProperty(XMPConst.NS_XMP, "Rating", xmpPhotoDto.getRating());

        if (xmpPhotoDto.getLabel() != null)
            xmpMeta.setProperty(XMPConst.NS_XMP, "Label", xmpPhotoDto.getLabel());

        if (xmpPhotoDto.getCreateDate() != null)
            xmpMeta.setProperty(XMPConst.NS_XMP, "CreateDate", xmpPhotoDto.getCreateDate());

        if (xmpPhotoDto.getMake() != null)
            xmpMeta.setProperty(XMPConst.NS_TIFF, "Make", xmpPhotoDto.getMake());

        if (xmpPhotoDto.getModel() != null)
            xmpMeta.setProperty(XMPConst.NS_TIFF, "Model", xmpPhotoDto.getModel());

        if (xmpPhotoDto.getDateTimeOriginal() != null)
            xmpMeta.setProperty(XMPConst.NS_EXIFX, "DateTimeOriginal", xmpPhotoDto.getDateTimeOriginal());

//        if (xmpPhotoDto.getPick() != null)
            xmpMeta.setProperty("http://ns.adobe.com/lightroom/1.0/", "Pick", xmpPhotoDto.getPick());

        // Mots-clés (dc:subject)
        if (xmpPhotoDto.getKeywords() != null && xmpPhotoDto.getKeywords().length > 0) {
            xmpMeta.deleteProperty(XMPConst.NS_DC, "subject"); // Supprimer l'ancien tableau pour éviter doublons
            for (String keyword : xmpPhotoDto.getKeywords()) {
                xmpMeta.appendArrayItem(XMPConst.NS_DC, "subject", new PropertyOptions(PropertyOptions.ARRAY_ORDERED | PropertyOptions.ARRAY_ALTERNATE), keyword, null);
            }
        }

        // 3. Écrire les métadonnées fusionnées dans le fichier
        try (FileOutputStream fileOutputStream = new FileOutputStream(xmpFile)) {
            XMPSerializerHelper.serialize((XMPMetaImpl) xmpMeta, fileOutputStream, null);
        }
    }


    public static XMPPhotoDto readMetadata(String xmpPath) throws IOException, XMPException {
        XMPPhotoDto xmpPhotoDto = new XMPPhotoDto();
        // Read the XMP sidecar file (if exists)
        File xmpFile = new File(xmpPath);
        if (!xmpFile.exists()) {
            System.out.println("XMP sidecar file does not exist.");
            return xmpPhotoDto;
        }

        InputStream inputStream = new FileInputStream(xmpFile);

        // Deserialize the XMP metadata from the file
        XMPMeta xmpMeta = XMPMetaFactory.parse(inputStream);

        // Extract values from the XMP metadata
        xmpPhotoDto.setRating(Integer.parseInt(xmpMeta.getPropertyString(XMPConst.NS_XMP, "Rating")));
        xmpPhotoDto.setCreateDate(String.valueOf(xmpMeta.getPropertyString(XMPConst.NS_XMP, "CreateDate")));

        // TIFF
        xmpPhotoDto.setMake(xmpMeta.getPropertyString(XMPConst.NS_TIFF, "Make"));
        xmpPhotoDto.setModel(xmpMeta.getPropertyString(XMPConst.NS_TIFF, "Model"));

        // EXIF
        xmpPhotoDto.setDateTimeOriginal(xmpMeta.getPropertyString(XMPConst.NS_EXIFX, "DateTimeOriginal"));

        // XMP Core
        xmpPhotoDto.setCreateDate(xmpMeta.getPropertyString(XMPConst.NS_XMP, "CreateDate"));
        String rating = xmpMeta.getPropertyString(XMPConst.NS_XMP, "Rating");
        xmpPhotoDto.setRating(rating != null ? Integer.parseInt(rating) : 0);
        xmpPhotoDto.setLabel(xmpMeta.getPropertyString(XMPConst.NS_XMP, "Label"));

        // Keywords: dc:subject
        int keywordCount = xmpMeta.countArrayItems(XMPConst.NS_DC, "subject");
        String[] tmpKeyWord = new String[keywordCount];
        for (int i = 1; i <= keywordCount; i++) {
            XMPProperty tag = xmpMeta.getArrayItem(XMPConst.NS_DC, "subject", i);
            tmpKeyWord[i - 1] = tag.getValue();
        }
        xmpPhotoDto.setKeywords(tmpKeyWord);

        // Lightroom Pick flag
        String pickStr = xmpMeta.getPropertyString("http://ns.adobe.com/lightroom/1.0/", "Pick");
        xmpPhotoDto.setPick(pickStr != null ? Integer.parseInt(pickStr) : 0);

        return xmpPhotoDto;
    }
}
