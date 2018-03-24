package com.example.imac.gab;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by imac on 4/19/17.
 */

public class parseJSON {
    static FirebaseAuth auth = FirebaseAuth.getInstance();
    static ArrayList<myGroups> arrayList = new ArrayList<>();

    public static ArrayList<myGroups> parseFeed(String content){

        try{


            JSONObject obj = new JSONObject(content);

            Iterator<String> keys = obj.keys();

            //Ensure that the arrayList is empty or we could get duplicates
            arrayList.clear();

            //Here we use the Iterator and Keys() to handle our unique objects
            //Simply put, we will iterate over each object and get the pieces of data that we need.
            //We then set the values created in the myGroups class.
            while(keys.hasNext()){
                String key = keys.next();
                JSONObject inside = obj.getJSONObject(key);
                myGroups anotherGroup = new myGroups();

                anotherGroup.setGroupCreator(inside.getString("groupCreator"));
                anotherGroup.setGroupId(inside.getString("groupId"));
                anotherGroup.setDescription(inside.getString("description"));
                anotherGroup.setGroupName(inside.getString("groupName"));
                anotherGroup.setGroupCreationDate(inside.getString("groupCreationDate"));

                //Each time the while loop runs, we add another group to the arrayList until there are none left.
                arrayList.add(anotherGroup);
            //Then we simply return the completed arrayList.
            }return arrayList;

        //If there is an error, we need to handle it
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
