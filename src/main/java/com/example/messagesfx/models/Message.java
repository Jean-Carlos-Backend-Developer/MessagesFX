package com.example.messagesfx.models;

import com.google.gson.annotations.SerializedName;
import javafx.scene.image.ImageView;

public class Message {
    @SerializedName("_id")
    private String _id;
    @SerializedName("from")
    private String from;
    @SerializedName("to")
    private String to;
    @SerializedName("message")
    private String message;
    @SerializedName("image")
    private String image;
    @SerializedName("sent")
    private String sent;

    public Message(String _id, String from, String to, String message, String image, String sent){
        this._id = _id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.image = image;
        this.sent = sent;
    }

    public Message(String _id, String from, String to, String message, String sent){
        this._id = _id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.sent = sent;
    }

    public String get_id() {
        return _id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public String getSent() {

        return sent;
    }

    public ImageView getImageView() {
        ImageView imgView = new ImageView("http://localhost:8080" + "/" + image);
        imgView.setFitHeight(30);
        imgView.setPreserveRatio(true);
        return imgView;
    }
}