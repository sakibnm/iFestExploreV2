package com.example.ifestexplore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {

    private static final String TAG = "demo";
    private FirebaseAuth mAuth;

    private EditText etName, etEmail, etPassword, etRepPassword;
    private Button createAccount;

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
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#042529")));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etRepPassword = findViewById(R.id.et_rep_password);
        createAccount = findViewById(R.id.button_register2);

        String userEmail = String.valueOf(etEmail.getText());
        String userPassword = String.valueOf(etPassword.getText());
        final String userName = String.valueOf(etName.getText());
        String userRepPassword = String.valueOf(etRepPassword.getText());

//        mAuth = FirebaseAuth.getInstance();

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


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
