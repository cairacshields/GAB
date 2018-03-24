package com.example.imac.gab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by imac on 4/19/17.
 */

public class HttpManager {

    static BufferedReader reader = null;

    public static String getData(String uri){

        try{

            URL url = new URL(uri);

            //Gain connection to the URL
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            //We will store the data in a String using the StringBuilder
            StringBuilder builder = new StringBuilder();

            //Using the connection we've established, we can create a reader to read the data from the URL
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;

            //The while loop will run as long as the 'line' is not null or empty.
            //** Note that 'line' is equal to the data being read by the InputStreamReader
            while((line = reader.readLine()) != null){

                //Most importantly we add each line of data to the StringBuilder
                builder.append(line);

             //Then return the entire string
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
