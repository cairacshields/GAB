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

    //Start by getting reference to our FireBase account which handles the database and authentication
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    //Note that we have two important root database nodes 'users' and 'groups'
    private DatabaseReference databaseReference= database.getReference().child("users");
    private DatabaseReference groupDatabaseReference = database.getReference().child("groups");

    //Just get access to our views
    Toolbar toolbar;
    EditText getTopic, description;
    Button startConversation;
    Typeface typeface;
    ImageView signOut, settings, userProfile, list_image;
    TextView logo, list_text, descriptionTextView;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    ListView lv;
    //Make sure that the arrayList type is correct
    ArrayList<myGroups> myusersArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);

        //Get reference to our typeface in assets
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

        //Set custom action bar
        setSupportActionBar(toolbar);
        //Set typeface
        logo.setTypeface(typeface);

        //Handle clicks on user profile (take user to view their profile)
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signedInActivity.this, userProfile.class));
            }
        });

        //Sign out the user
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Using AuthUI we can gather the current user session info and call the .signOut() function
                //Once the signOut is successful, it will call the onCompleteListener which brings the user back to the
                //Login activity.
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

        //Brings user to settings activity to manage account
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(signedInActivity.this, settings.class));
            }
        });

            //Call custom method to either create new user or locate current user
            writeNewUser(auth.getCurrentUser().getUid(), auth.getCurrentUser().getDisplayName(),
                    auth.getCurrentUser().getEmail(), auth.getCurrentUser().getPhotoUrl().toString());


        //The Start conversation function
        startConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Grab the time
                String date = dateFormat.format(calendar.getTime());
                //Grab the topic text
                String newTopic = getTopic.getText().toString();
                //Grab the topic description
                String descriptionContent = description.getText().toString();

                //Call the 'postNewGroup' function which takes all the above values as well as the current
                //Users information ** Be sure that these are in the correct order
                postNewGroup(descriptionContent, newTopic,
                        auth.getCurrentUser().getDisplayName(),date, auth.getCurrentUser().getUid()+newTopic);

                //Set the text back to blank
                description.setText("");

                //Take user to the chatInterface and be sure to add the newTopic as an extra in the intent to
                //customize the interface.
                Intent intent = new Intent(signedInActivity.this, chatInterface.class);
                intent.putExtra("groupName", newTopic);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Once the activity starts, call the 'requestData' function and pass in the FireBase database URL
        requestData("https://gab-app-e1435.firebaseio.com/groups.json");
    }

    public void requestData(String s){
        //The 'requestData' function makes a new instance of the FetchData class and is used to start the
        //Background task of gathering the data from the URL we pass in as a string. This process starts when we call
        //the execute() method.
        FetchData task = new FetchData();
        task.execute(s);
    }

    //Method to handle adding groups information into our database when the button is clicked.
    //We will use the groupName as the key in the database and we will also take note of the original creator
    //and the date of which the group was created. *** We also need to find a way to store the actual messages.
    public void postNewGroup(String description,String groupName, String groupCreator, String groupCreationDate, String groupId){
        String newTopic = getTopic.getText().toString();

        //We use the 'group' class to create a new group instance and then reference the database to add a new entry
        group aGroup = new group(description, groupName, groupCreator, groupCreationDate, groupId);
        groupDatabaseReference.child(newTopic).setValue(aGroup);

        getTopic.setText("");

    }

    //Method to save a user to the realtime database when they sign in
    public void writeNewUser(String userId, String username, String email, String image){

        //First we check to see if the user already exists
        if(databaseReference.child(auth.getCurrentUser().getUid()).equals(userId)){
            Toast.makeText(signedInActivity.this, "User already exists", Toast.LENGTH_LONG).show();
        }else {
         //If they don't exist in the database, we create a new user using the information they input and save to the database
            users user = new users(username, email, image);
            databaseReference.child(userId).setValue(user);
        }
    }


    public class FetchData extends AsyncTask<String, String, String>{

        //Using AsyncTask we can handle background processes
        //In our case, we are fetching the JSON data from our FireBase database.


        //Step one is to use the doInBackground function to call our custom getData function from the HttpManager class
        @Override
        protected String doInBackground(String... params) {
            //This function just takes the URL String that we provide, and establishes a connection.
            //Once the connection is established, we use a StringBuilder and an InputStreamReader to get the JSON data.
            //Basically as it reads through the data, we add it to create one long string!
            return HttpManager.getData(params[0]);
        }

        //Next we call the onPostExecute function, this function is called after the doInBackground completes
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
                //Inflate our custom layout
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.user_list_item_layout, parent, false);
               // *** If you're wondering why the imageView line is commented out. It is because I am testing it and no longer need it.
                // I will remove the comments when testing is done.

                // list_image = (ImageView)convertView.findViewById(R.id.proPic);

                //From the convertview, we can reference our views
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
