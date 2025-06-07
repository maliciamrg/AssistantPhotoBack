package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.cache.CacheService;
import com.malicia.mrg.assistant.photo.parameter.SeanceType;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoSessionService {

    private static final Logger logger = LoggerFactory.getLogger(PhotoSessionService.class);

    private final CacheService redisTemplate;
    private final RootRepertoire rootRep;
    private final MyConfig config;

    private final Duration ttl = Duration.ofMinutes(1);

    public PhotoSessionService(CacheService redisTemplate, RootRepertoire rootRep, MyConfig config) {
        this.redisTemplate = redisTemplate;
        this.rootRep = rootRep;
        this.config = config;
    }

    public List<Photo> getAllPhotoFromPhotoRepertoire(String seanceId, List<SeanceRepertoire> seanceList) {
        List<Photo> cachedPhotos = (List<Photo>) redisTemplate.get(seanceId);
        if (cachedPhotos != null) {
            Long ttlKey = redisTemplate.getExpire(seanceId);
            logger.info("TTL for key '{}' is: {} seconds", seanceId, ttlKey);
            return cachedPhotos;
        }
        Optional<SeanceRepertoire> pathToScan = seanceList.stream()
                .filter(seance -> seance.getId().equals(seanceId)) // Filter by id
                .findFirst(); // Return the first match (Optional)

        if (pathToScan.isEmpty()) {
            throw new IllegalArgumentException("Seance ID '" + seanceId + "' not found in provided seance list.");
        }

        List<Photo> allPhotoFromPhotoRepertoire = rootRep.getAllPhotoFromPhotoRepertoire(pathToScan.get());
        redisTemplate.set(seanceId, allPhotoFromPhotoRepertoire, ttl);
        logger.info("redisTemplate.opsForValue().set : {} ", seanceId);
        return allPhotoFromPhotoRepertoire;
    }

    public List<SeanceRepertoire> getSeanceRepertoireList(String typeName) {
        List<SeanceRepertoire> cachedSeances = (List<SeanceRepertoire>) redisTemplate.get(typeName);
        if (cachedSeances != null) {
            Long ttlKey = redisTemplate.getExpire(typeName);
            logger.info("TTL for key '{}' is: {} seconds", typeName, ttlKey);
            return cachedSeances;
        }
        Optional<SeanceTypeEnum> type = config.getSeanceType().stream()
                .map(SeanceType::getNom)
                .filter(seanceTypeEnum -> seanceTypeEnum.toString().equals(typeName))
                .findFirst();
        if (type.isPresent()) {
            List<SeanceRepertoire> seanceList = rootRep.getAllSeanceRepertoire(type.get());
            redisTemplate.set(typeName, seanceList, ttl);
            logger.info("redisTemplate.opsForValue().set :{}", typeName);
            return seanceList;
        } else {
            return Collections.emptyList();
        }
    }

}
