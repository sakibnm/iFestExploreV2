package com.example.ifestexplore.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ifestexplore.models.Ad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class SharedPrefHashMap {
    Context ctx;
    String key;

    public SharedPrefHashMap(Context ctx, String key) {
        this.ctx = ctx;
        this.key = key;
    }

    public void saveHashMap(Object object){
        SharedPreferences prefs = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        editor.putString(key, json);
        editor.commit();
    }

    public HashMap<String, Boolean> getHashMap(){
        HashMap<String, Boolean> adHashMap = new HashMap<>();

        SharedPreferences prefs = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        Type type = new TypeToken<HashMap<String, Boolean>>(){}.getType();

        adHashMap = gson.fromJson(json, type);

        return adHashMap;
    }
}
