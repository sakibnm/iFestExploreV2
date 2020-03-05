package com.example.ifestexplore.controllers;

import android.content.Context;
import android.graphics.Color;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ifestexplore.R;
import com.example.ifestexplore.fragments.Bookmarks;
import com.example.ifestexplore.fragments.ReceivedPosts;
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

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdHolder> {

    private static final String TAG = "demo";
    private ArrayList<Ad> adArrayList;
    private ArrayList<Ad> favAdArrayList;
    private Context mContext;
    public MyClickListener myClickListener;
    FirebaseFirestore db;
    FirebaseUser user;

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
            public void onFavoriteClicked(final int position, final View view) {
//                Toast.makeText(view.getContext(), "Favorite clicked: from "+view.getResources().get, Toast.LENGTH_SHORT).show();

                String textFav = ((Button)view.findViewById(R.id.button_rec_favorite)).getText().toString().trim();

                final Ad favAd = adArrayList.get(position);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String currentEmail = user.getEmail();
                db = FirebaseFirestore.getInstance();

                final DocumentReference favAdReference = db.collection("v2favoriteAds").document(currentEmail)
                        .collection("favorites").document(favAd.getAdSerialNo());

                if (textFav.equals("Mark Favorite")){

//                    LOGGING DATA....EVENT.....
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String datetime = formatter.format(date);
                    Events event = new Events(datetime, "Favorited ad: "+favAd.getAdSerialNo()+" "+favAd.getTitle());
                    db.collection("v2users").document(user.getEmail()).collection("events").add(event);

                    favAdReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (!documentSnapshot.exists()){
                                db.collection("v2favoriteAds").document(currentEmail).collection("favorites")
                                        .document(favAd.getAdSerialNo())
                                        .set(favAd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        view.findViewById(R.id.button_rec_favorite).setBackgroundColor(Color.parseColor("#FFCDD2"));

                                        ((Button) view.findViewById(R.id.button_rec_favorite)).setText("Undo Favorite");
                                        Toast.makeText(mContext, "Added to Favorites!", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "Could not add to Favorites, try again!", Toast.LENGTH_SHORT).show();
                                        view.findViewById(R.id.button_rec_favorite).setBackgroundColor(Color.parseColor("#DA0E6D"));
                                    }
                                });
                            }else{

                            }
                        }
                    });

                }else{

                    //                    LOGGING DATA....EVENT.....
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String datetime = formatter.format(date);
                    Events event = new Events(datetime, "UnFavorited ad: "+favAd.getAdSerialNo()+" "+favAd.getTitle());
                    db.collection("v2users").document(user.getEmail()).collection("events").add(event);

                    favAdReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                favAdReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Bookmarks.getUpdatedList();
                                        Toast.makeText(mContext, "Removed from Favorites!", Toast.LENGTH_SHORT).show();
                                        ReceivedPosts.getUpdatedList();

                                        ((Button) view.findViewById(R.id.button_rec_favorite)).setText("Mark Favorite");
                                        view.findViewById(R.id.button_rec_favorite).setBackgroundColor(Color.parseColor("#DA0E6D"));
                                    }
                                });

                            }else{

                            }
                        }
                    });

                }
//                    Toast.makeText(mContext, "Got Mark Favorite!", Toast.LENGTH_SHORT).show();

//                Toast.makeText(mContext, "Button saus, "+textFav, Toast.LENGTH_SHORT).show();
                

                
//                toggleButtonBackground(view);

            }

            @Override
            public void onForwardClicked(int position, final View view) {
//                DisplayProgressBar.......
//                view.getParent().findViewById(R.id.cv_forwarding).setVisibility(View.VISIBLE);


//                final Ad fwdAd = adArrayList.get(position);
//                user = FirebaseAuth.getInstance().getCurrentUser();
//                fwdAd.setForwarderEmail(user.getEmail());
//                fwdAd.setFwdPhotoURL(user.getPhotoUrl().toString());
//
//                FirebaseFirestore getDB = FirebaseFirestore.getInstance();
//                final FirebaseFirestore saveDB = FirebaseFirestore.getInstance();
//                getDB.collection("adsRepo")
//                        .document("adscounter").get()
//                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        final long current_count = (long) documentSnapshot.get("count");
//                        fwdAd.setAdSerialNo(String.valueOf(current_count));
//                        saveDB.collection("adsRepo").document(fwdAd.getAdSerialNo()).set(fwdAd.toHashMap())
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        saveDB.collection("adsRepo").document("adscounter").update("count", current_count+1);
//
////                                        view.findViewById(R.id.cv_forwarding).setVisibility(View.INVISIBLE);
////                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//
//                                    }
//                                });
//                    }
//                });
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
//            Log.d(TAG, "FAV: "+favAd.toString());
            if (ad.getAdSerialNo().equals(favAd.getAdSerialNo())){
//                Log.d(TAG, "FAVVVVVV: "+favAd.toString());
                holder.button_rec_favorite.setText("Undo Favorite");
                holder.button_rec_favorite.setBackgroundColor(Color.parseColor("#FFCDD2"));
            }
        }

