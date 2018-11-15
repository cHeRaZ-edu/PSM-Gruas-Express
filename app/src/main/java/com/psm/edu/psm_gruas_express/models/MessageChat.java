package com.psm.edu.psm_gruas_express.models;

import com.google.gson.Gson;

public class MessageChat {
    private int Id;
    private int idUserSend;
    private int idUserRecive;
    private String message;
    private String imageURL;
    private String time_send;
    private int response; //0 envio, 1 llego

    public MessageChat(int id, int idUserSend, int idUserRecive, String message, String imageURL, String time_send, int response) {
        Id = id;
        this.idUserSend = idUserSend;
        this.idUserRecive = idUserRecive;
        this.message = message;
        this.imageURL = imageURL;
        this.time_send = time_send;
        this.response = response;
    }

    public MessageChat() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getIdUserSend() {
        return idUserSend;
    }

    public void setIdUserSend(int idUserSend) {
        this.idUserSend = idUserSend;
    }

    public int getIdUserRecive() {
        return idUserRecive;
    }

    public void setIdUserRecive(int idUserRecive) {
        this.idUserRecive = idUserRecive;
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

    public String getTime_send() {
        return time_send;
    }

    public void setTime_send(String time_send) {
        this.time_send = time_send;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
