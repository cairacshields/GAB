package com.example.imac.gab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.*;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference= database.getReference();
    Button loginButton;
    ImageView uploadPicture;
    private static final int RC_SIGN_IN = 1027;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button)findViewById(R.id.loginButton);
        uploadPicture = (ImageView)findViewById(R.id.uploadPicture);
        //Below condition will check if the current user is already logged in or not.
        if(auth.getCurrentUser() != null) {
            //already Signed in, start signedInActivity
            startActivity(new Intent(this, signedInActivity.class));
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //not signed in
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(), new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setTheme(R.style.MyAppTheme)
                                    .setLogo(R.drawable.ic_launcher)
                                    .build(),
                            RC_SIGN_IN
                    );


                }
            });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check value of result code
        if(requestCode == RC_SIGN_IN){
            //Response from the sign in call
            IdpResponse response = IdpResponse.fromResultIntent(data);


            //Succesfully Signed in
            if(resultCode == ResultCodes.OK){
                Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
                IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
                startActivity(new Intent(this, signedInActivity.class)
                .putExtra("the_token", idpResponse.getIdpToken()));
                finish();
                return;
            }else {

                //Sign in Failed
                if(response == null){
                    //User pressed back
                    Toast.makeText(this, "Request Cancelled", Toast.LENGTH_LONG).show();
                    return;
                }
                if(response.getErrorCode() == ErrorCodes.NO_NETWORK){
                    Toast.makeText(this, "No internet connection, please try again later", Toast.LENGTH_LONG).show();
                    return;
                }
                if(response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                    Toast.makeText(this, "Something went wrong, please try again later!", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        }
    }


}
