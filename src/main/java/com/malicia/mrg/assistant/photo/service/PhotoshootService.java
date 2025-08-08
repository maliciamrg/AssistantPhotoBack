package com.malicia.mrg.assistant.photo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.dto.ValidationResult;
import com.malicia.mrg.assistant.photo.exception.NotFoundException;
import com.malicia.mrg.assistant.photo.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PhotoshootService {
    private static final Logger logger = LoggerFactory.getLogger(PhotoshootService.class);
    private final RootRepertoire rootRepertoire;
    private final PhotoService photoService;
    private final MyConfig config;
    private final TagService tagService;
    private final PhotoProcessorService photoProcessorService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public PhotoshootService(RootRepertoire rootRepertoire, PhotoService photoService, MyConfig config, TagService tagService, PhotoProcessorService photoProcessorService) {
        this.rootRepertoire = rootRepertoire;
        this.photoService = photoService;
        this.config = config;
        this.tagService = tagService;
        this.photoProcessorService = photoProcessorService;
    }

//    public PhotoGroup getAllPhotoFromPhotoshoot(String seanceId, List<Photoshoot> seanceList) {
//
//        Photoshoot photoshoot = getPhotoshoot(seanceId, seanceList);
//
//        return getAllPhotoFromPhotoshoot(photoshoot);
//
//    }

    @Cacheable(value = "getPhotoshootList", key = "#photoshootType.photoshootTypeEnum.name()")
    public List<Photoshoot> getPhotoshootList(PhotoshootType photoshootType) {
        List<Photoshoot> photoshootArrayList = new ArrayList<>();

        for (PhotoshootRoot photoshootRoot : photoshootType.getPhotoshootRoot()) {

            photoshootArrayList.addAll(rootRepertoire.getPhotoshootList(photoshootRoot));

        }
        return photoshootArrayList;
    }

    private PhotoshootMetaData buildMetaDataRep(String photoshootName, String[] photoshootNameParts, PhotoshootMetaDataAccumulator acc) {
        PhotoshootMetaData rep = new PhotoshootMetaData();
        rep.setPhotoshootName(photoshootName);
        rep.setPhotoshootNameParts(photoshootNameParts);
        rep.setLowerDate(acc.getLowerDate());
        rep.setUpperDate(acc.getUpperDate());
        rep.setNbDay(TimeUnit.DAYS.convert(acc.getUpperDate().getTime() - acc.getLowerDate().getTime(), TimeUnit.MILLISECONDS)+1);
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

    public Photoshoot getPhotoshoot(PhotoshootType photoshootType, String photoshootName) {
        return getPhotoshoot(photoshootType, photoshootName, null);
    }

    public Photoshoot getPhotoshoot(PhotoshootType photoshootType, String photoshootName, UUID sessionId) {
        logger.debug("getPhotoshoot");
        List<Photoshoot> photoshootList = getPhotoshootList(photoshootType);

        for (Photoshoot photoshoot : photoshootList) {
            if (photoshoot.getName().equals(photoshootName)) {
                photoshoot.setGroupOfPhoto(getAllPhotoFromPhotoshoot(photoshoot, sessionId));
                photoshoot.setMetaDataFromPhotoshoot(getMetaDataFromPhotoshoot(photoshoot));
                photoshoot.setValidationResult(validatePhotoshoot(photoshootType, photoshoot));
                return photoshoot;
            }
        }
        throw new NotFoundException("photoshootName " + photoshootName + " not found");

    }

    public PhotoGroup getAllPhotoFromPhotoshoot(Photoshoot photoshoot) {
        return getAllPhotoFromPhotoshoot(photoshoot, null);
    }

    @Cacheable(value = "getAllPhotoFromPhotoshoot", key = "#photoshoot.name")
    public PhotoGroup getAllPhotoFromPhotoshoot(Photoshoot photoshoot, UUID sessionId) {
        PhotoGroup photoGroup = new PhotoGroup();

        String pathToScan = photoshoot.getPath();
        if (sessionId == null) {
            sessionId = UUID.randomUUID();
        }
        List<PhotoDTO> photos = photoProcessorService.startProcessing(sessionId, pathToScan, rootRepertoire.getPathList(pathToScan));
        photoGroup.setPhotos(photos);


        return photoGroup;
    }

    private PhotoshootMetaData getMetaDataFromPhotoshoot(Photoshoot photoshoot) {

        PhotoGroup listPhoto = photoshoot.getGroupOfPhoto();

        PhotoshootMetaDataAccumulator accumulator = new PhotoshootMetaDataAccumulator();

        for (PhotoDTO photo : listPhoto) {
            accumulator.accumulate(photo);
        }

        String[] parts = photoshoot.getName().split("_");

        PhotoshootMetaData photoshootMetaData = buildMetaDataRep(photoshoot.getName(), parts, accumulator);

        writeMetaDataToFile(photoshootMetaData, photoshoot.getPath(), photoshoot.getName());

        return photoshootMetaData;
    }

    public ValidationResult validatePhotoshoot(PhotoshootType photoshootType, Photoshoot photoshoot) {
        String[] parts = photoshoot.getName().split("_");
        return controlRepertoire(parts, photoshoot.getMetaDataFromPhotoshoot(), photoshootType);
    }

    public ValidationResult validatePhotoshoot(PhotoshootType photoshootType, Photoshoot photoshoot, String[] parts) {
        return controlRepertoire(parts, photoshoot.getMetaDataFromPhotoshoot(), photoshootType);
    }

    private ValidationResult controlRepertoire(String[] parts, PhotoshootMetaData metaData, PhotoshootType photoshootType) {
        ValidationResult validationResult = new ValidationResult();

        boolean isValid = true;
        StringBuilder message = new StringBuilder();

        List<List<String>> validFields = formatValidFields(dateFormat.format(metaData.getLowerDate()) , getPhotoshootTypeZoneValeurAdmise(photoshootType));

        if (!validatePhotoshootName(parts, validFields , message)) {
            isValid = false;
        }

        if (!validatePhotoCount(metaData, photoshootType, message)) {
            isValid = false;
        }

        if (!validateStarRatios(metaData, photoshootType, message)) {
            isValid = false;
        }

        validationResult.setValid(isValid);
        validationResult.setMessage(isValid ? "valid" : message.toString());
        validationResult.setValidFields(validFields);
        return validationResult;
    }

    private List<List<String>> formatValidFields(String expectedDate, List<List<String>> expectedValues) {
        List<List<String>> correctedExpectedValues = new ArrayList<>();

        for (int i = 0; i < expectedValues.size(); i++) { //first value is id of list

            String specialExpectedValues = "";

            List<String> expected = new ArrayList<>(expectedValues.get(i));
            expected.remove(0);
            if (expected.get(0).startsWith("£") && expected.get(0).endsWith("£")) {
                specialExpectedValues = expected.get(0).substring(1, expected.get(0).length() - 1);
            }

            switch (specialExpectedValues) {
                case "DATE":
                    correctedExpectedValues.add(List.of(expectedValues.get(i).get(0),expectedDate));
                    break;
                default:
                    correctedExpectedValues.add(expectedValues.get(i));
                    break;
            }
        }

        return correctedExpectedValues;

    }

    private boolean validatePhotoshootName(String[] parts, List<List<String>> expectedValues, StringBuilder message) {
        String str = "PhotoshootName : ";

        if (expectedValues.size() != parts.length) {
            message.append(str)
                    .append(parts.length)
                    .append(" champs pour ")
                    .append(expectedValues.size())
                    .append(" attendu \n");
            return false;
        }

        boolean valid = true;

        for (int i = 0; i < parts.length; i++) {

            if (!expectedValues.get(i).contains(parts[i])) {
                message.append("zone ").append(i).append(" : ").append(parts[i]).append(" non valid \n");
                valid = false;
            }

        }

        if (!valid) { message.insert(0, str);}
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

    @Cacheable(value = "getPhotoshootType", key = "#photoshootTypeName")
    public PhotoshootType getPhotoshootType(String photoshootTypeName) {
        Optional<PhotoshootType> photoshootType = config.getPhotoshootType().stream().filter(seance -> photoshootTypeName.equals(seance.getPhotoshootTypeEnum().name())).findFirst();

        if (!photoshootType.isEmpty()) {
            return photoshootType.get();
        }

        return new PhotoshootType();
    }

    public List<List<String>> getPhotoshootTypeZoneValeurAdmise(PhotoshootType photoshootType) {
        List<List<String>> possibleValueForPhotoshoot = new ArrayList<>();

        for (String placeholder : photoshootType.getZoneValeurAdmise()) {

            List<String> possibleValueForPhotoshootChamp = new ArrayList<>();
            possibleValueForPhotoshootChamp.add(placeholder);

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

    public List<PhotoshootType> getPhotoshootType() {
        return config.getPhotoshootType();
    }
}
