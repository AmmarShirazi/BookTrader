package com.AmmarSherwan.i200409_i200689;

public class ReviewRVModel {

    String id;
    float rating;
    String comment;
    String bookID;
    String poster;
    String posterImgUri;

    public ReviewRVModel() {

    }

    public ReviewRVModel(String id, float rating, String comment, String bookID, String poster, String posterImgUri) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.bookID = bookID;
        this.poster = poster;
        this.posterImgUri = posterImgUri;
    }

    public String getPosterImgUri() {
        return posterImgUri;
    }

    public void setPosterImgUri(String posterImgUri) {
        this.posterImgUri = posterImgUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
