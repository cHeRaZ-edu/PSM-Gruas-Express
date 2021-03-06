package com.psm.edu.psm_gruas_express.models;

import com.google.gson.Gson;

public class User {
    private int Id;
    private String Name;
    private String LastName;
    private String Nickname;
    private String Email;
    private String Password;
    private String imageURL;
    private String imageBackgroundURL;
    private String Provider;//google.com,twitter.com,facebook.com
    private String Phone;

    public User() {
    }

    public User(String name, String lastName, String nickname, String email, String Password) {
        Name = name;
        LastName = lastName;
        Nickname = nickname;
        Email = email;
        this.Password = Password;
    }

    public User(String name, String lastName, String nickname, String email, String Password, String imageURL) {
        Name = name;
        LastName = lastName;
        Nickname = nickname;
        Email = email;
        this.Password = Password;
        this.imageURL = imageURL;
    }

    public User(String name, String lastName, String nickname, String email, String password, String imageURL, String provider) {
        Name = name;
        LastName = lastName;
        Nickname = nickname;
        Email = email;
        Password = password;
        this.imageURL = imageURL;
        Provider = provider;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageBackgroundURL() {
        return imageBackgroundURL;
    }

    public void setImageBackgroundURL(String imageBackgroundURL) {
        this.imageBackgroundURL = imageBackgroundURL;
    }

    public String getProvider() {
        return Provider;
    }

    public void setProvider(String provider) {
        Provider = provider;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
