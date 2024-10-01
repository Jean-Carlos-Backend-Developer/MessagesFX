package com.example.messagesfx.responses;

import com.example.messagesfx.models.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("ok")
    private boolean ok;
    @SerializedName("error")
    private String error;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String image;
    @SerializedName("token")
    private String token;
    @SerializedName("users")
    private List<User> users;

    public UserResponse(boolean ok){
        this.ok = ok;
    }

    public UserResponse(boolean ok, String error){
        this.ok = ok;
        this.error = error;
    }

    public UserResponse(boolean ok,String id, String name, String image, String token){
        this.ok = ok;
        this.id = id;
        this.name = name;
        this.image = image;
        this.token = token;
    }

    public UserResponse(boolean ok, List<User> users){
        this.ok = ok;
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public boolean getOk(){
        return ok;
    }

    public String getError() {
        return error;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getToken() {
        return token;
    }

    public List<User> getUsers() {
        return users;
    }
}