package com.example.ifestexplore.models;

import com.example.ifestexplore.models.Ad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersData {
    private String email;
    private String name;
    private String instanceID;


    private Map<String, Object> hashMap;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public UsersData(String email, String name, String instanceID) {
        this.email = email;
        this.name = name;
        this.instanceID = instanceID;

    }
    public Map toHashMap(){
        this.hashMap = new HashMap<>();

        this.hashMap.put("name", this.name);
        this.hashMap.put("email", this.email);

        this.hashMap.put("instanceID", this.instanceID);

        return this.hashMap;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }
}
