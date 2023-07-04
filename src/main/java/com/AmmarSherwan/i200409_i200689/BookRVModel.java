package com.AmmarSherwan.i200409_i200689;

import android.net.Uri;

public class BookRVModel {

    String id;
    String name;
    String category;
    String genre;
    String description;
    String author;
    String imgUri;
    String poster;

    public BookRVModel() {

    }

    public BookRVModel(String id, String name, String category, String genre, String description, String author, String imgUri, String poster) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.genre = genre;
        this.description = description;
        this.author = author;
        this.imgUri = imgUri;
        this.poster = poster;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }
}
