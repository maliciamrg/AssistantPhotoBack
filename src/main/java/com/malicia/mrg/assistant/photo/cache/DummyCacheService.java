package com.malicia.mrg.assistant.photo.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "redis.enabled", havingValue = "false", matchIfMissing = false)
public class DummyCacheService implements CacheService {

    public void set(String key, Object value) { // default implementation ignored
    }

    public Object get(String key) {
        return null;
    }

    public Long getExpire(String key) {
        return 0l;
    }
}
