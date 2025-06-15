package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.ValidationResult;
import com.malicia.mrg.assistant.photo.parameter.SeanceType;
import com.malicia.mrg.assistant.photo.pojo.MetaDataRep;
import com.malicia.mrg.assistant.photo.pojo.ShootingParam;
import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RepertoireService {

    private final MyConfig config;
    private final TagService tagService;
    private final PhotoSessionService photoSessionService;

    public RepertoireService(MyConfig config, TagService tagService, PhotoSessionService photoSessionService) {
        this.config = config;
        this.tagService = tagService;
        this.photoSessionService = photoSessionService;
    }

    public ValidationResult validateRepertoire(String typeName, String repertoireName) {
        List<SeanceRepertoire> seanceRepertoireList = photoSessionService.getSeanceRepertoireList(typeName);
        MetaDataRep metaDataFromPhotoRepertoire = photoSessionService.getMetaDataFromPhotoRepertoire(repertoireName, seanceRepertoireList);
        String[] parts = repertoireName.split("_");
        ShootingParam shootingParam = getshootingParam(typeName);
        return controlRepertoire(parts, metaDataFromPhotoRepertoire, shootingParam);
    }

    public ValidationResult validateRepertoire(String typeName, String repertoireName, String[] parts) {
        List<SeanceRepertoire> seanceRepertoireList = photoSessionService.getSeanceRepertoireList(typeName);
        MetaDataRep metaDataFromPhotoRepertoire = photoSessionService.getMetaDataFromPhotoRepertoire(repertoireName, seanceRepertoireList);
        ShootingParam shootingParam = getshootingParam(typeName);
        return controlRepertoire(parts, metaDataFromPhotoRepertoire, shootingParam);
    }

    private ValidationResult controlRepertoire(String[] parts, MetaDataRep metaData, ShootingParam shootingParam) {
        boolean isValid = true;
        StringBuilder message = new StringBuilder();

        if (!validateRepertoireName(parts, metaData.getLowerDate(), shootingParam.getPossibleValueForRepertoireName(), message)) {
            isValid = false;
        }

        if (!validatePhotoCount(metaData, shootingParam.getSeanceType(), message)) {
            isValid = false;
        }

        if (!validateStarRatios(metaData, shootingParam.getSeanceType(), message)) {
            isValid = false;
        }

        return new ValidationResult(isValid, isValid ? "valid" : message.toString());
    }

    private boolean validateRepertoireName(String[] parts, String expectedDate, List<List<String>> expectedValues, StringBuilder message) {
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

    private boolean validateStarRatios(MetaDataRep metaData, SeanceType seanceType, StringBuilder message) {
        boolean valid = true;
        int[] nbStars = metaData.getNbStar();
        int nbRepPhoto = metaData.getNbSelectedPhoto() + metaData.getNbNotSelectedPhoto();

        for (int i = 1; i < nbStars.length - 1; i++) {
            int nbStarRep = nbStars[i];

            int maxRatio = (int) Math.ceil(seanceType.getRatioStarMax().get(i - 1) * nbRepPhoto / 100.0);
            if (nbStarRep > maxRatio) {
                message.append("Star=").append(i).append(" : nbStarRep (").append(nbStarRep)
                        .append(") > nbMaxStar (").append(maxRatio).append(") \n");
                valid = false;
            }

            int minRatio = (int) Math.ceil(seanceType.getRatioStarMin().get(i - 1) * nbRepPhoto / 100.0);
            if (nbStarRep < minRatio) {
                message.append("Star=").append(i).append(" : nbStarRep (").append(nbStarRep)
                        .append(") < nbMinStar (").append(minRatio).append(") \n");
                valid = false;
            }
        }

        return valid;
    }

    private boolean validatePhotoCount(MetaDataRep metaData, SeanceType seanceType, StringBuilder message) {
        int nbMaxPhoto = (int) ((metaData.getNbDay() / seanceType.getUniteDeJour()) * seanceType.getNbMaxParUniteDeJour());
        int nbRepPhoto = metaData.getNbSelectedPhoto() + metaData.getNbNotSelectedPhoto();

        if (nbRepPhoto > nbMaxPhoto) {
            message.append("nbRepPhoto (").append(nbRepPhoto)
                    .append(") > nbMaxPhoto (").append(nbMaxPhoto).append(") \n");
            return false;
        }

        return true;
    }

    public ShootingParam getshootingParam(String typeId) {
        Optional<SeanceType> seanceType = config.getSeanceType().stream().filter(seance -> typeId.equals(seance.getNom().name())).findFirst();

        List<List<String>> possibleValueForRepertoireName = new ArrayList<>();

        if (!seanceType.isEmpty()) {

            for (String placeholder : seanceType.get().getZoneValeurAdmise()) {

                List<String> possibleValueForRepertoireNameChamp = new ArrayList<>();

                String findString = "";
                String[] parts = placeholder.split("\\|");
                for (String part : parts) {
                    if (part.startsWith("@") && part.length() > 1 && part.endsWith("@")) {
                        findString = part.substring(1, part.length() - 1);
                        possibleValueForRepertoireNameChamp.addAll(tagService.getTagListByName(findString));
                    } else {
                        possibleValueForRepertoireNameChamp.add(part);
                    }
                }

                possibleValueForRepertoireName.add(possibleValueForRepertoireNameChamp);
            }

        }
        ShootingParam shootingParam = new ShootingParam();
        shootingParam.setPossibleValueForRepertoireName(possibleValueForRepertoireName);
        shootingParam.setSeanceType(seanceType.isEmpty() ? null : seanceType.get());
        return shootingParam;
    }
}
