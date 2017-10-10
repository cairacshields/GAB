package com.example.imac.gab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by imac on 8/1/17.
 */

public class HttpManager2 {

        static BufferedReader reader = null;

        public static String getData(String uri){

            try{

                URL url = new URL(uri);

                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                StringBuilder builder = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;

                while((line = reader.readLine()) != null){

                    builder.append(line);
                }return builder.toString();

            }catch(Exception e){
                e.printStackTrace();
                return null;

            }finally {
                if(reader != null){
                    try{
                        reader.close();
                    }catch(IOException e){
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
    }


