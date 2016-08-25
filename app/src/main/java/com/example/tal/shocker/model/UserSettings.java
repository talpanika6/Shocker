package com.example.tal.shocker.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


/**
 * Created by Tal on 27-Sep-15.
 */
@ParseClassName("UserSettings")
public class UserSettings extends ParseObject{




        public UserSettings() {
            // A default constructor is required.
        }



        public ParseUser getAuthor() {
            return getParseUser("author");
        }

        public void setAuthor(ParseUser user) {
            put("author", user);
        }

        public void setSound(boolean soundFlag) {put("sound",soundFlag);};

        public boolean getSound() {return getBoolean("sound");};

          public void setPush(boolean pushFlag) {put("push",pushFlag);};

         public boolean getPush() {return getBoolean("push");};







}
