package com.example.imac.gab;

/**
 * Created by imac on 4/13/17.
 */

public class users {

    public String username;
    public String email;
    public String image;

    public users() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public users(String username, String email, String image) {
        this.username = username;
        this.email = email;
        this. image = image;
    }
}
