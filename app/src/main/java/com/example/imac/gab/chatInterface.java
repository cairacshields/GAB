package com.example.imac.gab;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.icu.text.DateFormat;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;
import static com.example.imac.gab.R.layout.gif_gridview;

public class chatInterface extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference= database.getReference().child("users");
    private DatabaseReference groupDatabaseReference = database.getReference().child("groups");
    private FirebaseListAdapter<message> adapter;
    private static final int PREFERENCES_MODE_PRIVATE = 0;

    //May use SharedPreferences to store the 'likedOrNot' value for each user.
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;


    ListView messageLV;
    Toolbar toolbar;
    Typeface typeface;
    EditText messageContent;
    ImageButton sendMessage, getGif;
    ImageView like;
    String groupName;
    View view;
    TextView logo;
    GridView gif_gv;
    Boolean likedOrNot;
    ArrayList<gifs> theGifArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_interface);
        likedOrNot = false;
        view = getLayoutInflater().inflate(gif_gridview,null);

        messageLV = (ListView)findViewById(R.id.messageLV);
        gif_gv = (GridView) view.findViewById(R.id.gif_gv);
        toolbar = (Toolbar)findViewById(R.id.chat_toolbar);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/my_font.ttf");
        logo = (TextView)findViewById(R.id.logo_text);
        like = (ImageView)findViewById(R.id.like);
        messageContent = (EditText)findViewById(R.id.contentMessage);
        sendMessage = (ImageButton)findViewById(R.id.sendMessage);
        getGif = (ImageButton)findViewById(R.id.getGif);


        //We hav to use the below line of code to get the groupName from our intents
        groupName = getIntent().getStringExtra("groupName");

        logo.setTypeface(typeface);

        //From here I need to test and see if the current user has the current group in there liked,
        //If they have the group in the "liked" node, we need to preset the image to liked.
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the group is NOT in the 'liked' node already, add it and change the liked image
                if(!likedOrNot){
                    like.setImageResource(R.drawable.liked);
                    databaseReference.child(auth.getCurrentUser().getUid()).child("liked").push().setValue(groupName);
                }

            }
        });

        //When the user selects the send message icon, the text will be taken from the EditText and we will push a new node
        //to the database under the specific group.
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageInfo = messageContent.getText().toString();

                //Get reference to the specific group and under the 'message' node, we add the new message.
                //Since FireBase is realtime, the user should see the message instantly
                groupDatabaseReference.child(groupName).child("message").push().setValue(new message(messageInfo,
                        auth.getCurrentUser().getDisplayName() ));

                //Then we just clear the message field
                messageContent.setText("");
            }
        });
        //We will call our custom method at the start of the activity to show the messages
        displayChatMessages();

        //Clicking this button will start the process of pulling popular gifs from the giphy API
        getGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlertDialog();
            }
        });
    }

    //This function is used to request data from giphy **Note that the URL requires a unique api key
    public void showAlertDialog(){
        //Use the giphy api URL to send request
        requestData("http://api.giphy.com/v1/gifs/trending?api_key=ff434554a797472ab393ca1c5b94d9b1");
        //We use an alert dialog to display the popular gif's
        AlertDialog.Builder builder = new AlertDialog.Builder(chatInterface.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        builder.setTitle("Choose a GIF");
        builder.setView(view);
        builder.show();

    }

    public void requestData(String str){
        //Same process as the 'signedInActivity' class
        FetchDataTwo task = new FetchDataTwo();
        task.execute(str);
    }

    //Get Gif Data request started with an Async Task
    public class FetchDataTwo extends AsyncTask<String, String, String>{


        @Override
        protected String doInBackground(String... params) {

            //Simply pass in the string URL for Giphy to get the data and return the String value before calling
            //onPostExecute.
            return HttpManager2.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {

            theGifArray = parseGifJson.parseGifs(s);
            //Set the gif grid view adapter which goes through the array of gif URL's and sets them up in
            //a grid of video views.
            gif_gv.setAdapter(new gifGridAdapter(view.getContext(), theGifArray));
            Toast.makeText(chatInterface.this, "Adapter set...", Toast.LENGTH_LONG).show();
            super.onPostExecute(s);

        }
    }

    //This is the method that instantiates the FirebaseListAdapter.
    //FireBase has a custom message view adapter that makes it easy to display messages
    public void displayChatMessages(){

        //Here we need to instantiate our listadapter by passing in the needed parameters.
        //Context, message model, specified layout and database SPECIFIC reference
            // ** Please remember to be specific when setting the database reference.
            //** I struggled with populating the messages because I did not specify the correct level of nodes
        adapter = new FirebaseListAdapter<message>(chatInterface.this, message.class,R.layout.message,
                groupDatabaseReference.child(groupName).child("message")) {
            //This required declaration is used for the date formatting
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void populateView(View v, message model, int position) {
                //Below, we simply gain reference to our views in the custom layout file
                TextView userName = (TextView)v.findViewById(R.id.message_user);
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageTime = (TextView)v.findViewById(R.id.message_date);

                //Then after gaining reference, we can set the text of each view. Everything about each message is stored in the model.
                messageText.setText(model.getMessage());
                //Since our date is stored as a type 'Long' in FireBase, we have to format it before setting it
                messageTime.setText(DateFormat.getDateTimeInstance().format(model.getDate()));
                userName.setText(model.getUser());



            }
        };
        //Be sure to actually set the adapter to the listview
        messageLV.setAdapter(adapter);
    }

    //Adapter for gridview
    public class gifGridAdapter extends BaseAdapter{

        private Context c;
        ArrayList<gifs> arrayGifs;

        public gifGridAdapter(Context c, ArrayList<gifs> arrayGifs){
            this.c = c;
            this.arrayGifs = arrayGifs;
        }

        @Override
        public int getCount() {
            return arrayGifs.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayGifs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.gridview_layout_item, null);


            }

            final VideoView vv = (VideoView) convertView.findViewById(R.id.gifView);

            gifs myGif = (gifs)this.getItem(position);

            final String embed_url = myGif.getEmbed_url();

            //Get the URL
            final String url = myGif.getUrl();

            //Set the video by parsing the URL
            vv.setVideoURI(Uri.parse(url));

            //If the video is complete, we will simply start it again so it loops
            vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    vv.start();
                }
            });
            //Be sure to start the gif
            vv.start();



            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Here we manage what happens when a gif is selected
                    //We ultimately want to send the url to Firebase as a message node
                    //Then display it along with the other messages.
                    // ** The tricky part is that it is not just text, it is a gif video...

                    groupDatabaseReference.child(groupName).child("message").push().setValue(new message(url,
                            auth.getCurrentUser().getDisplayName() ));

                }
            });
            return convertView;
        }
    }

}
