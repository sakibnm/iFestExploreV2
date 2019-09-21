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
import com.example.ifestexplore.fragments.Bookmarks;
import com.example.ifestexplore.models.Ad;
import com.example.ifestexplore.models.Events;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FavAdAdapter extends RecyclerView.Adapter<FavAdAdapter.AdHolder> {

    private static final String TAG = "demo";
    private ArrayList<Ad> favArrayList;
    private Context mContext;
    public FavAdAdapter.MyFavClickListener myClickListener;
    FirebaseFirestore db;
    FirebaseUser user;

    public FavAdAdapter(ArrayList<Ad> favArrayList, Context mContext, MyFavClickListener myClickListener) {
        this.favArrayList = favArrayList;
        this.mContext = mContext;
        this.myClickListener = myClickListener;
    }

    public void clear(){
        favArrayList.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Ad> adsList){
        favArrayList.addAll(adsList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public AdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.favorite_posts_cell, parent, false);
        FavAdAdapter.AdHolder holder = new FavAdAdapter.AdHolder(view, new MyFavClickListener() {
            @Override
            public void onRemove(final int position, final View view) {
                final Ad favAd = favArrayList.get(position);


                //                    LOGGING DATA....EVENT.....
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String datetime = formatter.format(date);
                Events event = new Events(datetime, "Removed From Favorites: "+favAd.getAdSerialNo()+" "+favAd.getTitle());
                db = FirebaseFirestore.getInstance();
                db.collection("users").document(user.getEmail()).collection("events").add(event);


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String currentEmail = user.getEmail();


                final DocumentReference favAdReference = db.collection("favoriteAds").document(currentEmail)
                        .collection("favorites").document(favAd.getAdSerialNo());

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Stop posting");
                builder.setMessage("Are you sure want remove the review from your favorites?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        favAdReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    favAdReference.delete();
                                    Toast.makeText(mContext, "Removed from Favorites!", Toast.LENGTH_SHORT).show();
                                    Bookmarks.adAdapter.notifyDataSetChanged();
                                    favArrayList.remove(position);
                                }else{

                                }
                            }
                        });

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(mContext, "Not removed!", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();




            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdHolder holder, int position) {
        Ad ad = favArrayList.get(position);

        holder.tv_fav_title.setText(ad.getTitle());
        holder.tv_fav_comment.setText(ad.getComment());
        holder.tv_fav_creator.setText(ad.getCreatorName());
        holder.tv_fav_booth_name.setText("At booth "+ad.getBoothName());
        if (ad.getBoothFlag()!=null)holder.iv_fav_booth_flag.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(ad.getBoothFlag()), null));

        Log.d(TAG, "FAVORITES: "+ad.toString());
        String urlPhoto = String.valueOf(ad.getItemPhotoURL());

        if (urlPhoto!=null && !urlPhoto.equals("")) Picasso.get().load(urlPhoto).into(holder.iv_fav_image);
        String urlPhotoCreator = String.valueOf(ad.getUserPhotoURL());

        if (urlPhotoCreator!=null && !urlPhoto.equals("")) Picasso.get().load(urlPhotoCreator).into(holder.iv_fav_creator);
//        if (urlPhoto!=null && !urlPhoto.equals(""))Picasso.get().load(urlPhoto).into(holder.iv_rec_image);
//        Log.d(TAG, "onBindViewHolder: "+ad.toString());
    }

    @Override
    public int getItemCount() {
        return favArrayList == null? 0: favArrayList.size();
    }

    public ArrayList<Ad> getFavArrayList() {
        return favArrayList;
    }

    public void setFavArrayList(ArrayList<Ad> favArrayList) {
        this.favArrayList = favArrayList;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public MyFavClickListener getMyClickListener() {
        return myClickListener;
    }

    public void setMyClickListener(MyFavClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    public class AdHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tv_fav_creator;
        private TextView tv_fav_comment;
        private TextView tv_fav_title;
        private ImageView iv_fav_image;
        private ImageView iv_fav_creator;
        private Button button_fav_unfavorite;
        private TextView tv_fav_booth_name;
        private ImageView iv_fav_booth_flag;

        MyFavClickListener myClickListener;
        public AdHolder(@NonNull View itemView, MyFavClickListener myClickListener) {
            super(itemView);
            tv_fav_creator = itemView.findViewById(R.id.tv_fav_creator_name);
            tv_fav_comment = itemView.findViewById(R.id.tv_fav_comment);
            tv_fav_title = itemView.findViewById(R.id.tv_fav_Title);
            iv_fav_image = itemView.findViewById(R.id.iv_fav_item_image);
            iv_fav_creator = itemView.findViewById(R.id.iv_fav_creator);
            button_fav_unfavorite = itemView.findViewById(R.id.button_fav_delete);
            tv_fav_booth_name = itemView.findViewById(R.id.tv_fav_place);
            iv_fav_booth_flag = itemView.findViewById(R.id.iv_fav_flag);
            this.myClickListener = myClickListener;

            button_fav_unfavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button_fav_delete:
                    myClickListener.onRemove(this.getLayoutPosition(), view);
                    break;
                default:
                    break;
            }

        }
    }

    public interface MyFavClickListener{
        void onRemove(int position, View view);
    }
}
