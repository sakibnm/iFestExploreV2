package com.example.ifestexplore.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Ad {
    private String creatorEmail;
    private String creatorName;
    private String adSerialNo;
    private String userPhotoURL;
    private String itemPhotoURL;
    private String title;
    private String comment;
    private String boothName;
    private String boothFlag;
    private String activeFlag;
//    private ArrayList<String> usersForwarded;

    public Ad(String creatorEmail, String creatorName, String adSerialNo, String userPhotoURL, String itemPhotoURL, String title, String comment, String boothName, String boothFlag, String activeFlag) {
        this.creatorEmail = creatorEmail;
        this.creatorName = creatorName;
        this.adSerialNo = adSerialNo;
        this.userPhotoURL = userPhotoURL;
        this.itemPhotoURL = itemPhotoURL;
        this.title = title;
        this.comment = comment;
        this.boothName = boothName;
        this.boothFlag = boothFlag;
//        this.usersForwarded = usersForwarded;
        this.activeFlag = activeFlag;
    }



    public Map<String, Object> getHashMap() {
        return hashMap;
    }

    public void setHashMap(Map<String, Object> hashMap) {
        this.hashMap = hashMap;
    }

    public Ad(String creatorEmail, String creatorName, String adSerialNo, String userPhotoURL, String itemPhotoURL, String title, String comment, String boothName, String boothFlag, String activeFlag, Map<String, Object> hashMap) {
        this.creatorEmail = creatorEmail;
        this.creatorName = creatorName;
        this.adSerialNo = adSerialNo;
        this.userPhotoURL = userPhotoURL;
        this.itemPhotoURL = itemPhotoURL;
        this.title = title;
        this.comment = comment;
        this.boothName = boothName;
        this.boothFlag = boothFlag;
        this.activeFlag = activeFlag;
        this.hashMap = hashMap;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "creatorEmail='" + creatorEmail + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", adSerialNo='" + adSerialNo + '\'' +
                ", userPhotoURL='" + userPhotoURL + '\'' +
                ", itemPhotoURL='" + itemPhotoURL + '\'' +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                ", boothName='" + boothName + '\'' +
                ", boothFlag=" + boothFlag +
                ", hashMap=" + hashMap +
                '}';
    }

    private Map<String, Object> hashMap;


    public Ad(){

    }



    public Ad(Map<String, Object> map){
        this.creatorEmail = (String) map.get("creator");
        this.creatorName = (String) map.get("creatorName");
        this.boothName = (String) map.get("boothName");
        this.adSerialNo = (String) map.get("adSerialNo");
        this.userPhotoURL = (String) map.get("userPhotoURL");
        this.boothFlag = (String) map.get("boothFlag");
        this.itemPhotoURL = (String) map.get("itemPhotoURL");
        this.title = (String) map.get("title");
        this.comment = (String) map.get("comment");
        this.activeFlag = (String) map.get("activeFlag");
//        TODO: change null to list.

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Map toHashMap(){
        this.hashMap = new HashMap<>();

        this.hashMap.put("creator", this.creatorEmail);
        this.hashMap.put("creatorName", this.creatorName);
        this.hashMap.put("boothName", this.boothName);
        this.hashMap.put("adSerialNo", this.adSerialNo);
        this.hashMap.put("itemPhotoURL", this.itemPhotoURL);
        this.hashMap.put("title", this.title);
        this.hashMap.put("comment", this.comment);
        this.hashMap.put("userPhotoURL", this.userPhotoURL);
        this.hashMap.put("boothFlag", this.boothFlag);
        this.hashMap.put("activeFlag", this.activeFlag);

        return this.hashMap;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
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

    public String getBoothName() {
        return boothName;
    }

    public void setBoothName(String boothName) {
        this.boothName = boothName;
    }

    public String getBoothFlag() {
        return boothFlag;
    }

    public void setBoothFlag(String boothFlag) {
        this.boothFlag = boothFlag;
    }

    public String getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad ad = (Ad) o;
        return Objects.equals(creatorEmail, ad.creatorEmail) &&
                Objects.equals(adSerialNo, ad.adSerialNo) &&
                Objects.equals(title, ad.title) &&
                Objects.equals(comment, ad.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creatorEmail, adSerialNo, title, comment);
    }
}
