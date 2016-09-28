package com.demo.photogallerytest.data.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jaivignesh.m.jt on 9/26/2016.
 */
public class AlbumResponse {

    @SerializedName("userId")
    public int userId;

    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
