package com.example.ifestexplore.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifestexplore.R;
import com.example.ifestexplore.models.Ad;
import com.example.ifestexplore.models.Events;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyAdAdapter extends RecyclerView.Adapter<MyAdAdapter.AdHolder> {
    private static final String TAG = "demo";
    private ArrayList<Ad> adArrayList;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private MyPostsClickListener myPostsClickListener;
    FirebaseFirestore db;


    public MyAdAdapter(ArrayList<Ad> adArrayList, Context mContext, MyPostsClickListener myPostsClickListener) {
        this.adArrayList = adArrayList;
        this.mContext = mContext;
        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
        this.myPostsClickListener = myPostsClickListener;
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
        MyAdAdapter.AdHolder holder = new MyAdAdapter.AdHolder(view, new MyPostsClickListener() {
            @Override
            public void onStopPostingClicked(int position, View view) {
                final Ad clickedAd = adArrayList.get(position);
                final String clickedAdSerial = clickedAd.getAdSerialNo();




                db = FirebaseFirestore.getInstance();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                if (clickedAd.getActiveFlag().equals("active")){

                    //                    LOGGING DATA....EVENT.....
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String datetime = formatter.format(date);
                    Events event = new Events(datetime, "Stopped posting ad: "+clickedAd.getAdSerialNo()+" "+clickedAd.getTitle());
                    db.collection("users").document(user.getEmail()).collection("events").add(event);

                    Log.d(TAG, "CLICKED AD: "+clickedAd.toString());

                    builder.setTitle("Stop posting");
                    builder.setMessage("Are you sure want to stop posting this review?");

                    builder.setPositiveButton("Yes, stop posting!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clickedAd.setActiveFlag("deleted");
                            db.collection("deletedAds")
                                    .document(clickedAd.getCreatorEmail())
                                    .collection("deleted")
                                    .document(clickedAdSerial).set(clickedAd)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("adsRepo")
                                                    .document(clickedAdSerial)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(mContext, "The post is stopped!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: Could not remove the review: "+e.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(mContext, "The review is not removed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{

                    //                    LOGGING DATA....EVENT.....
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String datetime = formatter.format(date);
                    Events event = new Events(datetime, "Resumed posting ad: "+clickedAd.getAdSerialNo()+" "+clickedAd.getTitle());
                    db.collection("users").document(user.getEmail()).collection("events").add(event);

                    builder.setTitle("Resume posting");
                    builder.setMessage("Are you sure want to resume posting this review?");

                    builder.setPositiveButton("Yes, resume posting!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "CLICKED AD: "+clickedAd.toString());
                            clickedAd.setActiveFlag("active");

                            db.collection("deletedAds")
                                    .document(clickedAd.getCreatorEmail())
                                    .collection("deleted")
                                    .document(clickedAdSerial)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("adsRepo")
                                                    .document(clickedAdSerial)
                                                    .set(clickedAd)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContext, "The review is resumed again!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                        }
                                    });

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(mContext, "The review is not resumed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


//                db.collection("adsRepo").document(clickedAdSerial).delete();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdAdapter.AdHolder holder, int position) {
        Ad ad = adArrayList.get(position);
        Log.d(TAG, "onBindViewHolder: "+ad.getTitle());
        holder.tv_my_posts_comment.setText(ad.getComment());
        holder.tv_my_posts_title.setText(ad.getTitle());
        holder.tv_my_posts_me.setText(user.getDisplayName());
        holder.tv_my_posts_booth_name.setText("At booth "+ad.getBoothName());
        holder.iv_my_posts_booth_flag.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(ad.getBoothFlag()), null));
        if (ad.getActiveFlag().equals("deleted")){
            holder.button_my_posts_stop.setText("Resume Posting");
            holder.button_my_posts_stop.setBackgroundResource(R.drawable.button_round);

        }else{
            holder.button_my_posts_stop.setText("Stop Posting");
            holder.button_my_posts_stop.setBackgroundResource(R.drawable.button_round_cancel);

        }
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


    public class AdHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_my_posts_comment;
        private TextView tv_my_posts_me;
        private TextView tv_my_posts_title;
        private ImageView iv_my_posts_image;
        private ImageView iv_my_photo_image;
        private Button button_my_posts_stop;
        private TextView tv_my_posts_booth_name;
        private ImageView iv_my_posts_booth_flag;
        private  MyPostsClickListener myPostsClickListener;

        public AdHolder(@NonNull View itemView, MyPostsClickListener myPostsClickListener) {
            super(itemView);
            tv_my_posts_comment = itemView.findViewById(R.id.tv_my_comment);
            tv_my_posts_me = itemView.findViewById(R.id.tv_my_username);
            tv_my_posts_title = itemView.findViewById(R.id.tv_my_Title);
            iv_my_posts_image = itemView.findViewById(R.id.iv_my_userphoto);
            iv_my_photo_image = itemView.findViewById(R.id.iv_my_photo);
            tv_my_posts_booth_name = itemView.findViewById(R.id.tv_my_place);
            iv_my_posts_booth_flag = itemView.findViewById(R.id.iv_my_flag);
            button_my_posts_stop = itemView.findViewById(R.id.button_my_Stop_Posting);
            this.myPostsClickListener = myPostsClickListener;

            button_my_posts_stop.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button_my_Stop_Posting:
                    myPostsClickListener.onStopPostingClicked(this.getLayoutPosition(), view);
                    break;
                default:
                    break;
            }
        }
    }

    public interface MyPostsClickListener{
        void onStopPostingClicked(int position, View view);
    }

}
