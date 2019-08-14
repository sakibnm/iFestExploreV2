package com.example.ifestexplore.models;

import java.util.ArrayList;

public class Ad {
    private String userEmail;
    private String adSerialNo;
    private String userPhotoURL;
    private ArrayList<String> users;


    public Ad(){

    }

    public Ad(String userEmail, String adSerialNo, String userPhotoURL, ArrayList<String> users) {
        this.userEmail = userEmail;
        this.adSerialNo = adSerialNo;
        this.userPhotoURL = userPhotoURL;
        this.users = users;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAdSerialNo() {
        return adSerialNo;
    }

    public void setAdSerialNo(String adSerialNo) {
        this.adSerialNo = adSerialNo;
    }

    public String getUserPhotoURL() {
        return userPhotoURL;
    }

    public void setUserPhotoURL(String userPhotoURL) {
        this.userPhotoURL = userPhotoURL;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "userEmail='" + userEmail + '\'' +
                ", adSerialNo='" + adSerialNo + '\'' +
                ", userPhotoURL='" + userPhotoURL + '\'' +
                ", users=" + users +
                '}';
    }
}
