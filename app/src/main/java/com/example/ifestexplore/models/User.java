package com.example.ifestexplore.models;

public class User {

    private String name;
    private String email;
    private String password;
    String photoURL;

    User(){

    }

    public User(String name, String email, String photoURL, String password) {
        this.name = name;
        this.email = email;
        this.photoURL = photoURL;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", photoURL='" + photoURL + '\'' +
                '}';
    }
}
