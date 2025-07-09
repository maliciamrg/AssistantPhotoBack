package com.malicia.mrg.assistant.photo.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;


@Configuration
@EnableCaching
public class RedisConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${application.redis.prefix}")
    private String prefix;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        logger.info("RedisCacheManager.cacheManager");
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Cache expiration time
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> prefix + cacheName + "::")
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        logger.debug("RedisCacheManager Ttl {} , prefix {}", redisCacheConfiguration.getTtl(), redisCacheConfiguration.getKeyPrefix());
        return redisCacheConfiguration;
    }
}
