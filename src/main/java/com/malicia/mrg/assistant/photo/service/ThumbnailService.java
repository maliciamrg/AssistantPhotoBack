package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.controller.PhotoshootController;
import com.malicia.mrg.assistant.photo.entity.Photo;
import com.malicia.mrg.assistant.photo.entity.PhotoThumbnail;
import com.malicia.mrg.assistant.photo.repository.PhotoThumbnailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ThumbnailService {
    private final PhotoThumbnailRepository photoThumbnailRepository;
    private static final Logger logger = LoggerFactory.getLogger(ThumbnailService.class);

    public ThumbnailService(PhotoThumbnailRepository photoThumbnailRepository) {
        this.photoThumbnailRepository = photoThumbnailRepository;
    }

    public PhotoThumbnail getThumbnail(String photoUUID) {

        Optional<PhotoThumbnail> existingPhotoThumbnail = photoThumbnailRepository.findByPhotoId(UUID.fromString(photoUUID));
        if (existingPhotoThumbnail.isPresent()) {
            return existingPhotoThumbnail.get();
        }
        return new PhotoThumbnail();
    }

    public PhotoThumbnail generateThumbnail(Photo photo) {
        PhotoThumbnail photoThumbnail = new PhotoThumbnail();

        try {

            // Load the image file
            BufferedImage originalImage = FileSystemService.getBufferedImage(photo.getFileSystem().getPath());

            if (originalImage != null) {
                // Set the dimensions for the thumbnail
                int thumbnailWidth = originalImage.getWidth() / 5; // Set desired thumbnail width
                int thumbnailHeight = originalImage.getHeight() / 5; // Set desired thumbnail height

                // Create a scaled instance (thumbnail)
                Image thumbnail = originalImage.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_SMOOTH);

                // Optionally, you can create a BufferedImage for the thumbnail if you need it in BufferedImage format
                BufferedImage bufferedThumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_ARGB);
                bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);

                // Convert BufferedImage to byte array
                byte[] thumbnailBytes = imageToByteArray(bufferedThumbnail, "PNG");
                logger.debug("Thumbnail size: " + thumbnailBytes.length);
                logger.debug("First byte: " + thumbnailBytes[0]);
                photoThumbnail.setData(thumbnailBytes);

            }

        } catch (IOException e) {
            logger.debug("Error reading the image file: " + e.getMessage());
        }
        return photoThumbnail;
    }

    // Utility method to convert a BufferedImage to a byte array
    private byte[] imageToByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, byteArrayOutputStream);  // Write the image to the byte array output stream
        return byteArrayOutputStream.toByteArray();  // Return the byte array
    }

}
