package com.malicia.mrg.assistant.photo.service;


import com.adobe.internal.xmp.*;
import com.adobe.internal.xmp.impl.XMPMetaImpl;
import com.adobe.internal.xmp.impl.XMPSerializerHelper;
import com.malicia.mrg.assistant.photo.DTO.XMPPhotoDto;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class XMPService {

    public static void storeMetadata(XMPPhotoDto xmpPhotoDto, String xmpPath) throws IOException, XMPException {
        // Create the XMPMeta object
        XMPMeta xmpMeta = XMPMetaFactory.create();
        // Add custom fields to XMP (Label and Rating)
        xmpMeta.setProperty(XMPConst.NS_XMP, "Rating", xmpPhotoDto.getRating());

        // Write XMP metadata to file
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(xmpPath))) {
            XMPSerializerHelper xmpSerializerHelper = new XMPSerializerHelper();
            xmpSerializerHelper.serialize((XMPMetaImpl)xmpMeta, fileOutputStream,null);
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

        return xmpPhotoDto;
    }

}
