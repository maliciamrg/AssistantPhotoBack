package com.malicia.mrg.assistant.photo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.PhotoDTO;
import com.malicia.mrg.assistant.photo.dto.ValidationResult;
import com.malicia.mrg.assistant.photo.exception.NotFoundException;
import com.malicia.mrg.assistant.photo.pojo.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoshootService {

    private final RootRepertoire rootRepertoire;
    private final MyConfig config;
    private final TagService tagService;

    public PhotoshootService(RootRepertoire rootRepertoire, MyConfig config, TagService tagService) {
        this.rootRepertoire = rootRepertoire;
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

        Photoshoot photoshoot = getPhotoshoot(seanceId, seanceList);

        return rootRepertoire.getAllPhotoFromPhotoshoot(photoshoot);

    }

    @Cacheable(value = "getPhotoshootList")
    public List<Photoshoot> getPhotoshootList(PhotoshootType photoshootType) {
        List<Photoshoot> photoshootArrayList = new ArrayList<>();

        for (PhotoshootRoot photoshootRoot : photoshootType.getPhotoshootRoot()) {

            photoshootArrayList = rootRepertoire.getPhotoshootList(photoshootRoot);

        }
        return photoshootArrayList;
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

    public Photoshoot getPhotoshoot(PhotoshootType photoshootType, String photoshootName) {

        List<Photoshoot> photoshootList = getPhotoshootList(photoshootType);

        for (Photoshoot photoshoot : photoshootList) {
            if (photoshoot.getName().equals(photoshootName)) {
                photoshoot.setGroupOfPhoto(rootRepertoire.getAllPhotoFromPhotoshoot(photoshoot));
                photoshoot.setMetaDataFromPhotoshoot(getMetaDataFromPhotoshoot(photoshoot));
                return photoshoot;
            }
        }
        throw new NotFoundException("photoshootName " + photoshootName + " not found");

    }

    private PhotoshootMetaData getMetaDataFromPhotoshoot(Photoshoot photoshoot) {

        PhotoGroup listPhoto = photoshoot.getGroupOfPhoto();

        PhotoshootMetaDataAccumulator accumulator = new PhotoshootMetaDataAccumulator();

        for (PhotoDTO photo : listPhoto) {
            accumulator.accumulate(photo);
        }

        String[] parts = photoshoot.getName().split("_");
        long daysBetween = computeDaysBetween(accumulator.getLowerDate(), accumulator.getUpperDate());

        PhotoshootMetaData photoshootMetaData = buildMetaDataRep(photoshoot.getName(), parts, accumulator, daysBetween);

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
        if (expectedValues.size() != parts.length) {
            message.append(parts.length)
                    .append(" champs pour ")
                    .append(expectedValues.size())
                    .append(" attendu \n");
            return false;
        }

        boolean valid = true;

        for (int i = 0; i < parts.length; i++) {

            List<String> expected = expectedValues.get(i);
            String specialExpectedValues = isSpecialExpectedValues(expected);

            switch (specialExpectedValues){
                case "DATE":
                    if (!expectedDate.equals(parts[i])) {
                        message.append("zone ").append(i).append(" : ").append(parts[i])
                                .append(" non valid for ").append(expected)
                                .append(" (").append(expectedDate).append(") \n");
                        valid = false;
                    }
                    break;
                default:
                    if (!expected.contains(parts[i])) {
                        message.append("zone ").append(i).append(" : ").append(parts[i]).append(" non valid \n");
                        valid = false;
                    }
                    break;
            }
        }

        return valid;
    }

    private String isSpecialExpectedValues(List<String> expected) {
        if (expected.get(0).startsWith("£") && expected.get(0).endsWith("£")) {
            return expected.get(0).substring(1, expected.get(0).length() - 1);
        }
        return "";
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

    @Cacheable(value = "getPhotoshootType")
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
