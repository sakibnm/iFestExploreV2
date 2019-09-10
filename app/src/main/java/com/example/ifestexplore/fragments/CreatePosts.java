package com.example.ifestexplore.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ifestexplore.R;
import com.example.ifestexplore.models.Ad;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreatePosts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreatePosts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePosts extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int CAM_REQ = 0x1111;
    private static final int REQ_CODE = 0x005;
    private static final String TAG = "demo";
    private FirebaseAuth mAuth;
    private FirebaseFirestore saveDB;
    private FirebaseFirestore getDB;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View view;
    private CardView cardViewTakePhoto;
    private Bitmap bitmap;
    private Button button_createAd;
    private Button button_createAdClear;
    private EditText et_Title;
    private EditText et_Comment;

    private Boolean takenPhoto = false;
    private Boolean commentGiven = false;

    private Ad createdAd;
    private boolean titleGiven;

    public CreatePosts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreatePosts.
     */
    // TODO: Rename and change types and number of parameters
    public static CreatePosts newInstance(String param1, String param2) {
        CreatePosts fragment = new CreatePosts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_posts, container, false);
        button_createAd = view.findViewById(R.id.button_createAd_share);
        button_createAdClear = view.findViewById(R.id.button_createAdClearAll);
        cardViewTakePhoto = view.findViewById(R.id.card_addPhoto);
        cardViewTakePhoto.setOnClickListener(new TakePhoto());

        button_createAd.setOnClickListener(this);
        button_createAdClear.setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_createAd_share){
            et_Title = this.view.findViewById(R.id.et_Title);
            et_Comment = this.view.findViewById(R.id.et_Comment);
            String title = et_Title.getText().toString().trim();
            String comment = et_Comment.getText().toString().trim();
            if(comment.equals(""))commentGiven =false;
            else commentGiven =true;
            if(title.equals(""))titleGiven =false;
            else titleGiven =true;
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();


            if(takenPhoto && titleGiven && commentGiven){
                displayProgressBar();
                this.createdAd = new Ad(user.getEmail(), user.getDisplayName(),user.getEmail(), "", user.getPhotoUrl().toString(),"", title, comment,"");
                uploadImage(bitmap);
            }

        }else if (view.getId() == R.id.button_createAdClearAll){
            clearAll();
        }
    }

    class TakePhoto implements ImageButton.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAM_REQ);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAM_REQ) {
            bitmap = (Bitmap) data.getExtras().get("data");
            ImageView iv_createAdPhoto = view.findViewById(R.id.iv_createAdPhoto);
            iv_createAdPhoto.setImageBitmap(bitmap);
//            cardViewTakePhoto.setBackground(new BitmapDrawable(getResources(), bitmap));
            ((TextView) view.findViewById(R.id.textView2)).setText("");
            takenPhoto = true;
        }
    }

    private void uploadImage(Bitmap bitmap) {
        Log.d(TAG, "uploading Ad Image");
        Bitmap userPhotoBitmap = bitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        userPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        final byte[] bytes = stream.toByteArray();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        byte[] array = new byte[12]; // length is bounded by 7
        new Random().nextBytes(array);
        String key = new String(array, Charset.forName("UTF-8"));
        final StorageReference userPhotoReference = storage.getReference().child("adsImages/"+key+".png");
        UploadTask uploadTask = userPhotoReference.putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                exception.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String imageURL = taskSnapshot.getMetadata().getPath();
                Log.d(TAG, "onSuccess: ImageUpload" + imageURL);

                StorageReference downloadStorage = taskSnapshot.getMetadata().getReference();

                downloadStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        createdAd.setItemPhotoURL(uri.toString());
                        getDB = FirebaseFirestore.getInstance();
                        saveDB = FirebaseFirestore.getInstance();
                        Log.d(TAG, "onSuccess: Saving Ad First");
                        getDB.collection("adsRepo").document("adscounter").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                final long current_count = (long) documentSnapshot.get("count");
                                createdAd.setAdSerialNo(String.valueOf(current_count));
                                saveDB.collection("adsRepo").document(createdAd.getAdSerialNo()).set(createdAd.toHashMap())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        saveDB.collection("adsRepo").document("adscounter").update("count", current_count+1);

                                        view.findViewById(R.id.progress_createAd).setVisibility(View.GONE);
//                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Log.d(TAG, "onSuccess: Saving Ad Second!");

                                        getBackToReceived();

                                    }
                                });
                            }
                        });

//                        saveDB.collection("adsRepo").document(createdAd.)
                    }
                });}
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: FAILED URI");
                        Toast.makeText(getContext(), "Could not post due to network errors! Please try again!", Toast.LENGTH_SHORT).show();
                        view.findViewById(R.id.progress_createAd).setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        
                    }
                });

    }

    private void displayProgressBar() {
        view.findViewById(R.id.progress_createAd).setVisibility(View.VISIBLE);
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void clearAll(){

        if (mListener!=null)mListener.onClearAllPressedFromCreatePosts();
    }
    public void getBackToReceived(){
        Log.d(TAG, "getBackToReceived: done!");
        if (mListener!=null)mListener.onCreatePressedFromCreatePosts();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onClearAllPressedFromCreatePosts();
        void onCreatePressedFromCreatePosts();
    }
}
