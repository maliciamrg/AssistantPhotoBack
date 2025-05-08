package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoSessionService {

    private static final Logger logger = LoggerFactory.getLogger(PhotoSessionService.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final RootRepertoire rootRep;
    private final MyConfig config;

    private Duration ttl = Duration.ofMinutes(1);

    public PhotoSessionService(RedisTemplate<String, Object> redisTemplate, RootRepertoire rootRep, MyConfig config) {
        this.redisTemplate = redisTemplate;
        this.rootRep = rootRep;
        this.config = config;
    }

    public List<Photo> getAllPhotoFromPhotoRepertoire(String seanceId, List<SeanceRepertoire> seanceList) {
        List<Photo> cachedPhotos = (List<Photo>) redisTemplate.opsForValue().get(seanceId);
        if (cachedPhotos != null) {
            Long ttl = redisTemplate.getExpire(seanceId);
            logger.info("TTL for key '{}' is: {} seconds", seanceId, ttl);
            return cachedPhotos;
        }
        Optional<SeanceRepertoire> pathToScan = seanceList.stream()
                .filter(seance -> seance.getId().equals(seanceId)) // Filter by id
                .findFirst(); // Return the first match (Optional)
        List<Photo> allPhotoFromPhotoRepertoire = rootRep.getAllPhotoFromPhotoRepertoire(pathToScan.get());
        redisTemplate.opsForValue().set(seanceId, allPhotoFromPhotoRepertoire,ttl);
        logger.info("redisTemplate.opsForValue().set :" + seanceId);
        return allPhotoFromPhotoRepertoire;
    }

    public List<SeanceRepertoire> getSeanceRepertoireList(String typeName) {
        List<SeanceRepertoire> cachedSeances = (List<SeanceRepertoire>) redisTemplate.opsForValue().get(typeName);
        if (cachedSeances != null) {
            Long ttl = redisTemplate.getExpire(typeName);
            logger.info("TTL for key '{}' is: {} seconds", typeName, ttl);
            return cachedSeances;
        }
        Optional<SeanceTypeEnum> type = config.getSeanceType().stream()
                .map(seanceType -> seanceType.getNom())
                .filter(seanceTypeEnum -> seanceTypeEnum.toString().equals(typeName))
                .findFirst();
        List<SeanceRepertoire> seanceList = rootRep.getAllSeanceRepertoire(type.get());
        redisTemplate.opsForValue().set(typeName, seanceList,ttl);
        logger.info("redisTemplate.opsForValue().set :" + typeName);
        return seanceList;
    }

}
