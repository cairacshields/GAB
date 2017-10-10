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
                if(likedOrNot ==false){
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
                groupDatabaseReference.child(groupName).child("message").push().setValue(new message(messageInfo,
                        auth.getCurrentUser().getDisplayName() ));

                messageContent.setText("");
            }
        });
        //We will call our custom method at the start of the activity to show the messages
        displayChatMessages();

        getGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlertDialog();
            }
        });
    }

    public void showAlertDialog(){
        //Use the giphy api URL to send request
        requestData("http://api.giphy.com/v1/gifs/trending?api_key=ff434554a797472ab393ca1c5b94d9b1");
        AlertDialog.Builder builder = new AlertDialog.Builder(chatInterface.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        builder.setTitle("Choose a GIF");
        builder.setView(view);
        builder.show();

    }

    public void requestData(String str){

        FetchDataTwo task = new FetchDataTwo();
        task.execute(str);
    }

    //Get Gif Data request started with an Async Task
    public class FetchDataTwo extends AsyncTask<String, String, String>{


        @Override
        protected String doInBackground(String... params) {

            return HttpManager2.getData(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {

            theGifArray = parseGifJson.parseGifs(s);

            gif_gv.setAdapter(new gifGridAdapter(view.getContext(), theGifArray));
            Toast.makeText(chatInterface.this, "Adapter set...", Toast.LENGTH_LONG).show();

            super.onPostExecute(s);

        }
    }

    //This is the method that instantiates the FirebaseListAdapter.
    public void displayChatMessages(){

        //Here we need to instantiate our listadapter by passing in the needed paramteters.
        //Context, message model, specified layout and database SPECIFIC reference
            // ** Please remember to be specific when setting the database reference.
            //** I struggled with populating the messages because I did not specify the correct level of nodes
        adapter = new FirebaseListAdapter<message>(chatInterface.this, message.class,R.layout.message,
                groupDatabaseReference.child(groupName).child("message")) {
            //This requires declaration is used for the date formatting
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void populateView(View v, message model, int position) {
                //Below, we simply gain reference to our views in the custom layout file
                TextView userName = (TextView)v.findViewById(R.id.message_user);
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageTime = (TextView)v.findViewById(R.id.message_date);

                //Then after gaining reference, we can set the text of each view
                messageText.setText(model.getMessage());
                //Since our date is stored as a 'Long' in Firebase, we have to format it before setting it
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
            final String url = myGif.getUrl();

            vv.setVideoURI(Uri.parse(url));
            //If the video is complete, we will simply start it again
            vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    vv.start();
                }
            });
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
