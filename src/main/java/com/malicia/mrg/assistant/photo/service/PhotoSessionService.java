package com.malicia.mrg.assistant.photo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.cache.CacheService;
import com.malicia.mrg.assistant.photo.parameter.SeanceType;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.repertoire.Photo;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    private static SeanceRepertoire getSeanceRepertoire(String seanceId, List<SeanceRepertoire> seanceList) {
        Optional<SeanceRepertoire> pathToScan = seanceList.stream()
                .filter(seance -> seance.getId().equals(seanceId)) // Filter by id
                .findFirst(); // Return the first match (Optional)
        if (pathToScan.isEmpty()) {
            throw new IllegalArgumentException("Seance ID '" + seanceId + "' not found in provided seance list.");
        }
        SeanceRepertoire seanceRepertoire = pathToScan.get();
        return seanceRepertoire;
    }

    public List<Photo> getAllPhotoFromPhotoRepertoire(String seanceId, List<SeanceRepertoire> seanceList) {
        List<Photo> cachedPhotos = (List<Photo>) redisTemplate.get(seanceId);
        if (cachedPhotos != null) {
            Long ttlKey = redisTemplate.getExpire(seanceId);
            logger.info("TTL for key '{}' is: {} seconds", seanceId, ttlKey);
            return cachedPhotos;
        }

        SeanceRepertoire seanceRepertoire = getSeanceRepertoire(seanceId, seanceList);

        List<Photo> allPhotoFromPhotoRepertoire = rootRep.getAllPhotoFromPhotoRepertoire(seanceRepertoire);
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

    public HashMap<String, Object> getMetaDataFromPhotoRepertoire(String repertoireName, List<SeanceRepertoire> seanceList) {

        SeanceRepertoire seanceRepertoire = getSeanceRepertoire(repertoireName, seanceList);

        List<Photo> listPhoto = getAllPhotoFromPhotoRepertoire(repertoireName, seanceList);

        String lowerDate = "9999-99-99";
        String upperDate = "0000-00-00";

        int nbRejectedPhoto = 0;
        int nbNotSelectedPhoto = 0;
        int nbSelectedPhoto = 0;
        int nbPhotoTotal = 0;

        int[] nbStar = new int[6];
        Map<String, Integer> nbLabel = new HashMap<>();
        Map<String, Integer> nbTag = new HashMap<>();

        for (Photo photo : listPhoto) {
            nbPhotoTotal++;

            int pick = photo.getPick();
            String label = photo.getLabel();
            String[] tags = photo.getKeywords();
            String exifDate = photo.getExifDate();
            int rating = photo.getRating();

            switch (pick) {
                case -1:
                    nbRejectedPhoto++;
                    break;
                case 0:
                    nbNotSelectedPhoto++;
                    break;
                case 1:
                    nbSelectedPhoto++;
                    break;
            }

            if (pick == -1) {
                nbRejectedPhoto++;
                continue; // exit loop as all other counting are to be done for pick 0 ou 1
            }

            if (rating >= 0 && rating < nbStar.length) {
                nbStar[rating]++;
            }

            nbLabel.merge(label, 1, Integer::sum);
            for (String tag : tags) {
                nbTag.merge(tag, 1, Integer::sum);
            }

            if (exifDate != null && exifDate.compareTo(lowerDate) < 0) {
                lowerDate = exifDate.substring(0, 10);
            }
            if (exifDate != null && exifDate.compareTo(upperDate) > 0) {
                upperDate = exifDate.substring(0, 10);
            }
        }

        String[] parts = repertoireName.split("_");
        // Parse the strings to LocalDate
        LocalDate startDate = LocalDate.parse(lowerDate);
        LocalDate endDate = LocalDate.parse(upperDate);
        // Calculate the number of days between the two dates
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        HashMap<String, Object> metaDataRep = new HashMap<>();
        metaDataRep.put("seanceRepertoire", seanceRepertoire);
        metaDataRep.put("nbPhotoTotal", nbPhotoTotal);
        metaDataRep.put("nbNotSelectedPhoto", nbNotSelectedPhoto);
        metaDataRep.put("nbSelectedPhoto", nbSelectedPhoto);
        metaDataRep.put("nbRejectedPhoto", nbRejectedPhoto);
        metaDataRep.put("lowerDate", lowerDate);
        metaDataRep.put("upperDate", upperDate);
        metaDataRep.put("nbDay", daysBetween + 1);
        metaDataRep.put("nbStar", nbStar);
        metaDataRep.put("nbLabel", nbLabel);
        metaDataRep.put("nbTag", nbTag);
        metaDataRep.put("repertoireName", repertoireName);
        metaDataRep.put("repertoireNameParts", parts);

        //save metaDataRep into file
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(seanceRepertoire.getPath() + File.separator + repertoireName + "_metadata.json"), metaDataRep);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return metaDataRep;

    }
}
