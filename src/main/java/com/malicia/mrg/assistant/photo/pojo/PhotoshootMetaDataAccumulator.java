package com.malicia.mrg.assistant.photo.pojo;

import com.malicia.mrg.assistant.photo.dto.PhotoData;
import com.malicia.mrg.assistant.photo.entity.Photo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoshootMetaDataAccumulator {
    private final int[] nbStar = new int[6];
    private final Map<String, Integer> nbLabel = new HashMap<>();
    private final Map<String, Integer> nbTag = new HashMap<>();
    private String lowerDate = "9999-99-99";
    private String upperDate = "0000-00-00";
    private int nbRejectedPhoto = 0;
    private int nbNotSelectedPhoto = 0;
    private int nbSelectedPhoto = 0;
    private int nbPhotoTotal = 0;

    public void accumulate(PhotoData photo) {
        nbPhotoTotal++;

        int pick = photo.getPick();
        String label = photo.getLabel();
        List<String> tags = photo.getKeywords();
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

    public String getLowerDate() {
        return lowerDate;
    }

    public String getUpperDate() {
        return upperDate;
    }

    public int getNbRejectedPhoto() {
        return nbRejectedPhoto;
    }

    public int getNbNotSelectedPhoto() {
        return nbNotSelectedPhoto;
    }

    public int getNbSelectedPhoto() {
        return nbSelectedPhoto;
    }

    public int getNbPhotoTotal() {
        return nbPhotoTotal;
    }

    public int[] getNbStar() {
        return nbStar;
    }

    public Map<String, Integer> getNbLabel() {
        return nbLabel;
    }

    public Map<String, Integer> getNbTag() {
        return nbTag;
    }
}
