package com.malicia.mrg.assistant.photo.event;

import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class PhotoEvent extends ApplicationEvent {
    private final UUID sessionId;
    private final PhotoDTO photo;

    public PhotoEvent(Object source, UUID sessionId, PhotoDTO photo) {
        super(source);
        this.sessionId = sessionId;
        this.photo = photo;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public PhotoDTO getPhoto() {
        return photo;
    }
}
