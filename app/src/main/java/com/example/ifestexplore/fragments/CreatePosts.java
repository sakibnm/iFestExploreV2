package com.example.ifestexplore.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
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
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private ImageView iv_createAdPhoto;
    private Bitmap bitmap;
    private Button button_createAd;
    private Button button_createAdClear;
    private EditText et_Title;
    private EditText et_Comment;
    private String booth_Name;
    private String booth_Flag;
    private Button button_searchCountry;
    private Boolean takenPhoto = false;
    private Boolean commentGiven = false;
    private Boolean boothSelected = false;
    String currentPhotoPath;


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
        iv_createAdPhoto = view.findViewById(R.id.iv_createAdPhoto);
        cardViewTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        button_searchCountry = view.findViewById(R.id.button_addCountry);

        button_createAd.setOnClickListener(this);
        button_searchCountry.setOnClickListener(this);
        button_createAdClear.setOnClickListener(this);

        return view;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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
            if(comment.equals("")){
                commentGiven =false;
                et_Comment.setError("Can't be empty!");
            }
            else commentGiven =true;
            if(title.equals("")){
                et_Title.setError("Can't be empty!");
                titleGiven =false;
            }
            else titleGiven =true;
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();


            if(takenPhoto && titleGiven && commentGiven && boothSelected){
                displayProgressBar();
                String activeFlag = "active";
                this.createdAd = new Ad(user.getEmail(), user.getDisplayName(),"", user.getPhotoUrl().toString(),"",et_Title.getText().toString(),et_Comment.getText().toString(), booth_Name, booth_Flag, activeFlag);
                uploadImage(bitmap);
            } else {
                Toast.makeText(getContext(), "Please check everything!", Toast.LENGTH_SHORT).show();
            }

        }else if (view.getId() == R.id.button_createAdClearAll){
            clearAll();
        }else if (view.getId() == R.id.button_addCountry){
            searchAndAddCountry();
        }
    }

    private void searchAndAddCountry() {
        CountryPicker.Builder builder = new CountryPicker.Builder().with(getContext()).listener(new OnCountryPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
//                Log.d(TAG, "onSelectCountry: "+country.getName()+" clicked!");
                booth_Name = country.getName();
                String countryWords[]  = booth_Name.split(" ", 2);
                String firstWord = countryWords[0];
                if (firstWord.contains(",")){
                    booth_Name = firstWord.substring(0,firstWord.length()-1);
                }

                booth_Flag = String.valueOf(country.getFlag());
                button_searchCountry.setText(booth_Name);
                button_searchCountry.setCompoundDrawablesRelativeWithIntrinsicBounds(view.getResources().getDrawable(Integer.parseInt(booth_Flag), null),null,null,null);
                boothSelected = true;

            }
        }).sortBy(CountryPicker.SORT_BY_NAME);

        CountryPicker picker = builder.build();

        picker.showBottomSheet((AppCompatActivity) getActivity());

    }



//    Taking photo____________________________________________________________________________________________________________________________________________

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAM_REQ);
            }
        }
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        galleryAddPic();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);

        setPic();
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = iv_createAdPhoto.getMaxWidth();
        int targetH = iv_createAdPhoto.getMaxHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        this.bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        this.bitmap = Bitmap.createScaledBitmap(this.bitmap, 1280, 960,false);
//        Log.d("demo", "setPic: "+ this.bitmap.getWidth()+" "+this.bitmap.getHeight());
        Log.d("demo", "setPic: "+ this.bitmap.getByteCount()/1000);
        iv_createAdPhoto.setImageBitmap(this.bitmap);
        takenPhoto = true;
    }

    //    ____________________________________________________________________________________________________________________________________________


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
        key.replace("/","_");
        final StorageReference userPhotoReference = storage.getReference().child("v2adsImages/"+key+".png");
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
                        getDB.collection("v2adsRepo").document("adscounter").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                final long current_count = (long) documentSnapshot.get("count");
                                createdAd.setAdSerialNo(String.valueOf(current_count));
                                saveDB.collection("adminCheck").document(createdAd.getAdSerialNo()).set(createdAd.toHashMap())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        saveDB.collection("v2adsRepo").document("adscounter").update("count", current_count+1);

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
