package com.example.ifestexplore.controllers;

import android.content.Context;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavAdAdapter extends RecyclerView.Adapter<FavAdAdapter.AdHolder> {

    private static final String TAG = "demo";
    private ArrayList<Ad> favArrayList;
    private Context mContext;
    public FavAdAdapter.MyFavClickListener myClickListener;
    FirebaseFirestore db;

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
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.favorite_posts_cell, parent, false);
        FavAdAdapter.AdHolder holder = new FavAdAdapter.AdHolder(view, new MyFavClickListener() {
            @Override
            public void onRemove(final int position, final View view) {
                final Ad favAd = favArrayList.get(position);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String currentEmail = user.getEmail();
                db = FirebaseFirestore.getInstance();

                final DocumentReference favAdReference = db.collection("favoriteAds").document(currentEmail)
                        .collection("favorites").document(favAd.getAdSerialNo());

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
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdHolder holder, int position) {
        Ad ad = favArrayList.get(position);

        holder.tv_fav_title.setText(ad.getTitle());
        holder.tv_fav_comment.setText(ad.getComment());

        String urlPhoto = String.valueOf(ad.getItemPhotoURL());

        if (urlPhoto!=null && !urlPhoto.equals("")) Picasso.get().load(urlPhoto).into(holder.iv_fav_image);
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
        private TextView tv_fav_comment;
        private TextView tv_fav_title;
        private ImageView iv_fav_image;
        private Button button_fav_unfavorite;
        MyFavClickListener myClickListener;
        public AdHolder(@NonNull View itemView, MyFavClickListener myClickListener) {
            super(itemView);
            tv_fav_comment = itemView.findViewById(R.id.tv_fav_comment);
            tv_fav_title = itemView.findViewById(R.id.tv_fav_Title);
            iv_fav_image = itemView.findViewById(R.id.iv_fav_item_image);
            button_fav_unfavorite = itemView.findViewById(R.id.button_fav_delete);
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
