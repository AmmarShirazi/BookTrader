package com.AmmarSherwan.i200409_i200689;

public class ChatRVModel {

    String id;
    String email;
    String imgUri;

    public ChatRVModel() {
        // fml
    }

    public ChatRVModel(String id, String email, String imgUri) {
        this.id = id;
        this.email = email;
        this.imgUri = imgUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }
}
