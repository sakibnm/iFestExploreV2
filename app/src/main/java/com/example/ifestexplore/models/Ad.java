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

    @Override
    public String toString() {
        return "Ad{" +
                "creatorEmail='" + creatorEmail + '\'' +
                ", adSerialNo='" + adSerialNo + '\'' +
                ", userPhotoURL='" + userPhotoURL + '\'' +
                ", itemPhotoURL='" + itemPhotoURL + '\'' +
                ", comment='" + comment + '\'' +
                ", usersForwarded=" + usersForwarded +
                ", hashMap=" + hashMap +
                '}';
    }

    public Ad(Map<String, Object> map){
        this.creatorEmail = (String) map.get("creator");
        this.adSerialNo = (String) map.get("adSerialNo");
        this.userPhotoURL = (String) map.get("userPhotoURL");
        this.itemPhotoURL = (String) map.get("itemPhotoURL");
        this.comment = (String) map.get("comment");
//        TODO: change null to list...
        this.usersForwarded = null;
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
