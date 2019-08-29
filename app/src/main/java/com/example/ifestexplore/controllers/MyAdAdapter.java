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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdAdapter extends RecyclerView.Adapter<MyAdAdapter.AdHolder> {
    private static final String TAG = "demo";
    private ArrayList<Ad> adArrayList;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    public MyAdAdapter(ArrayList<Ad> adArrayList, Context mContext) {
        this.adArrayList = adArrayList;
        this.mContext = mContext;
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
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
    public MyAdAdapter.AdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.my_posts_cell, parent, false);
        return new MyAdAdapter.AdHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdAdapter.AdHolder holder, int position) {
        Ad ad = adArrayList.get(position);
        Log.d(TAG, "onBindViewHolder: "+ad.getTitle());
        holder.tv_my_posts_comment.setText(ad.getComment());
        holder.tv_my_posts_title.setText(ad.getTitle());
        holder.tv_my_posts_me.setText(user.getDisplayName());
        String urlPhoto = String.valueOf(ad.getItemPhotoURL());
        String urlMyPhoto = String.valueOf(ad.getUserPhotoURL());
//        holder.tv_my_posts_users.setText(ad.getUsersForwarded().toString());
//        if (urlPhoto!=null && !urlPhoto.equals(""))
          Picasso.get().load(urlPhoto).into(holder.iv_my_posts_image);
          Picasso.get().load(urlMyPhoto).into(holder.iv_my_photo_image);

//        TODO: stop button logic should be added......


    }

    @Override
    public int getItemCount() {
        return adArrayList == null? 0: adArrayList.size();
    }

    public void setAdArrayList(ArrayList<Ad> adArrayList) {
        this.adArrayList = adArrayList;
    }


    public class AdHolder extends RecyclerView.ViewHolder {
        private TextView tv_my_posts_comment;
        private TextView tv_my_posts_me;
        private TextView tv_my_posts_title;
        private ImageView iv_my_posts_image;
        private ImageView iv_my_photo_image;
        private Button button_my_posts_stop;

        public AdHolder(@NonNull View itemView) {
            super(itemView);
            tv_my_posts_comment = itemView.findViewById(R.id.tv_my_comment);
            tv_my_posts_me = itemView.findViewById(R.id.tv_my_username);
            tv_my_posts_title = itemView.findViewById(R.id.tv_my_Title);
            iv_my_posts_image = itemView.findViewById(R.id.iv_my_userphoto);
            iv_my_photo_image = itemView.findViewById(R.id.iv_my_photo);
            button_my_posts_stop = itemView.findViewById(R.id.button_my_Stop_Posting);
        }
    }

}
