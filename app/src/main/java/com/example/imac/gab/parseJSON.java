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

            arrayList.clear();

            //Here we use the Iterator and Keys() to handle our unique objects
            while(keys.hasNext()){
                String key = keys.next();
                JSONObject inside = obj.getJSONObject(key);
                myGroups anotherGroup = new myGroups();

                anotherGroup.setGroupCreator(inside.getString("groupCreator"));
                anotherGroup.setGroupId(inside.getString("groupId"));
                anotherGroup.setDescription(inside.getString("description"));
                anotherGroup.setGroupName(inside.getString("groupName"));
                anotherGroup.setGroupCreationDate(inside.getString("groupCreationDate"));

                arrayList.add(anotherGroup);

            }return arrayList;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
