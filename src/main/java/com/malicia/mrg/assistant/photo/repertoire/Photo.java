package com.malicia.mrg.assistant.photo.repertoire;


import java.io.Serializable;
import java.util.UUID;

public class Photo implements Serializable {
    public static final String T_09_15_00_Z = "2024-03-12T09:15:00Z";
    private static final long serialVersionUID = 1L;
    private UUID id;

    private String path;
    private byte[] thumbnail;  // BLOB stored as byte array
    private String relativeToPath;
    private String filename;
    private String extension;
    private String createdDate;
    private String exifDate;
    private String dateTaken;
    private int rating;
    private String label;
    private String[] keywords;
    private int pick;

    public Photo() {
        //       this.date_taken = T_09_15_00_Z;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public int getPick() {
        return pick;
    }

    public void setPick(int pick) {
        this.pick = pick;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnailBytes) {
        this.thumbnail = thumbnailBytes;  // Store the binary data directly
    }

    // Getters and setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        if (dateTaken == null || dateTaken.compareTo(T_09_15_00_Z) == 0) {
            this.dateTaken = createdDate;
        }
    }

    public String getExifDate() {
        return exifDate;
    }

    public void setExifDate(String exifDate) {
        this.exifDate = exifDate;
        this.dateTaken = exifDate;
    }

    @Override
    public String toString() {
        return "Photo{" + "path='" + path + '\'' + ", relativeToPath='" + relativeToPath + '\'' + ", filename='" + filename + '\'' + ", extension='" + extension + '\'' + ", createdDate='" + createdDate + '\'' + ", exifDate='" + exifDate + '\'' + '}';
    }

    public String getRelativeToPath() {
        return relativeToPath;
    }

    public void setRelativeToPath(String relativeToPath) {
        this.relativeToPath = relativeToPath;
    }

    public UUID getId() {
        return id;
    }

    public void mergeFrom(Photo other) {
        if (other == null) {
            return;  // No data to merge if the other Photo is null
        }

        if (other.path != null) {
            this.path = other.path;
        }
        if (other.thumbnail != null) {
            this.thumbnail = other.thumbnail;
        }
        if (other.relativeToPath != null) {
            this.relativeToPath = other.relativeToPath;
        }
        if (other.filename != null) {
            this.filename = other.filename;
        }
        if (other.extension != null) {
            this.extension = other.extension;
        }
        if (other.createdDate != null) {
            this.createdDate = other.createdDate;
        }
        if (other.exifDate != null) {
            this.exifDate = other.exifDate;
        }
        if (other.dateTaken != null) {
            this.dateTaken = other.dateTaken;
        }
        //     if (other.rating != null) {
        this.rating = other.rating;
        //     }
        if (other.label != null) {
            this.label = other.label;
        }
        if (other.keywords != null) {
            this.keywords = other.keywords;
        }
        //    if (other.pick != null) {
        this.pick = other.pick;
        //    }
    }

}
