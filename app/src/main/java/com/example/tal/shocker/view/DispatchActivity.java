package com.example.tal.shocker.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;
import com.parse.ParseUser;

/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends AppCompatActivity {

  static int objectResume=0;
  public DispatchActivity() {

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // inside your activity
    getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
    super.onCreate(savedInstanceState);


      ParseUser currentUser = ParseUser.getCurrentUser();
    // Check if there is current user info
      if ((currentUser != null) ) {
      // Start an intent for the logged in activity
      startActivity(new Intent(this, MainActivity.class));
    } else {
      // Start and intent for the logged out activity
      startActivity(new Intent(this, WelcomeActivity.class));
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if(objectResume>0)
       Toast.makeText(DispatchActivity.this,"Press back for exit",Toast.LENGTH_LONG).show();

    objectResume++;
  }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
