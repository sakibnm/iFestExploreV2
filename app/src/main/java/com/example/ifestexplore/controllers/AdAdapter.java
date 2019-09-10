package com.example.ifestexplore.controllers;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdHolder> {

    private static final String TAG = "demo";
    private ArrayList<Ad> adArrayList;
    private ArrayList<Ad> favAdArrayList;
    private Context mContext;
    public MyClickListener myClickListener;
    FirebaseFirestore db;

    public AdAdapter(ArrayList<Ad> adArrayList, ArrayList<Ad> favAdArrayList, Context mContext, MyClickListener myClickListener) {
        this.adArrayList = adArrayList;
        this.favAdArrayList = favAdArrayList;
        this.mContext = mContext;
        this.myClickListener = myClickListener;
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
        AdAdapter.AdHolder holder = new AdAdapter.AdHolder(view, new MyClickListener() {
            @Override
            public void onFavoriteClicked(int position, final View view) {
//                Toast.makeText(view.getContext(), "Favorite clicked: from "+view.getResources().get, Toast.LENGTH_SHORT).show();

                String textFav = ((Button)view.findViewById(R.id.button_rec_favorite)).getText().toString().trim();

                final Ad favAd = adArrayList.get(position);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String currentEmail = user.getEmail();
                db = FirebaseFirestore.getInstance();

                DocumentReference favAdReference = db.collection("favoriteAds").document(currentEmail)
                        .collection("favorites").document(favAd.getAdSerialNo());

                if (textFav.equals("Mark Favorite")){


                    favAdReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (!documentSnapshot.exists()){
                                db.collection("favoriteAds").document(currentEmail).collection("favorites")
                                        .document(favAd.getAdSerialNo())
                                        .set(favAd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        view.findViewById(R.id.button_rec_favorite).setBackground(view.getResources()
                                                .getDrawable(R.drawable.button__background_favorite_round));

                                        ((Button) view.findViewById(R.id.button_rec_favorite)).setText("Undo Favorite");
                                        Toast.makeText(mContext, "Added to Favorites!", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "Could not add to Favorites, try again!", Toast.LENGTH_SHORT).show();
                                        view.findViewById(R.id.button_rec_favorite).setBackground(view.getResources()
                                                .getDrawable(R.drawable.button__background_unfavorite_round));
                                    }
                                });
                            }else{

                            }
                        }
                    });

                }else{

                    favAdReference.delete();
                    Toast.makeText(mContext, "Undo Favorite succeeded!", Toast.LENGTH_SHORT).show();
                    view.findViewById(R.id.button_rec_favorite).setBackground(view.getResources()
                            .getDrawable(R.drawable.button__background_unfavorite_round));
                    ((Button) view.findViewById(R.id.button_rec_favorite)).setText("Mark Favorite");
                }
//                    Toast.makeText(mContext, "Got Mark Favorite!", Toast.LENGTH_SHORT).show();

//                Toast.makeText(mContext, "Button saus, "+textFav, Toast.LENGTH_SHORT).show();
                

                
//                toggleButtonBackground(view);

            }

            @Override
            public void onForwardClicked(int position, View view) {

            }

        });
        //        return new AdAdapter.AdHolder(view);

        return holder;
    }

    private void toggleButtonBackground(View view) {

    }

    @Override
    public void onBindViewHolder(@NonNull AdHolder holder, int position) {
        Ad ad = adArrayList.get(position);

        for (Ad favAd: favAdArrayList){
            Log.d(TAG, "FAV: "+favAd.toString());
            if (ad.getAdSerialNo().equals(favAd.getAdSerialNo())){
                Log.d(TAG, "FAVVVVVV: "+favAd.toString());
                holder.button_rec_favorite.setText("Undo Favorite");
                holder.button_rec_favorite.setBackgroundResource(R.drawable.button__background_favorite_round);
            }
        }

//      TODO: FILTER FOR USERS NEEDED...
        // Set the data to the views here
        holder.tv_rec_comment.setText(ad.getComment());
        holder.tv_rec_title.setText(ad.getTitle());
        String urlPhoto = String.valueOf(ad.getItemPhotoURL());

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

    public ArrayList<Ad> getFavAdArrayList() {
        return favAdArrayList;
    }

    public void setFavAdArrayList(ArrayList<Ad> favAdArrayList) {
        this.favAdArrayList = favAdArrayList;
    }

    public class AdHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_rec_comment;
        private TextView tv_rec_title;
        private ImageView iv_rec_image;
        private Button button_rec_favorite;
        private Button button_rec_Forward;


        MyClickListener myClickListener;
        public AdHolder(@NonNull View itemView, MyClickListener myClickListener) {
            super(itemView);
            tv_rec_comment = itemView.findViewById(R.id.tv_rec_item_comment);
            tv_rec_title = itemView.findViewById(R.id.tv_rec_title);
            iv_rec_image = itemView.findViewById(R.id.iv_rec_item_image);
            button_rec_favorite = itemView.findViewById(R.id.button_rec_favorite);
            button_rec_Forward = itemView.findViewById(R.id.button_rec_forward);
            this.myClickListener = myClickListener;

            button_rec_favorite.setOnClickListener(this);
            button_rec_Forward.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button_rec_favorite:
                    myClickListener.onFavoriteClicked(this.getLayoutPosition(), view);
                    break;
                case R.id.button_rec_forward:
                    myClickListener.onForwardClicked(this.getLayoutPosition(), view);
                    break;
                default:
                    break;

            }
        }
    }

    public interface MyClickListener{
        void onFavoriteClicked(int position, View view);
        void onForwardClicked(int position, View view);
    }
}
