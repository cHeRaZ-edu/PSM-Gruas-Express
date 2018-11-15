package com.psm.edu.psm_gruas_express.models;

import com.google.gson.Gson;

public class Grua {
    private int Id;
    private String Name;
    private String Description;
    private String imageURL;

    public Grua() {
    }

    public Grua(String name, String urlImg) {
        Name = name;
        imageURL = urlImg;
    }

    public Grua(String name, String description, String imageURL) {
        Name = name;
        Description = description;
        this.imageURL = imageURL;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
