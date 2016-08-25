package com.example.tal.shocker.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Tal on 12/18/2014.
 */
@ParseClassName("UserPhoto")
public class UserPhoto extends ParseObject {

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a user photo shot or liberyPhoto
 */

        public UserPhoto() {
            // A default constructor is required.
        }

        public String getTitle() {
            return getString("title");
        }

        public void setTitle(String title) {
            put("title", title);
        }

        public ParseUser getAuthor() {
            return getParseUser("author");
        }

        public void setAuthor(ParseUser user) {
            put("author", user);
        }

        public void setIsSocked(boolean flag) {put("shock",flag);};

        public boolean getIsSocked() {return getBoolean("shock");};

        public void setUri(String uri){ put("uri",uri);}

        public String getUri(){return getString("uri");}

        public void setLocation(String location) {
            put("location", location);
        }
        public String getLocation(){return getString("location");}

        public ParseFile getPhotoFile() {
            return getParseFile("photo");
        }

        public Date getPhotoDate() {
        return getUpdatedAt();
    }

        public void setPhotoFile(ParseFile file) {
            put("photo", file);
        }



    }


