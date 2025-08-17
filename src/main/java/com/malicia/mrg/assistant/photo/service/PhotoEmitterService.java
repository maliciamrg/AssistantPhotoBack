package com.malicia.mrg.assistant.photo.service;

import com.adobe.internal.xmp.XMPException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.entity.*;
import com.malicia.mrg.assistant.photo.event.PhotoDoneEvent;
import com.malicia.mrg.assistant.photo.event.PhotoEvent;
import com.malicia.mrg.assistant.photo.mapper.PhotoMapper;
import com.malicia.mrg.assistant.photo.repository.PhotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PhotoEmitterService {
    private static final Logger logger = LoggerFactory.getLogger(PhotoEmitterService.class);
    private final ApplicationEventPublisher publisher;
    private final PhotoRepository photoRepository;
    private final ThumbnailService thumbnailService;
    private final PhotoService photoService;
    private final ExecutorService photoEmitterExecutor = Executors.newFixedThreadPool(10);

    public PhotoEmitterService(ApplicationEventPublisher publisher, PhotoRepository photoRepository, ThumbnailService thumbnailService, PhotoService photoService) {
        this.publisher = publisher;
        this.photoRepository = photoRepository;
        this.thumbnailService = thumbnailService;
        this.photoService = photoService;
    }

    public void startEmittingPhotos(UUID sessionId, String rootDir, List<Path> paths) {
        int totalPaths = paths.size();
        CountDownLatch latch = new CountDownLatch(totalPaths);

        for (Path path : paths) {
            photoEmitterExecutor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.debug("[{}] Start emitting photo for path: {}", threadName, path);
                try {
                    PhotoDTO photo = photoService.getPhotoDataFromPath(rootDir, path);
                    publisher.publishEvent(new PhotoEvent(this, sessionId, photo));
                    logger.trace("[{}] PhotoEvent published for: {}", threadName, photo.getId());
                    Thread.sleep(50);
                } catch (Exception e) {
                    logger.error("[{}] Error processing path {}: {}", threadName, path, e.getMessage(), e);
                } finally {
                    latch.countDown(); // Mark this path as done
                    logger.info("[{}] Finished processing path: {} (Remaining: {})", threadName, path, latch.getCount());
                }
            });
        }

        // Wait for all threads to complete in a separate task
        photoEmitterExecutor.submit(() -> {
            try {
                latch.await(); // Wait until all tasks have counted down
                publisher.publishEvent(new PhotoDoneEvent(this, sessionId));
                logger.info("All {} photo events processed. Done event published for session: {}",totalPaths, sessionId);
            } catch (InterruptedException e) {
                logger.warn("Thread waiting for latch interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        });
    }

}
