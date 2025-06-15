package com.malicia.mrg.assistant.photo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.cache.CacheService;
import com.malicia.mrg.assistant.photo.parameter.SeanceType;
import com.malicia.mrg.assistant.photo.parameter.SeanceTypeEnum;
import com.malicia.mrg.assistant.photo.pojo.MetaDataRep;
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
        return pathToScan.get();
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

    public MetaDataRep getMetaDataFromPhotoRepertoire(String repertoireName, List<SeanceRepertoire> seanceList) {

        SeanceRepertoire seanceRepertoire = getSeanceRepertoire(repertoireName, seanceList);
        List<Photo> listPhoto = getAllPhotoFromPhotoRepertoire(repertoireName, seanceList);

        MetaDataAccumulator accumulator = new MetaDataAccumulator();

        for (Photo photo : listPhoto) {
            accumulator.accumulate(photo);
        }

        String[] parts = repertoireName.split("_");
        long daysBetween = computeDaysBetween(accumulator.getLowerDate(), accumulator.getUpperDate());

        MetaDataRep metaDataRep = buildMetaDataRep(seanceRepertoire, repertoireName, parts, accumulator, daysBetween);
        writeMetaDataToFile(metaDataRep, seanceRepertoire.getPath(), repertoireName);

        return metaDataRep;
    }
    private long computeDaysBetween(String lowerDate, String upperDate) {
        LocalDate start = LocalDate.parse(lowerDate);
        LocalDate end = LocalDate.parse(upperDate);
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    private MetaDataRep buildMetaDataRep(SeanceRepertoire seanceRepertoire, String name, String[] parts, MetaDataAccumulator acc, long nbDays) {
        MetaDataRep rep = new MetaDataRep();
        rep.setSeanceRepertoire(seanceRepertoire);
        rep.setRepertoireName(name);
        rep.setRepertoireNameParts(parts);
        rep.setLowerDate(acc.getLowerDate());
        rep.setUpperDate(acc.getUpperDate());
        rep.setNbDay(nbDays);
        rep.setNbPhotoTotal(acc.getNbPhotoTotal());
        rep.setNbNotSelectedPhoto(acc.getNbNotSelectedPhoto());
        rep.setNbSelectedPhoto(acc.getNbSelectedPhoto());
        rep.setNbRejectedPhoto(acc.getNbRejectedPhoto());
        rep.setNbStar(acc.getNbStar());
        rep.setNbLabel(acc.getNbLabel());
        rep.setNbTag(acc.getNbTag());
        return rep;
    }

    private void writeMetaDataToFile(MetaDataRep metaData, String path, String name) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(path + File.separator + name + "_metadata.json");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, metaData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class MetaDataAccumulator {
        private String lowerDate = "9999-99-99";
        private String upperDate = "0000-00-00";

        private int nbRejectedPhoto = 0;
        private int nbNotSelectedPhoto = 0;
        private int nbSelectedPhoto = 0;
        private int nbPhotoTotal = 0;

        private final int[] nbStar = new int[6];
        private final Map<String, Integer> nbLabel = new HashMap<>();
        private final Map<String, Integer> nbTag = new HashMap<>();

        public void accumulate(Photo photo) {
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
                default:
                    break;
            }

            if (pick == -1) return;

            if (rating >= 0 && rating < nbStar.length) {
                nbStar[rating]++;
            }

            nbLabel.merge(label, 1, Integer::sum);
            for (String tag : tags) {
                nbTag.merge(tag, 1, Integer::sum);
            }

            updateDateBounds(exifDate);
        }

        private void updateDateBounds(String exifDate) {
            if (exifDate != null && exifDate.length() >= 10) {
                String date = exifDate.substring(0, 10);
                if (date.compareTo(lowerDate) < 0) lowerDate = date;
                if (date.compareTo(upperDate) > 0) upperDate = date;
            }
        }

        public String getLowerDate() { return lowerDate; }
        public String getUpperDate() { return upperDate; }
        public int getNbRejectedPhoto() { return nbRejectedPhoto; }
        public int getNbNotSelectedPhoto() { return nbNotSelectedPhoto; }
        public int getNbSelectedPhoto() { return nbSelectedPhoto; }
        public int getNbPhotoTotal() { return nbPhotoTotal; }
        public int[] getNbStar() { return nbStar; }
        public Map<String, Integer> getNbLabel() { return nbLabel; }
        public Map<String, Integer> getNbTag() { return nbTag; }
    }

}
