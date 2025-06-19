package com.malicia.mrg.assistant.photo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.cache.CacheService;
import com.malicia.mrg.assistant.photo.dto.ValidationResult;
import com.malicia.mrg.assistant.photo.exception.NotFoundException;
import com.malicia.mrg.assistant.photo.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoshootService {

    private static final Logger logger = LoggerFactory.getLogger(PhotoshootService.class);

    private final CacheService redisTemplate;
    private final RootRepertoire rootRep;
    private final MyConfig config;
    private final TagService tagService;

    private final Duration ttl = Duration.ofMinutes(1);

    public PhotoshootService(CacheService redisTemplate, RootRepertoire rootRep, MyConfig config, TagService tagService) {
        this.redisTemplate = redisTemplate;
        this.rootRep = rootRep;
        this.config = config;
        this.tagService = tagService;
    }

    private static Photoshoot getPhotoshoot(String seanceId, List<Photoshoot> seanceList) {
        Optional<Photoshoot> pathToScan = seanceList.stream()
                .filter(seance -> seance.getName().equals(seanceId)) // Filter by id
                .findFirst(); // Return the first match (Optional)
        if (pathToScan.isEmpty()) {
            throw new IllegalArgumentException("Seance ID '" + seanceId + "' not found in provided seance list.");
        }
        return pathToScan.get();
    }

    public PhotoGroup getAllPhotoFromPhotoshoot(String seanceId, List<Photoshoot> seanceList) {
        PhotoGroup cachedPhotos = (PhotoGroup) redisTemplate.get(seanceId);
        if (cachedPhotos != null) {
            Long ttlKey = redisTemplate.getExpire(seanceId);
            logger.info("TTL for key '{}' is: {} seconds", seanceId, ttlKey);
            return cachedPhotos;
        }

        Photoshoot photoshoot = getPhotoshoot(seanceId, seanceList);

        PhotoGroup allPhotoFromPhotoRepertoire = rootRep.getAllPhotoFromPhotoshoot(photoshoot);
        redisTemplate.set(seanceId, allPhotoFromPhotoRepertoire, ttl);
        logger.info("redisTemplate.opsForValue().set : {} ", seanceId);
        return allPhotoFromPhotoRepertoire;
    }

    public List<Photoshoot> getPhotoshootList(String photoshootTypeName) {
        List<Photoshoot> photoshootListCached = (List<Photoshoot>) redisTemplate.get(photoshootTypeName);
        if (photoshootListCached != null) {
            Long ttlKey = redisTemplate.getExpire(photoshootTypeName);
            logger.info("TTL for key '{}' is: {} seconds", photoshootTypeName, ttlKey);
            return photoshootListCached;
        }

        PhotoshootType photoshootType = getPhotoshootType(photoshootTypeName);

        if (photoshootType != null) {

            List<Photoshoot> photoshootList = rootRep.getPhotoshootList(photoshootType.getPhotoshootTypeEnum());

            redisTemplate.set(photoshootTypeName, photoshootList, ttl);
            logger.info("redisTemplate.opsForValue().set :{}", photoshootTypeName);

            return photoshootList;
        } else {
            return Collections.emptyList();
        }
    }

    public PhotoshootMetaData getMetaDataFromPhotoshoot(String photoshootName, List<Photoshoot> seanceList) {

        Photoshoot photoshoot = getPhotoshoot(photoshootName, seanceList);
        PhotoGroup listPhoto = getAllPhotoFromPhotoshoot(photoshootName, seanceList);

        PhotoshootMetaDataAccumulator accumulator = new PhotoshootMetaDataAccumulator();

        for (Photo photo : listPhoto) {
            accumulator.accumulate(photo);
        }

        String[] parts = photoshootName.split("_");
        long daysBetween = computeDaysBetween(accumulator.getLowerDate(), accumulator.getUpperDate());

        PhotoshootMetaData photoshootMetaData = buildMetaDataRep(photoshootName, parts, accumulator, daysBetween);
        writeMetaDataToFile(photoshootMetaData, photoshoot.getPath(), photoshootName);

        return photoshootMetaData;
    }

    private long computeDaysBetween(String lowerDate, String upperDate) {
        LocalDate start = LocalDate.parse(lowerDate);
        LocalDate end = LocalDate.parse(upperDate);
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    private PhotoshootMetaData buildMetaDataRep(String photoshootName, String[] photoshootNameParts, PhotoshootMetaDataAccumulator acc, long nbDays) {
        PhotoshootMetaData rep = new PhotoshootMetaData();
        rep.setPhotoshootName(photoshootName);
        rep.setPhotoshootNameParts(photoshootNameParts);
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

    private void writeMetaDataToFile(PhotoshootMetaData metaData, String path, String name) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(path + File.separator + name + "_metadata.json");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, metaData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Photoshoot getPhotoshoot(String photoshootTypeName, String photoshootName) {

        List<Photoshoot> photoshootList = getPhotoshootList(photoshootTypeName);

        for (Photoshoot photoshoot : photoshootList) {
            if (photoshoot.getName().equals(photoshootName)) {
                photoshoot.setGroupOfPhoto(rootRep.getAllPhotoFromPhotoshoot(photoshoot));
                photoshoot.setMetaDataFromPhotoshoot(getMetaDataFromPhotoshoot(photoshoot));
                return photoshoot;
            }
        }
        throw new NotFoundException("photoshootName " + photoshootName + " not found");

    }

    private PhotoshootMetaData getMetaDataFromPhotoshoot(Photoshoot photoshoot) {

        PhotoGroup listPhoto = photoshoot.getGroupOfPhoto();

        PhotoshootMetaDataAccumulator accumulator = new PhotoshootMetaDataAccumulator();

        for (Photo photo : listPhoto) {
            accumulator.accumulate(photo);
        }

        String[] parts = photoshoot.getName().split("_");
        long daysBetween = computeDaysBetween(accumulator.getLowerDate(), accumulator.getUpperDate());

        PhotoshootMetaData photoshootMetaData = buildMetaDataRep(photoshoot.getName(), parts, accumulator, daysBetween);

        writeMetaDataToFile(photoshootMetaData, photoshoot.getPath(), photoshoot.getName());

        return photoshootMetaData;
    }

    public ValidationResult validatePhotoshoot(String photoshootTypeName, Photoshoot photoshoot) {
        String[] parts = photoshoot.getName().split("_");
        PhotoshootType photoshootType = getPhotoshootType(photoshootTypeName);
        return controlRepertoire(parts, photoshoot.getMetaDataFromPhotoshoot(), photoshootType);
    }

    public ValidationResult validatePhotoshoot(String photoshootTypeName, Photoshoot photoshoot, String[] parts) {
        PhotoshootType photoshootType = getPhotoshootType(photoshootTypeName);
        return controlRepertoire(parts, photoshoot.getMetaDataFromPhotoshoot(), photoshootType);
    }
//
//    public ValidationResult validatePhotoshoot(String photoshootTypeName, String photoshootName) {
//        List<Photoshoot> photoshootList = getPhotoshootList(photoshootTypeName);
//        PhotoshootMetaData metaDataFromPhotoRepertoire = getMetaDataFromPhotoshoot(photoshootName, photoshootList);
//        String[] parts = photoshootName.split("_");
//        PhotoshootType photoshootType = getPhotoshootType(photoshootTypeName);
//        return controlRepertoire(parts, metaDataFromPhotoRepertoire, photoshootType);
//    }
//
//    public ValidationResult validatePhotoshoot(String photoshootTypeName, String photoshootName, String[] parts) {
//        List<Photoshoot> photoshootList = getPhotoshootList(photoshootTypeName);
//        PhotoshootMetaData metaDataFromPhotoRepertoire = getMetaDataFromPhotoshoot(photoshootName, photoshootList);
//        PhotoshootType photoshootType = getPhotoshootType(photoshootTypeName);
//        return controlRepertoire(parts, metaDataFromPhotoRepertoire, photoshootType);
//    }

    private ValidationResult controlRepertoire(String[] parts, PhotoshootMetaData metaData, PhotoshootType photoshootType) {
        boolean isValid = true;
        StringBuilder message = new StringBuilder();

        if (!validatePhotoshootName(parts, metaData.getLowerDate(), getPhotoshootTypeZoneValeurAdmise(photoshootType), message)) {
            isValid = false;
        }

        if (!validatePhotoCount(metaData, photoshootType, message)) {
            isValid = false;
        }

        if (!validateStarRatios(metaData, photoshootType, message)) {
            isValid = false;
        }

        return new ValidationResult(isValid, isValid ? "valid" : message.toString());
    }

    private boolean validatePhotoshootName(String[] parts, String expectedDate, List<List<String>> expectedValues, StringBuilder message) {
        boolean valid = true;

        if (expectedValues.size() != parts.length) {
            message.append(parts.length).append(" champs pour ").append(expectedValues.size()).append(" attendu \n");
            return false;
        }

        for (int i = 0; i < expectedValues.size(); i++) {
            String expected = expectedValues.get(i).get(0);

            if (expected.startsWith("£") && expected.endsWith("£")) {
                String key = expected.substring(1, expected.length() - 1);

                if ("DATE".equals(key)) {
                    if (!expectedDate.equals(parts[i])) {
                        message.append("zone ").append(i).append(" : ").append(parts[i])
                                .append(" non valid for ").append(expected)
                                .append(" (").append(expectedDate).append(") \n");
                        valid = false;
                    }
                } else {
                    message.append("zone ").append(i).append(" : part ").append(expected).append(" non reconnu \n");
                    valid = false;
                }
            } else {
                if (!expectedValues.get(i).contains(parts[i])) {
                    message.append("zone ").append(i).append(" : ").append(parts[i]).append(" non valid \n");
                    valid = false;
                }
            }
        }

        return valid;
    }

    private boolean validateStarRatios(PhotoshootMetaData metaData, PhotoshootType photoshootType, StringBuilder message) {
        boolean valid = true;
        int[] nbStars = metaData.getNbStar();
        int nbRepPhoto = metaData.getNbSelectedPhoto() + metaData.getNbNotSelectedPhoto();

        for (int i = 1; i < nbStars.length - 1; i++) {
            int nbStarRep = nbStars[i];

            int maxRatio = (int) Math.ceil(photoshootType.getRatioStarMax().get(i - 1) * nbRepPhoto / 100.0);
            if (nbStarRep > maxRatio) {
                message.append("Star=").append(i).append(" : nbStarRep (").append(nbStarRep)
                        .append(") > nbMaxStar (").append(maxRatio).append(") \n");
                valid = false;
            }

            int minRatio = (int) Math.ceil(photoshootType.getRatioStarMin().get(i - 1) * nbRepPhoto / 100.0);
            if (nbStarRep < minRatio) {
                message.append("Star=").append(i).append(" : nbStarRep (").append(nbStarRep)
                        .append(") < nbMinStar (").append(minRatio).append(") \n");
                valid = false;
            }
        }

        return valid;
    }

    private boolean validatePhotoCount(PhotoshootMetaData metaData, PhotoshootType photoshootType, StringBuilder message) {
        int nbMaxPhoto = (int) ((metaData.getNbDay() / photoshootType.getUniteDeJour()) * photoshootType.getNbMaxParUniteDeJour());
        int nbRepPhoto = metaData.getNbSelectedPhoto() + metaData.getNbNotSelectedPhoto();

        if (nbRepPhoto > nbMaxPhoto) {
            message.append("nbRepPhoto (").append(nbRepPhoto)
                    .append(") > nbMaxPhoto (").append(nbMaxPhoto).append(") \n");
            return false;
        }

        return true;
    }

    public PhotoshootType getPhotoshootType(String photoshootTypeName) {
        Optional<PhotoshootType> photoshootType = config.getPhotoshootType().stream().filter(seance -> photoshootTypeName.equals(seance.getPhotoshootTypeEnum().name())).findFirst();

        if (!photoshootType.isEmpty()) {
            return photoshootType.get();
        }

        return null;
    }

    public List<List<String>> getPhotoshootTypeZoneValeurAdmise(PhotoshootType photoshootType) {
        List<List<String>> possibleValueForPhotoshoot = new ArrayList<>();

        for (String placeholder : photoshootType.getZoneValeurAdmise()) {

            List<String> possibleValueForPhotoshootChamp = new ArrayList<>();

            String findString = "";
            String[] parts = placeholder.split("\\|");
            for (String part : parts) {
                if (part.startsWith("@") && part.length() > 1 && part.endsWith("@")) {
                    findString = part.substring(1, part.length() - 1);
                    possibleValueForPhotoshootChamp.addAll(tagService.getTagListByName(findString));
                } else {
                    possibleValueForPhotoshootChamp.add(part);
                }
            }

            possibleValueForPhotoshoot.add(possibleValueForPhotoshootChamp);
        }

        return possibleValueForPhotoshoot;
    }

}
