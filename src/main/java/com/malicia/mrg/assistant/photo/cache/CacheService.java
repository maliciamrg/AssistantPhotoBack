package com.malicia.mrg.assistant.photo.cache;

import java.time.Duration;

public interface CacheService {
    void set(String key, Object value, Duration ttl);
    Object get(String key);
    Long getExpire(String typeName);
}
