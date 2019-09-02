package com.example.ifestexplore.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Ad {
    private String creatorEmail;
    private String forwarderEmail;
    private String adSerialNo;
    private String userPhotoURL;
    private String itemPhotoURL;
    private String title;
    private String comment;
    private ArrayList<String> usersForwarded;

    public Map<String, Object> getHashMap() {
        return hashMap;
    }

    public void setHashMap(Map<String, Object> hashMap) {
        this.hashMap = hashMap;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "creatorEmail='" + creatorEmail + '\'' +
                ", adSerialNo='" + adSerialNo + '\'' +
                ", userPhotoURL='" + userPhotoURL + '\'' +
                ", itemPhotoURL='" + itemPhotoURL + '\'' +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                ", usersForwarded=" + usersForwarded +
                ", hashMap=" + hashMap +
                '}';
    }



    private Map<String, Object> hashMap;


    public Ad(){

    }

    public Ad(String creatorEmail, String forwarderEmail, String adSerialNo, String userPhotoURL, String itemPhotoURL, String title, String comment, ArrayList<String> usersForwarded) {
        this.creatorEmail = creatorEmail;
        this.forwarderEmail = forwarderEmail;
        this.adSerialNo = adSerialNo;
        this.userPhotoURL = userPhotoURL;
        this.itemPhotoURL = itemPhotoURL;
        this.title = title;
        this.comment = comment;
        this.usersForwarded = usersForwarded;
    }

    public Ad(Map<String, Object> map){
        this.creatorEmail = (String) map.get("creator");
        this.forwarderEmail = (String) map.get("fwdBy");
        this.adSerialNo = (String) map.get("adSerialNo");
        this.userPhotoURL = (String) map.get("userPhotoURL");
        this.itemPhotoURL = (String) map.get("itemPhotoURL");
        this.title = (String) map.get("title");
        this.comment = (String) map.get("comment");
//        TODO: change null to list.
        this.usersForwarded = null;
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
        this.hashMap.put("adSerialNo", this.adSerialNo);
        this.hashMap.put("itemPhotoURL", this.itemPhotoURL);
        this.hashMap.put("title", this.title);
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
    public String getForwarderEmail() {
        return forwarderEmail;
    }
    public void setForwarderEmail(String forwarderEmail) {
        this.forwarderEmail = forwarderEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad ad = (Ad) o;
        return Objects.equals(creatorEmail, ad.creatorEmail) &&
                Objects.equals(adSerialNo, ad.adSerialNo) &&
                Objects.equals(title, ad.title) &&
                Objects.equals(forwarderEmail, ad.forwarderEmail) &&
                Objects.equals(comment, ad.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creatorEmail, adSerialNo, title, comment);
    }
}
