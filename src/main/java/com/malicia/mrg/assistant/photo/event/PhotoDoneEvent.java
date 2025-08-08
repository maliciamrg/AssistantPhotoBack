package com.malicia.mrg.assistant.photo.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class PhotoDoneEvent extends ApplicationEvent {
    private final UUID sessionId;

    public PhotoDoneEvent(Object source, UUID sessionId) {
        super(source);
        this.sessionId = sessionId;
    }

    public UUID getSessionId() {
        return sessionId;
    }
}