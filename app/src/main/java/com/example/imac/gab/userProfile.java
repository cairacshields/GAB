package com.example.imac.gab;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;

public class userProfile extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    Toolbar toolbar;
    Typeface typeface;
    ImageView signOut, settings, userProfile, uploadPicture;
    TextView logo, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        typeface = Typeface.createFromAsset(getAssets(), "fonts/my_font.ttf");
        logo = (TextView) findViewById(R.id.logo_text);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        signOut = (ImageView) findViewById(R.id.signOut);
        uploadPicture = (ImageView) findViewById(R.id.uploadPicture);
        userName = (TextView) findViewById(R.id.userName);
        userProfile = (ImageView) findViewById(R.id.userProfile);
        settings = (ImageView) findViewById(R.id.settings);
        setSupportActionBar(toolbar);

        logo.setTypeface(typeface);
        userName.setTypeface(typeface);


        userName.setText(auth.getCurrentUser().getDisplayName());



            Glide.with(userProfile.this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .transform(new CircularTransform(userProfile.this))
                    .into(uploadPicture);



        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuthUI.getInstance()
                        .signOut(userProfile.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(userProfile.this, MainActivity.class));
                                Toast.makeText(userProfile.this, "Logged Out", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(userProfile.this, settings.class));
            }
        });


    }



}

