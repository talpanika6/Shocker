package com.example.tal.shocker;


import android.app.Application;
import com.example.tal.shocker.model.PhotoShare;
import com.example.tal.shocker.model.UserPhoto;
import com.example.tal.shocker.model.UserSettings;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;




public class Shocker extends Application {



  @Override
  public void onCreate() {
    super.onCreate();

    //crash
      ParseCrashReporting.enable(this);
      // Initialize Parse
      Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
      //Push

      ParseInstallation installation = ParseInstallation.getCurrentInstallation();
      installation.saveEventually();
      ParsePush.subscribeInBackground("Shocker");

      // Initialize Facebook
      String appId = getString(R.string.facebook_app_id);
      ParseFacebookUtils.initialize(this);

       /*
      *
      * subclass ParseObject for
      * create and modify UserPhoto objectsY
      */
      ParseObject.registerSubclass(UserPhoto.class);
      /*
      *
      * subclass ParseObject for
      * create and modify PhotoShare--send and recieve shock notification   objects
        */
      ParseObject.registerSubclass(PhotoShare.class);

     /*
      *
      * subclass ParseObject for
      * create and modify  UserSettings
        */
     ParseObject.registerSubclass(UserSettings.class);



  }

}
