package com.example.ifestexplore.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ad {
    private String creatorEmail;
    private String adSerialNo;
    private String userPhotoURL;
    private String itemPhotoURL;
    private String comment;
    private ArrayList<String> usersForwarded;

    private Map<String, Object> hashMap;


    public Ad(){

    }

    public Ad(String creatorEmail, String adSerialNo, String userPhotoURL, String itemPhotoURL, String comment, ArrayList<String> usersForwarded) {
        this.creatorEmail = creatorEmail;
        this.adSerialNo = adSerialNo;
        this.userPhotoURL = userPhotoURL;
        this.itemPhotoURL = itemPhotoURL;
        this.comment = comment;
        this.usersForwarded = usersForwarded;
    }

    public Map toHashMap(){
        this.hashMap = new HashMap<>();

        this.hashMap.put("creator", this.creatorEmail);
        this.hashMap.put("adSerialNo", this.adSerialNo);
        this.hashMap.put("itemPhotoURL", this.itemPhotoURL);
        this.hashMap.put("comment", this.comment);
        this.hashMap.put("userPhotoURL", this.userPhotoURL);

        return this.hashMap;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
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

    public String getItemPhotoURL() {
        return itemPhotoURL;
    }

    public void setItemPhotoURL(String itemPhotoURL) {
        this.itemPhotoURL = itemPhotoURL;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<String> getUsersForwarded() {
        return usersForwarded;
    }

    public void setUsersForwarded(ArrayList<String> usersForwarded) {
        this.usersForwarded = usersForwarded;
    }


}
