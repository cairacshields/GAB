package com.example.imac.gab;

import java.util.Date;

/**
 * Created by imac on 7/30/17.
 */

public class message {

    String message;
    String user;
    long date;

    public message(String message, String user) {
        this.message = message;
        this.user = user;

        // Initialize to current time
        date = new Date().getTime();
    }

    public message(){

    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
