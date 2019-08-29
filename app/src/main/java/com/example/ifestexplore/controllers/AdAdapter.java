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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdHolder> {

    private static final String TAG = "demo";
    private ArrayList<Ad> adArrayList;
    private Context mContext;


    public AdAdapter(ArrayList<Ad> adArrayList, Context mContext) {
        this.adArrayList = adArrayList;
        this.mContext = mContext;
    }

    public void clear(){
        adArrayList.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Ad> adsList){
        adArrayList.addAll(adsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.received_ad_cell, parent, false);
        return new AdAdapter.AdHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdHolder holder, int position) {
        Ad ad = adArrayList.get(position);
//TODO: FILTER FOR USERS NEEDED...
        // Set the data to the views here
        holder.tv_rec_comment.setText(ad.getComment());
        holder.tv_rec_title.setText(ad.getTitle());
        String urlPhoto = String.valueOf(ad.getItemPhotoURL());
//        try {
//            urlPhoto = URLEncoder.encode(ad.getItemPhotoURL(), "UTF-8");
//            Log.d(TAG, "BALCHAL: "+urlPhoto);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        if (urlPhoto!=null && !urlPhoto.equals(""))Picasso.get().load(urlPhoto).into(holder.iv_rec_image);
//        if (urlPhoto!=null && !urlPhoto.equals(""))Picasso.get().load(urlPhoto).into(holder.iv_rec_image);
        Log.d(TAG, "onBindViewHolder: "+ad.toString());

        // You can set click listners to indvidual items in the viewholder here
        // make sure you pass down the listner or make the Data members of the viewHolder public

    }

    @Override
    public int getItemCount() {
        return adArrayList == null? 0: adArrayList.size();
    }

    public void setAdArrayList(ArrayList<Ad> adArrayList) {
        this.adArrayList = adArrayList;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public class AdHolder extends RecyclerView.ViewHolder {
        private TextView tv_rec_comment;
        private TextView tv_rec_title;
        private ImageView iv_rec_image;
        private Button button_rec_favorite;
        private Button button_rec_Forward;
        public AdHolder(@NonNull View itemView) {
            super(itemView);
            tv_rec_comment = itemView.findViewById(R.id.tv_rec_item_comment);
            tv_rec_title = itemView.findViewById(R.id.tv_rec_title);
            iv_rec_image = itemView.findViewById(R.id.iv_rec_item_image);
            button_rec_favorite = itemView.findViewById(R.id.button_rec_favorite);
            button_rec_Forward = itemView.findViewById(R.id.button_rec_forward);
        }

    }
}
