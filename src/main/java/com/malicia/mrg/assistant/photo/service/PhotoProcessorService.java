package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.event.PhotoDoneEvent;
import com.malicia.mrg.assistant.photo.event.PhotoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class PhotoProcessorService {

    private final Map<UUID, List<PhotoDTO>> photoBuffers = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<List<PhotoDTO>>> photoFutures = new ConcurrentHashMap<>();

    private final PhotoEmitterService photoEmitterService;

    public PhotoProcessorService(PhotoEmitterService photoEmitterService) {
        this.photoEmitterService = photoEmitterService;
    }

    public List<PhotoDTO> startProcessing(UUID sessionId, String rootDir, List<Path> paths) {
        photoBuffers.put(sessionId, new ArrayList<>());
        CompletableFuture<List<PhotoDTO>> future = new CompletableFuture<>();
        photoFutures.put(sessionId, future);

        // Trigger threaded processing
        photoEmitterService.startEmittingPhotos(sessionId, rootDir ,paths);

        try {
            // Wait until the thread completes and done event is fired
            return future.get(180, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Photo processing timeout or error", e);
        } finally {
            // Clean up
            photoBuffers.remove(sessionId);
            photoFutures.remove(sessionId);
        }
    }

    @EventListener
    public void onPhotoEvent(PhotoEvent event) {
        photoBuffers.computeIfAbsent(event.getSessionId(), id -> new ArrayList<>())
                .add(event.getPhoto());
    }

    @EventListener
    public void onPhotoDone(PhotoDoneEvent event) {
        UUID sessionId = event.getSessionId();
        List<PhotoDTO> result = photoBuffers.getOrDefault(sessionId, Collections.emptyList());

        CompletableFuture<List<PhotoDTO>> future = photoFutures.get(sessionId);
        if (future != null) {
            future.complete(result); // Unblocks the waiting method
        }
    }
}
