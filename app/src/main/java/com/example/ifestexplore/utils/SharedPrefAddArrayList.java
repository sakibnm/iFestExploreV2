package com.example.ifestexplore.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ifestexplore.models.Ad;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefAddArrayList {
    Context ctx;
    String key;

    public SharedPrefAddArrayList(Context ctx, String key) {
        this.ctx = ctx;
        this.key = key;
    }

    public void saveAdArrayList(Object object){
        SharedPreferences prefs = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        editor.putString(key, json);
        editor.commit();
    }

    public ArrayList<Ad> getAdArrayList(){
        ArrayList<Ad> adArrayList = new ArrayList<>();

        SharedPreferences prefs = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        Type type = new TypeToken<ArrayList<Ad>>(){}.getType();

        adArrayList = gson.fromJson(json, type);

        return adArrayList;
    }
}
