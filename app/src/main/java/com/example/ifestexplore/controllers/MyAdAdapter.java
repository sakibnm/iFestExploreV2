package com.example.ifestexplore.controllers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifestexplore.R;
import com.example.ifestexplore.models.Ad;
import com.google.api.LogDescriptor;

import java.util.ArrayList;

public class MyAdAdapter extends RecyclerView.Adapter<MyAdAdapter.AdHolder> {
    private static final String TAG = "demo";
    private ArrayList<Ad> adArrayList;
    private Context mContext;

    public MyAdAdapter(ArrayList<Ad> adArrayList, Context mContext) {
        this.adArrayList = adArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyAdAdapter.AdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.my_posts_cell, parent, false);
        return new MyAdAdapter.AdHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdAdapter.AdHolder holder, int position) {
        Ad ad = adArrayList.get(position);

        Log.d(TAG, "onBindViewHolder: "+ad.toString());
    }

    @Override
    public int getItemCount() {
        return adArrayList == null? 0: adArrayList.size();
    }


    public class AdHolder extends RecyclerView.ViewHolder {
        private TextView tv_my_posts_comment;
        private TextView tv_my_posts_users;
        private ImageView iv_my_posts_image;
        private Button button_my_posts_View;
        private Button button_my_posts_stop;

        public AdHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
