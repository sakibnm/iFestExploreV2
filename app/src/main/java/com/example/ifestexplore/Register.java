package com.example.ifestexplore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ifestexplore.models.Ad;
import com.example.ifestexplore.models.User;
import com.example.ifestexplore.models.UsersData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.altbeacon.beacon.Identifier;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Register extends AppCompatActivity {

    private static final String TAG = "demo";
    private static final int REQ_CODE = 0x005;
    private FirebaseAuth mAuth;

    private EditText etName, etEmail, etPassword, etRepPassword;
    private TextView tvInstr;
    private Button createAccount;
    private TextView tvLoginClicked;
    private ImageView ivUserPhoto;
    private static int CAM_REQ = 0x1111;
    private Bitmap bitmap;
    private ProgressBar progressBar;
    private URI userUri;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null)Log.d(TAG, "onStart: "+ currentUser.getEmail()+" name: "+currentUser.getDisplayName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#042529")));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        if(!isNetworkAvailable()) Log.d(TAG, "onCreate: Internet not Available!!!");

        ivUserPhoto = findViewById(R.id.iv_userphoto);
        tvInstr = findViewById(R.id.tvInstr);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etRepPassword = findViewById(R.id.et_rep_password);
        createAccount = findViewById(R.id.button_register2);
        tvLoginClicked = findViewById(R.id.tv_sign_in_click);


        mAuth = FirebaseAuth.getInstance();


//        if(userPassword.equals(userRepPassword)){
//            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
//                    .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(task.isSuccessful()){
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
//                                        .setDisplayName(userName)
//                                        .build();
//                                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                            Log.d(TAG, "User profile updated.");
//                                        }
//                                    }
//                                });
//                            }else{
//
//                            }
//                        }
//                    });
//        }else{
//            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
//        }

        ivUserPhoto.setOnClickListener(new TakePhoto());

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = String.valueOf(etEmail.getText()).trim();
                String userPassword = String.valueOf(etPassword.getText()).trim();
                final String userName = String.valueOf(etName.getText()).trim();
                String userRepPassword = String.valueOf(etRepPassword.getText()).trim();
                boolean inputValid = true;
                if(! Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    etEmail.setError("Put a valid Email address!");
                    inputValid = false;
                }
                if(userName.equals("")|| userEmail.equals("") || bitmap==null||userPassword== ""||userRepPassword==""){
                    inputValid = false;
                    Toast.makeText(getApplicationContext(),"Please take a photo, and fill up all the fields", Toast.LENGTH_SHORT).show();
                }else if (!userPassword.equals(userRepPassword)){
                    inputValid = false;
                    etPassword.setError("Passwords do not match!");
                    etRepPassword.setError("Passwords do not match!");
                }

                else if(inputValid){
                    displayProgressBar();
                    User user = new User(userName, userEmail, "", userPassword);

                    signUp(user);

                }
            }
        });

        tvLoginClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });



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
            ivUserPhoto.setImageBitmap(bitmap);
            tvInstr.setText("");
        }else if(requestCode == REQ_CODE){
            finish();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private void uploadImage(Bitmap bitmap, final User user){
        Bitmap userPhotoBitmap = bitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        userPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        final byte[] bytes = stream.toByteArray();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference userPhotoReference = storage.getReference().child("userPhotos/"+user.getEmail()+"_photo.jpg");
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
                Log.d(TAG, "onSuccess: ImageUpload"+imageURL);
                user.setPhotoURL(imageURL);

                StorageReference downloadStorage = taskSnapshot.getMetadata().getReference();

                downloadStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
////                        .setDisplayName(userR.getName())
                                .setPhotoUri(uri)
                                .build();
                        mAuth.getCurrentUser().updateProfile(profileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                findViewById(R.id.progressCard).setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Intent intent = new Intent(Register.this, Home.class);

                                startActivityForResult(intent, REQ_CODE);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: FAILED URI");
                    }
                });

//                if(downloadUri.isSuccessful()){
//                    Log.d(TAG, "onSuccess: URL: "+downloadUri.getResult());
//
//                }else{
//                    Log.d(TAG, "onFailure: NOT WORKING URI" );
//                }




//                signUp(user);

            }
        });


    }
    private void displayProgressBar() {
        findViewById(R.id.progressCard).setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void signUp(final User userR){
        Log.d(TAG, "signUpStarting: "+userR.toString());
//        final Uri userProfURI = Uri.parse(userR.getPhotoURL());
        mAuth.createUserWithEmailAndPassword(userR.getEmail(), userR.getPassword())
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userR.getName())
//                                    .setPhotoUri(userProfURI)
                                    .build();

                            createUsersData(userR);
                            user.updateProfile(profileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "User profile updated.");
                                    uploadImage(bitmap, userR);
                                }
                            });

                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT);
                        }
                    }
                });


    }

    private void createUsersData(final User userC){
        final String email = userC.getEmail();
        String name = userC.getName();
        List<Ad> ads = new ArrayList<Ad>();

        String instanceID = Identifier.parse(UUID.randomUUID().toString()).toString();
        final UsersData usersData = new UsersData(email, name, instanceID);

        final FirebaseFirestore saveDB = FirebaseFirestore.getInstance();

        saveDB.collection("users").document(email).set(usersData.toHashMap())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess Saving User to Firestore for: "+ email);
                    Map<String, Object> idToEmail = new HashMap<>();
                    idToEmail.put("email", usersData.getEmail());
                    String partInstanceID1 = usersData.getInstanceID().substring(0, 8);
                    String partInstanceID2 = usersData.getInstanceID().substring(9, 13);
                    String partInstanceID = "0x"+partInstanceID1+partInstanceID2;
                    Log.d(TAG, "PART INSTANCE ID: "+ partInstanceID1+" "+partInstanceID2+" "+partInstanceID);
                    saveDB.collection("mapIDtoemail").document(partInstanceID).set(idToEmail);
                }
            })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure Saving User to Firestore for: "+ email);
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
