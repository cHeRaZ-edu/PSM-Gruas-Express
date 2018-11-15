package com.psm.edu.psm_gruas_express.models;

import com.google.gson.Gson;

public class Calificacion {
    private int idUser;
    private int idGrua;
    private int vote_5;
    private int vote_4;
    private int vote_3;
    private int vote_2;
    private int vote_1;
    private String message;
    private String imageURL;

    public Calificacion(int idUser, int idGrua, int vote_5, int vote_4, int vote_3, int vote_2, int vote_1, String message, String imageURL) {
        this.idUser = idUser;
        this.idGrua = idGrua;
        this.vote_5 = vote_5;
        this.vote_4 = vote_4;
        this.vote_3 = vote_3;
        this.vote_2 = vote_2;
        this.vote_1 = vote_1;
        this.message = message;
        this.imageURL = imageURL;
    }

    public Calificacion(int idUser, int idGrua, String message, String imageURL) {
        this.idUser = idUser;
        this.idGrua = idGrua;
        this.message = message;
        this.imageURL = imageURL;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdGrua() {
        return idGrua;
    }

    public void setIdGrua(int idGrua) {
        this.idGrua = idGrua;
    }

    public int getVote_5() {
        return vote_5;
    }

    public void setVote_5(int vote_5) {
        this.vote_5 = vote_5;
    }

    public int getVote_4() {
        return vote_4;
    }

    public void setVote_4(int vote_4) {
        this.vote_4 = vote_4;
    }

    public int getVote_3() {
        return vote_3;
    }

    public void setVote_3(int vote_3) {
        this.vote_3 = vote_3;
    }

    public int getVote_2() {
        return vote_2;
    }

    public void setVote_2(int vote_2) {
        this.vote_2 = vote_2;
    }

    public int getVote_1() {
        return vote_1;
    }

    public void setVote_1(int vote_1) {
        this.vote_1 = vote_1;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public float getRating() {
        return (float)(vote_1 + vote_2*2 + vote_3*3 + vote_4*4 + vote_5*5);
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
