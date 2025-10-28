package com.malicia.mrg.assistant.photo.service;


import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.adobe.internal.xmp.impl.XMPMetaImpl;
import com.adobe.internal.xmp.impl.XMPSerializerHelper;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.adobe.internal.xmp.properties.XMPProperty;
import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.dto.PhotoMetadataDTO;
import com.malicia.mrg.assistant.photo.entity.PhotoMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class XMPService {
    private static final Logger logger = LoggerFactory.getLogger(XMPService.class);

    public static void storeMetadata(PhotoDTO xmpPhoto, String xmpPath) throws IOException, XMPException {
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

        XMPMetaFactory.getSchemaRegistry().registerNamespace(
                "http://ns.adobe.com/lightroom/1.0/",
                "lr"
        );

        // 2. Mettre à jour uniquement les propriétés modifiées ou nouvelles
        xmpMeta.setProperty(XMPConst.NS_XMP, "Rating", xmpPhoto.getRating());

        if (xmpPhoto.getLabel() != null)
            xmpMeta.setProperty(XMPConst.NS_XMP, "Label", xmpPhoto.getLabel());

        if (xmpPhoto.getTakenDate() != null)
            xmpMeta.setProperty(XMPConst.NS_XMP, "TakenDate", xmpPhoto.getTakenDate());

//        if (xmpPhoto.getMake() != null)
//            xmpMeta.setProperty(XMPConst.NS_TIFF, "Make", xmpPhoto.getMake());

//        if (xmpPhoto.getModel() != null)
//            xmpMeta.setProperty(XMPConst.NS_TIFF, "Model", xmpPhoto.getModel());

//        if (xmpPhoto.getDateTimeOriginal() != null)
//            xmpMeta.setProperty(XMPConst.NS_EXIFX, "DateTimeOriginal", xmpPhoto.getDateTimeOriginal());

        xmpMeta.setProperty("http://ns.adobe.com/lightroom/1.0/", "Pick", xmpPhoto.getPick());

        // Mots-clés (dc:subject)
        if (xmpPhoto.getKeywords() != null && xmpPhoto.getKeywords().size() > 0) {
            xmpMeta.deleteProperty(XMPConst.NS_DC, "subject"); // Supprimer l'ancien tableau pour éviter doublons
            for (String keyword : xmpPhoto.getKeywords()) {
                xmpMeta.appendArrayItem(XMPConst.NS_DC, "subject", new PropertyOptions(PropertyOptions.ARRAY_ORDERED | PropertyOptions.ARRAY_ALTERNATE), keyword, null);
            }
        }

        // 3. Écrire les métadonnées fusionnées dans le fichier
        try (FileOutputStream fileOutputStream = new FileOutputStream(xmpFile)) {
            XMPSerializerHelper.serialize((XMPMetaImpl) xmpMeta, fileOutputStream, null);
        }
    }

    public static PhotoMetadata readMetadata(String xmpPath) throws IOException, XMPException {
        PhotoMetadata photoMetadata = new PhotoMetadata();
        File xmpFile = new File(xmpPath);

        if (!xmpFile.exists()) {
            logger.debug("XMP sidecar file does not exist.");
            return photoMetadata;
        }

        try (InputStream inputStream = new FileInputStream(xmpFile)) {
            XMPMeta xmpMeta = XMPMetaFactory.parse(inputStream);

            // XMP Core
            if (xmpMeta.doesPropertyExist(XMPConst.NS_XMP, "TakenDate")) {
                photoMetadata.setTakenDate(xmpMeta.getPropertyString(XMPConst.NS_XMP, "TakenDate"));
            }

            if (xmpMeta.doesPropertyExist(XMPConst.NS_XMP, "Rating")) {
                String ratingStr = xmpMeta.getPropertyString(XMPConst.NS_XMP, "Rating");
                photoMetadata.setRating(ratingStr != null ? Integer.parseInt(ratingStr) : 0);
            } else {
                photoMetadata.setRating(0);
            }

            if (xmpMeta.doesPropertyExist(XMPConst.NS_XMP, "Label")) {
                photoMetadata.setLabel(xmpMeta.getPropertyString(XMPConst.NS_XMP, "Label"));
            }

            // TIFF
//            if (xmpMeta.doesPropertyExist(XMPConst.NS_TIFF, "Make")) {
//                photoMetadata.setMake(xmpMeta.getPropertyString(XMPConst.NS_TIFF, "Make"));
//            }

//            if (xmpMeta.doesPropertyExist(XMPConst.NS_TIFF, "Model")) {
//                photoMetadata.setModel(xmpMeta.getPropertyString(XMPConst.NS_TIFF, "Model"));
//            }

            // EXIF
//            if (xmpMeta.doesPropertyExist(XMPConst.NS_EXIFX, "DateTimeOriginal")) {
//                photoMetadata.setDateTimeOriginal(xmpMeta.getPropertyString(XMPConst.NS_EXIFX, "DateTimeOriginal"));
//            }

            // Keywords (dc:subject array)
            if (xmpMeta.doesPropertyExist(XMPConst.NS_DC, "subject")) {
                int keywordCount = xmpMeta.countArrayItems(XMPConst.NS_DC, "subject");
                List<String> tmpKeyWord = new ArrayList<>();
                for (int i = 1; i <= keywordCount; i++) {
                    XMPProperty tag = xmpMeta.getArrayItem(XMPConst.NS_DC, "subject", i);
                    if (tag != null) {
                        tmpKeyWord.add(tag.getValue());
                    }
                }
                photoMetadata.setKeywords(tmpKeyWord);
            }

            // Lightroom Pick flag
            final String LIGHTROOM_NS = "http://ns.adobe.com/lightroom/1.0/";
            if (xmpMeta.doesPropertyExist(LIGHTROOM_NS, "Pick")) {
                String pickStr = xmpMeta.getPropertyString(LIGHTROOM_NS, "Pick");
                photoMetadata.setPick(pickStr != null ? Integer.parseInt(pickStr) : 0);
            } else {
                photoMetadata.setPick(0);
            }
        } catch (XMPException | NumberFormatException e) {
            System.err.println("Error reading or parsing XMP metadata: " + e.getMessage());
            // Optionally, rethrow or log as needed
        }

        return photoMetadata;
    }

}
