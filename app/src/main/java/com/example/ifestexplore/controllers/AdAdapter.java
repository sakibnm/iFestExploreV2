package com.example.ifestexplore.controllers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifestexplore.R;
import com.example.ifestexplore.models.Ad;

import java.util.ArrayList;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdHolder> {

    private static final String TAG = "demo";
    private ArrayList<Ad> adArrayList;
    private Context mContext;


    public AdAdapter(ArrayList<Ad> adArrayList, Context mContext) {
        this.adArrayList = adArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.received_ad_cell, parent, false);
        return new AdHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdHolder holder, int position) {
        Ad ad = adArrayList.get(position);
        // Set the data to the views here

        Log.d(TAG, "onBindViewHolder: "+ad.toString());

        // You can set click listners to indvidual items in the viewholder here
        // make sure you pass down the listner or make the Data members of the viewHolder public

    }

    @Override
    public int getItemCount() {
        return adArrayList == null? 0: adArrayList.size();
    }

    public class AdHolder extends RecyclerView.ViewHolder {
        private TextView tv_rec_comment;
        private TextView tv_rec_serial;
        public AdHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}
