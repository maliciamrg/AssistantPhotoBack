package com.malicia.mrg.assistant.photo.repertoire;



import java.io.Serializable;
import java.util.UUID;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String T_09_15_00_Z = "2024-03-12T09:15:00Z";

    private UUID id;

    private String path;
    private byte[] thumbnail;  // BLOB stored as byte array
    private String relativeToPath;
    private String filename;
    private String extension;
    private String createdDate;
    private String exifDate;
    private String date_taken;
    private Boolean flagged;
    private String flagType;
    private Integer starred;

    public Photo() {
 //       this.date_taken = T_09_15_00_Z;
    }

    public Integer getStarred() {
        return starred;
    }

    public void setStarred(Integer starred) {
        this.starred = starred;
    }

    public String getFlagType() {
        return flagType;
    }

    public void setFlagType(String flagType) {
        this.flagType = flagType;
    }

    public Boolean getFlagged() {
        return flagged;
    }

    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
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
        if (date_taken == null || date_taken.compareTo(T_09_15_00_Z) == 0) {
            this.date_taken = createdDate;
        }
    }

    public String getExifDate() {
        return exifDate;
    }

    public void setExifDate(String exifDate) {
        this.exifDate = exifDate;
        this.date_taken = exifDate;
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
        if (other.date_taken != null) {
            this.date_taken = other.date_taken;
        }
        if (other.flagged != null) {
            this.flagged = other.flagged;
        }
        if (other.flagType != null) {
            this.flagType = other.flagType;
        }
        if (other.starred != null) {
            this.starred = other.starred;
        }
    }
}