//      TODO: FILTER FOR USERS NEEDED...
        // Set the data to the views here
        holder.tv_rec_creator.setText(ad.getCreatorName());
        holder.tv_rec_comment.setText(ad.getComment());
        holder.tv_rec_title.setText(ad.getTitle());
        holder.tv_booth_name.setText(ad.getBoothName());
        holder.iv_booth_flag.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(ad.getBoothFlag()), null));
        String urlPhoto = String.valueOf(ad.getItemPhotoURL());
        String urlPhotoUser = String.valueOf(ad.getUserPhotoURL());
        String urlCreatorPhoto = String.valueOf(ad.getUserPhotoURL());
//        String urlForwarderPhoto = String.valueOf(ad.getFwdPhotoURL());
//        if (!ad.getCreatorEmail().equals(ad.getForwarderEmail())){
//            holder.cv_fwd_photo.setVisibility(View.VISIBLE);
//            holder.tv_fwd.setVisibility(View.VISIBLE);
//            if (urlCreatorPhoto!=null && !urlCreatorPhoto.equals(""))Picasso.get().load(urlCreatorPhoto).into(holder.iv_creator_photo);
//            if (urlForwarderPhoto!=null && !urlForwarderPhoto.equals(""))Picasso.get().load(urlForwarderPhoto).into(holder.iv_forwarder_photo);
//        }else{
//            if (urlCreatorPhoto!=null && !urlCreatorPhoto.equals(""))Picasso.get().load(urlCreatorPhoto).into(holder.iv_creator_photo);
//        }
        if (urlPhoto!=null && !urlPhoto.equals(""))Picasso.get().load(urlPhoto).into(holder.iv_rec_image);
        if (urlPhotoUser!=null && !urlPhotoUser.equals(""))Picasso.get().load(urlPhotoUser).into(holder.iv_creator_photo);

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
        private TextView tv_rec_creator;
        private TextView tv_rec_comment;
        private TextView tv_rec_title;
        private ImageView iv_rec_image;
        private Button button_rec_favorite;
        private ImageView iv_creator_photo;
        private ImageView iv_booth_flag;
        private TextView tv_booth_name;
        MyClickListener myClickListener;

        public AdHolder(@NonNull View itemView, MyClickListener myClickListener) {
            super(itemView);
            tv_rec_creator = itemView.findViewById(R.id.tv_creator_name);
            tv_rec_comment = itemView.findViewById(R.id.tv_rec_item_comment);
            tv_rec_title = itemView.findViewById(R.id.tv_rec_title);
            iv_rec_image = itemView.findViewById(R.id.iv_rec_item_image);
            iv_creator_photo = itemView.findViewById(R.id.iv_creator_photo);
            iv_booth_flag = itemView.findViewById(R.id.iv_rec_flag);
            tv_booth_name = itemView.findViewById(R.id.tv_rec_place);
//            iv_forwarder_photo = itemView.findViewById(R.id.iv_forwarder_photo);
//            cv_fwd_photo = itemView.findViewById(R.id.cv_fwd_image);
//            tv_fwd = itemView.findViewById(R.id.tv_fwded_by);
//            button_rec_Forward = itemView.findViewById(R.id.button_rec_forward);
            button_rec_favorite = itemView.findViewById(R.id.button_rec_favorite);

            this.myClickListener = myClickListener;

            button_rec_favorite.setOnClickListener(this);
//            button_rec_Forward.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button_rec_favorite:
                    myClickListener.onFavoriteClicked(this.getLayoutPosition(), view);
                    break;
//                case R.id.button_rec_forward:
//                    myClickListener.onForwardClicked(this.getLayoutPosition(), view);
//                    break;
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
