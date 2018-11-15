package com.psm.edu.psm_gruas_express.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class Position {
    private double Lat;
    private double Lng;

    public Position(LatLng latLng) {
        Lat = latLng.latitude;
        Lng = latLng.longitude;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
