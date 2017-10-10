package com.example.imac.gab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.imac.gab.R.drawable.like;

public class signedInActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference= database.getReference().child("users");
    private DatabaseReference groupDatabaseReference = database.getReference().child("groups");
    Toolbar toolbar;
    EditText getTopic, description;
    Button startConversation;
    Typeface typeface;
    ImageView signOut, settings, userProfile, list_image;
    TextView logo, list_text, descriptionTextView;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    ListView lv;
    ArrayList<myGroups> myusersArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/my_font.ttf");
        logo = (TextView)findViewById(R.id.logo_text);
        getTopic = (EditText)findViewById(R.id.getTopic);
        description = (EditText)findViewById(R.id.description);
        startConversation = (Button)findViewById(R.id.startConversation);
        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        signOut = (ImageView)findViewById(R.id.signOut);
        lv = (ListView)findViewById(R.id.myusers);
        userProfile = (ImageView)findViewById(R.id.userProfile);
        settings = (ImageView)findViewById(R.id.settings);
        setSupportActionBar(toolbar);

        logo.setTypeface(typeface);

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signedInActivity.this, userProfile.class));
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AuthUI.getInstance()
                        .signOut(signedInActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(signedInActivity.this, MainActivity.class));
                                Toast.makeText(signedInActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(signedInActivity.this, settings.class));
            }
        });


            writeNewUser(auth.getCurrentUser().getUid(), auth.getCurrentUser().getDisplayName(),
                    auth.getCurrentUser().getEmail(), auth.getCurrentUser().getPhotoUrl().toString());

        startConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = dateFormat.format(calendar.getTime());
                String newTopic = getTopic.getText().toString();
                String descriptionContent = description.getText().toString();

                postNewGroup(descriptionContent, newTopic,
                        auth.getCurrentUser().getDisplayName(),date, auth.getCurrentUser().getUid()+newTopic);

                description.setText("");
                Intent intent = new Intent(signedInActivity.this, chatInterface.class);
                intent.putExtra("groupName", newTopic);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        requestData("https://gab-app-e1435.firebaseio.com/groups.json");
    }

    public void requestData(String s){

        FetchData task = new FetchData();
        task.execute(s);
    }

    //Method to handle adding groups information into our database when the button is clicked.
    //We will use the groupName as the key in the database and we will allso take note of the original creator
    //and the date of which the group was created. *** We also need to find a way to store the actual messages.
    public void postNewGroup(String description,String groupName, String groupCreator, String groupCreationDate, String groupId){
        String newTopic = getTopic.getText().toString();

        group aGroup = new group(description, groupName, groupCreator, groupCreationDate, groupId);
        groupDatabaseReference.child(newTopic).setValue(aGroup);

        getTopic.setText("");

    }

    //Method to save a user to the realtime database when they sign in
    public void writeNewUser(String userId, String username, String email, String image){

        if(databaseReference.child(auth.getCurrentUser().getUid()).equals(userId)){
            Toast.makeText(signedInActivity.this, "User already exists", Toast.LENGTH_LONG).show();
        }else {
            users user = new users(username, email, image);

            databaseReference.child(userId).setValue(user);
        }
    }

    public class FetchData extends AsyncTask<String, String, String>{


        @Override
        protected String doInBackground(String... params) {

            return HttpManager.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {

            myusersArrayList = parseJSON.parseFeed(s);

            lv.setAdapter(new ListAdapter(signedInActivity.this, myusersArrayList));

            super.onPostExecute(s);
        }
    }

    public class ListAdapter extends BaseAdapter{

        private Context context;
        ArrayList<myGroups> arrayList;
        public ListAdapter(Context context, ArrayList<myGroups> arrayList){
            this.context = context;
            this.arrayList = arrayList;
        }
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.user_list_item_layout, parent, false);
               // *** If you're wondering why the imageView line is commented out. It is because I am testing it and no longer need it.
                // I will remove the comments when testing is done.

                // list_image = (ImageView)convertView.findViewById(R.id.proPic);
                list_text = (TextView)convertView.findViewById(R.id.userName);
                descriptionTextView = (TextView)convertView.findViewById(R.id.descriptionText);
            }else{
                //list_image = (ImageView)convertView;
            }

            myGroups mygroup = (myGroups) this.getItem(position);

            final String name = mygroup.getGroupName();
            final String descriptionText = mygroup.getDescription();

           /* Glide.with(signedInActivity.this)
                    .load(image)
                    .transform(new CircularTransform(signedInActivity.this))
                    .into(list_image);*/

            //list_text.setTypeface(typeface);
            list_text.setText(name);
            descriptionTextView.setText(descriptionText);

            //The below code will handle list item clicks.. When a certain item is clicked, we will call
            // The custom method 'goToChatInterface' which takes one parameter which is the name of our group
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChatInterface(name);
                }
            });
            return convertView;
        }

        //This method will create an intent to the chosen chat room, we simply add the name of the chat room
        //as an extra in our intent. The ChatInterface class will take the name we pass to select the appropriate room
        private void goToChatInterface(String name){

            Intent intent = new Intent(signedInActivity.this, chatInterface.class);
            intent.putExtra("groupName", name);
            context.startActivity(intent);
        }
    }

}
