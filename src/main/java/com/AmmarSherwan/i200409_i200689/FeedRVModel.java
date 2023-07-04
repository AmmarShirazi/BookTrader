package com.AmmarSherwan.i200409_i200689;

public class FeedRVModel {

    String id;
    String giver;
    String bookName;
    String taker;
    String date;

    public FeedRVModel() {

    }

    public FeedRVModel(String id, String giver, String bookName, String taker, String date) {
        this.id = id;
        this.giver = giver;
        this.bookName = bookName;
        this.taker = taker;
        this.date = date;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGiver() {
        return giver;
    }

    public void setGiver(String giver) {
        this.giver = giver;
    }

    public String getTaker() {
        return taker;
    }

    public void setTaker(String taker) {
        this.taker = taker;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
