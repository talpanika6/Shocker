package com.example.tal.shocker.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Tal on 1/7/2015.
 */
@ParseClassName("PhotoShare")
public class PhotoShare extends ParseObject {


        public PhotoShare() {
            // A default constructor is required.
        }

        public ParseUser getSender() {
            return getParseUser("sender");
        }

        public void setSender(ParseUser sender) {
            put("sender", sender);
        }

        public void setReceiver(ParseUser receiver) {
        put("receiver", receiver);
    }

        public ParseUser getReceiver() {
            return getParseUser("receiver");
        }

        public void setReadBy(boolean flag) {put("read",flag);};

        public boolean getReadBy() {return getBoolean("read");};


        public void setUserPhoto(UserPhoto photo) {put("photo", photo);}

        public ParseObject getUserPhoto() {
            return getParseObject("photo");
        }

          public Date getPhotoShareDate() {
        return getCreatedAt();
    }

        public void setUserPhotoShock(UserPhoto photo) {
        put("photoShock", photo);
    }
        public ParseObject getUserPhotoShock() {
        return getParseObject("photoShock");
    }








}
