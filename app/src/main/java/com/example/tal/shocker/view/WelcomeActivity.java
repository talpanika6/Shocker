package com.example.tal.shocker.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tal.shocker.Shocker;
import com.example.tal.shocker.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class WelcomeActivity extends AppCompatActivity {

    // UI references.
    private EditText userNameEditText;
    private EditText passwordEditText;
    private LoginButton loginFaceButton;
    Shocker app;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      // inside your activity
      getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          // set an exit transition
          getWindow().setEnterTransition(new Slide(Gravity.START));
          // set an exit transition
          getWindow().setExitTransition(new Slide(Gravity.END));
      }

    super.onCreate(savedInstanceState);

    setContentView(R.layout.home_logged_out);


      // Set up the login form.
      userNameEditText = (EditText) findViewById(R.id.userText);
      passwordEditText = (EditText) findViewById(R.id.passText);
      passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
              if (actionId == R.id.edittext_action_login ||
                      actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                  loginWithParse();
                  return true;
              }
              return false;
          }
      });



      //setUp facebook login Button
       loginFaceButton = (LoginButton) findViewById(R.id.loginFaceButton);
      loginFaceButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              loginWithFacebook();
          }
      });

      // Set up the submit button click handler Parse
      Button actionButton = (Button) findViewById(R.id.Loginbtn);
      actionButton.setOnClickListener(new View.OnClickListener() {
          public void onClick(View view) {

              loginWithParse();
          }
      });

      final WelcomeActivity act=this;
    // Sign up button click handler
    Button signupButton = (Button) findViewById(R.id.Signup_btn);
    signupButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // Starts an intent for the sign up activity
        startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class),ActivityOptions.makeSceneTransitionAnimation(act).toBundle());
      }
    });
  }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void loginWithFacebook() {

        final ProgressDialog dialog = new ProgressDialog(WelcomeActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();
        ///get permissions
        List<String> permissions = Arrays.asList("public_profile","user_friends","user_location", "user_birthday","email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                dialog.dismiss();
                if (user != null) {
                    if (user.isNew()) {
                        //new User web view
                        makeMeRequest();

                    } else {
                        startAppActivity();
                    }
                } else
                    Toast.makeText(getApplicationContext(),
                            R.string.logn_generic_error,
                            Toast.LENGTH_LONG).show();

            }
        });
    }

    

    private void makeMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                        if (jsonObject != null) {

                            JSONObject userProfile = new JSONObject();

                            try {

                                userProfile.put("facebookId", jsonObject.getString("id"));
                                userProfile.put("name", jsonObject.getString("name"));

                                if (jsonObject.getString("email") != null)
                                    userProfile.put("email", jsonObject.getString("email"));

                                if (jsonObject.getString("gender") != null)
                                    userProfile.put("gender", jsonObject.getString("gender"));

                                if (jsonObject.getString("location") != null)
                                    userProfile.put("location", jsonObject.getString("location"));

                                if (jsonObject.getString("birthday") != null)
                                    userProfile.put("birthday", jsonObject.getString("birthday"));

                             ParseUser user=ParseUser.getCurrentUser();
                                // Save the user profile info in a user property
                                user.put("profile", userProfile);
                                user.saveInBackground();

                                //start App
                                startAppActivity();
                            } catch (JSONException e) {
                                Toast.makeText(WelcomeActivity.this, "Error parsing returned user data. " + e, Toast.LENGTH_LONG).show();
                            }
                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Toast.makeText(WelcomeActivity.this, "Authentication error: " + graphResponse.getError(), Toast.LENGTH_LONG).show();
                                    break;
                                case TRANSIENT:
                                    Toast.makeText(WelcomeActivity.this, "Transient error. Try again. " + graphResponse.getError(), Toast.LENGTH_LONG).show();
                                    break;
                                case OTHER:
                                    Toast.makeText(WelcomeActivity.this, "Some other error: " + graphResponse.getError(), Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }
                });
          request.executeAsync();
    }

    
    private void loginWithParse() {
        String username = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(WelcomeActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(WelcomeActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();
        // Call the Parse login method
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {

                    startAppActivity();
                }
            }
        });
    }

 // Start an intent for the dispatch activity
    private void startAppActivity() {  
        Intent intent = new Intent(WelcomeActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
















