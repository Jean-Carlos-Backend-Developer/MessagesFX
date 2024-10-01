package com.example.messagesfx.models;

import com.google.gson.annotations.SerializedName;
import javafx.scene.image.ImageView;

public class User {
    @SerializedName("_id")
    private String _id;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String image;

    public User(String _id, String name, String image){
        this._id = _id;
        this.name = name;
        this.image = image;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public ImageView getImageView() {
        ImageView imgView = new ImageView("http://localhost:8080/" + image);
        imgView.setFitHeight(30);
        imgView.setPreserveRatio(true);
        return imgView;
    }
}
