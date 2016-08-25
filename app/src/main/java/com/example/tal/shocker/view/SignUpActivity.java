package com.example.tal.shocker.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tal.shocker.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Activity which displays a login screen to the user.
 */
public class SignUpActivity extends AppCompatActivity {
  // UI references.
  private EditText userNameEditText;
  private EditText passwordEditText;
  private EditText passwordAgainEditText;
  private CardView signupView;

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

    setContentView(R.layout.sign_up);

    // Set up the signup form.
    userNameEditText = (EditText) findViewById(R.id.user_name_edit_text);

    passwordEditText = (EditText) findViewById(R.id.password_edit_text);
    passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
    passwordAgainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == R.id.edittext_action_signup ||
            actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
          signup();
          return true;
        }
        return false;
      }
    });

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          int titleFrame=12;

          signupView = (CardView) findViewById(R.id.sighupView);
          signupView.setElevation(titleFrame);
      }


    // Set up the submit button click handler
    Button mActionButton = (Button) findViewById(R.id.sigh_up_btn);
    mActionButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        signup();
      }
    });
  }


  private void signup() {
    String username = userNameEditText.getText().toString().trim();
    String password = passwordEditText.getText().toString().trim();
    String passwordAgain = passwordAgainEditText.getText().toString().trim();

    // Validate the sign up data
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
    if (!password.equals(passwordAgain)) {
      if (validationError) {
        validationErrorMessage.append(getString(R.string.error_join));
      }
      validationError = true;
      validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
    }
    validationErrorMessage.append(getString(R.string.error_end));

    // If there is a validation error, display the error
    if (validationError) {
      Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
          .show();
      return;
    }

    // Set up a progress dialog
    final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
    dialog.setMessage(getString(R.string.progress_signup));
    dialog.show();

    // Set up a new Parse user
    ParseUser user = new ParseUser();
    user.setUsername(username);
    user.setPassword(password);

    // Call the Parse signup method
    user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(ParseException e) {
        dialog.dismiss();
        if (e != null) {
          // Show the error message
          Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
          // Start an intent for the dispatch activity
          Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        }
      }
    });
  }
}
