package com.example.imac.gab;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class settings extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference= database.getReference().child("users");
    Toolbar toolbar;
    Typeface typeface;
    ImageView signOut, settings;
    ListView settingsLv;
    TextView logo, settingsText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/my_font.ttf");
        logo = (TextView)findViewById(R.id.logo_text);
        settingsLv = (ListView)findViewById(R.id.settingsLv);
        String[] values = new String[]{
                "Delete Account",
                "Smile"
        };
        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        signOut = (ImageView)findViewById(R.id.signOut);
        settings = (ImageView)findViewById(R.id.settings);
        settingsText = (TextView)findViewById(R.id.settingsText);
        setSupportActionBar(toolbar);
        settingsText.setTypeface(typeface);
        logo.setTypeface(typeface);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(settings.this, signedInActivity.class));
            }
        });
        //Listview Adapter

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, values);

        //Set our adapter
        settingsLv.setAdapter(adapter);

        settingsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked, delete account
                            deleteAccount();
                            Toast.makeText(settings.this, "Account Deleted", Toast.LENGTH_LONG).show();
                            AuthUI.getInstance()
                                    .delete(settings.this)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Deletion succeeded
                                                startActivity(new Intent(settings.this, MainActivity.class));

                                            } else {
                                                // Deletion failed
                                                Toast.makeText(settings.this, "Account Deletion Un-successful", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            Toast.makeText(settings.this, "Account NOT deleted", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            };
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Value of item clicked
                int itemPosition = position;

                if(itemPosition == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuthUI.getInstance()
                        .signOut(settings.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(settings.this, MainActivity.class));
                                Toast.makeText(settings.this, "Logged Out", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(settings.this, "No Action Required", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteAccount(){
        Toast.makeText(settings.this, "Account deleted from database", Toast.LENGTH_LONG).show();
        databaseReference.child(auth.getCurrentUser().getUid()).removeValue();
    }
}
