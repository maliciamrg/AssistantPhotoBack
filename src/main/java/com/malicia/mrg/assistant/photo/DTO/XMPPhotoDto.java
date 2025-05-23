package com.malicia.mrg.assistant.photo.DTO;

public class XMPPhotoDto {
    //The value shall be -1 or in the range [0..5],
    // where -1 indicates “rejected”
    // and 0 indicates “unrated”.
    // If xmp:Rating is not present, a value of 0 should be assumed.
    // NOTE: Anticipated usage is for a typical “star rating” UI, with the addition of a notion of rejection.
    private Integer rating;
    private String createDate;
    private String subject;

    public XMPPhotoDto() {
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
