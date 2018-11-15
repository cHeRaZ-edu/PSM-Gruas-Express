package com.psm.edu.psm_gruas_express.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class MarkerUser {
    private int id;
    private String name;
    private String nickname;
    private String jsonLatLng;
    private int mode;

    public MarkerUser() {
    }

    public MarkerUser(int id, String name, String nickname, String jsonLatLng, int mode) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.jsonLatLng = jsonLatLng;
        this.mode = mode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getJsonLatLng() {
        return jsonLatLng;
    }

    public void setJsonLatLng(String jsonLatLng) {
        this.jsonLatLng = jsonLatLng;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    public  String toJSON() {
        return new Gson().toJson(this);
    }

    public LatLng getLatitudeLongitude() {
        Position p = new Gson().fromJson(jsonLatLng, Position.class);
        return new LatLng(p.getLat(),p.getLng());
    }
}
