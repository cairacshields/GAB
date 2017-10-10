package com.example.imac.gab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by imac on 8/1/17.
 */

public class parseGifJson {

    static ArrayList<gifs> gifsArrayList = new ArrayList<>();

    public static ArrayList<gifs> parseGifs(String content){

        try {
            JSONObject obj = new JSONObject(content);

            JSONArray arr = obj.getJSONArray("data");


            gifsArrayList.clear();


            for(int i = 0; i< arr.length(); i++){

                JSONObject object = arr.getJSONObject(i);

                JSONObject innerObject = object.getJSONObject("images");
                JSONObject anotherObject = innerObject.getJSONObject("original_mp4");
                JSONObject secondInnerObject = innerObject.getJSONObject("preview");
                gifs myGifs = new gifs();

                myGifs.setUrl(secondInnerObject.getString("mp4"));
                myGifs.setEmbed_url(anotherObject.getString("mp4"));

                gifsArrayList.add(myGifs);
            }return gifsArrayList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
