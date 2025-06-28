package com.malicia.mrg.assistant.photo.cache;

public interface CacheService {
    void set(String key, Object value);
    Object get(String key);
    Long getExpire(String typeName);
}
