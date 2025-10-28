package com.malicia.mrg.assistant.photo.pojo;

import com.malicia.mrg.assistant.photo.dto.PhotoDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoshootMetaDataAccumulator {
    private final int[] nbStar = new int[6];
    private final Map<String, Integer> nbLabel = new HashMap<>();
    private final Map<String, Integer> nbTag = new HashMap<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date lowerDate = new Date(Long.MAX_VALUE);
    private Date upperDate = new Date(0L);
    private int nbRejectedPhoto = 0;
    private int nbNotSelectedPhoto = 0;
    private int nbSelectedPhoto = 0;
    private int nbPhotoTotal = 0;

    public void accumulate(PhotoDTO photo) {
        nbPhotoTotal++;

        int pick = photo.getPick();
        String label = photo.getLabel();
        List<String> tags = photo.getKeywords();
        String takenDate = photo.getTakenDate();
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

        if (label != null) {
            nbLabel.merge(label, 1, Integer::sum);
        }
        for (String tag : tags) {
            nbTag.merge(tag, 1, Integer::sum);
        }

        updateDateBounds(takenDate);
    }

    private void updateDateBounds(String takenDate) {
        if(takenDate == null) return;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = formatter.parse(takenDate);

            if (lowerDate == null || parsedDate.before(lowerDate)) {
                lowerDate = parsedDate;
            }
            if (upperDate == null || parsedDate.after(upperDate)) {
                upperDate = parsedDate;
            }
        } catch (ParseException e) {
            System.err.println("Invalid takenDate: " + takenDate);
        }
    }


    public Date getLowerDate() {
        return lowerDate;
    }

    public Date getUpperDate() {
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
