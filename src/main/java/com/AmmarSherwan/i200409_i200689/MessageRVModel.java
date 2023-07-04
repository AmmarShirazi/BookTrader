package com.AmmarSherwan.i200409_i200689;

public class MessageRVModel {

    String id;
    int messageType;
    String message;
    String senderEmail;
    String receiverEmail;
    String senderImgUri;

    public MessageRVModel() {

    }


    public MessageRVModel(String id, int messageType, String message, String senderEmail, String receiverEmail, String senderImgUri) {
        this.id = id;
        this.messageType = messageType;
        this.message = message;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.senderImgUri = senderImgUri;
    }

    public String getSenderImgUri() {
        return senderImgUri;
    }

    public void setSenderImgUri(String senderImgUri) {
        this.senderImgUri = senderImgUri;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
