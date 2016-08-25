package com.example.tal.shocker.view;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.tal.shocker.R;
import com.example.tal.shocker.model.UserSettings;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class SettingsActivity extends AppCompatActivity  implements
        CompoundButton.OnCheckedChangeListener{

    ThreadLogOut mThreadLogOut;
    int Value=0;
    AppCompatActivity mAct;
     ProgressDialog dialog;
    private Handler handler;
    SwitchCompat switchSound,switchPush;
    public static boolean soundOn,pushOn;
    UserSettings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inside your activity
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // set an exit transition
            getWindow().setEnterTransition(new Explode());
            // set an exit transition
            getWindow().setExitTransition(new Slide(GravityCompat.START).setDuration(80));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar settingsLayout;
        TextView logOut;
        TextView legal_privacy;

        //switch
        switchSound = (SwitchCompat) findViewById(R.id.SwitchSound);
        switchSound.setOnCheckedChangeListener(this);
        switchPush  = (SwitchCompat) findViewById(R.id.SwitchPush);
        switchPush.setOnCheckedChangeListener(this);
        switchSound.setChecked(soundOn);
        switchPush.setChecked(pushOn);



        //Toolbar
        settingsLayout=(Toolbar)findViewById(R.id.settingsLayout);
        setSupportActionBar(settingsLayout);


        legal_privacy=(TextView)findViewById(R.id.legal_privacy);
        legal_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLegalPrivacy();
            }
        });


        ///set backlistener
        settingsLayout.setTitle(getString(R.string.title_activity_settings));
        settingsLayout.setNavigationIcon(getDrawable(R.drawable.ic_arrow_back_white_18dp));
        settingsLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAct=this;
        logOut=(TextView)findViewById(R.id.logout_text);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogOutThread();
            }
        });


        setupSettings();

    }

    private void setupSettings()
    {
        ParseQuery<UserSettings> query = new ParseQuery<>("UserSettings");
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<UserSettings>() {
            @Override
            public void done(List<UserSettings> list, ParseException e) {

                if(e==null) {
                    if (!list.isEmpty()) {


                        settings = list.get(0);


                    } else {
                        showSnackbar("list is empty!!!");
                    }


                }
                else
                    showSnackbar("Error fatching user setting!!!");
            }
        });

    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        switchSound.setChecked(state.getBoolean("sound"));
        switchPush.setChecked(state.getBoolean("push"));
    }



    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean("push",pushOn);
        state.putBoolean("sound",soundOn);
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.SwitchPush:

                pushOn=isChecked;
                if(isChecked)
                    ParsePush.subscribeInBackground("Shocker", new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                          //  Toast.makeText(getApplication(),"subscribe",Toast.LENGTH_LONG).show();
                        }
                    });

                else
                    ParsePush.unsubscribeInBackground("Shocker", new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                         //   Toast.makeText(getApplication(),"unsubscribe",Toast.LENGTH_LONG).show();
                        }
                    });

                if (settings!=null) {

                    settings.setPush(pushOn);
                    settings.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e != null)

                                Toast.makeText(getApplicationContext(), "Error saving settings", Toast.LENGTH_LONG).show();

                        }
                    });
                }
                break;
            case R.id.SwitchSound:

                soundOn=isChecked;

                if (settings!=null) {
                    settings.setSound(soundOn);
                    settings.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e != null)

                                Toast.makeText(getApplicationContext(), "Error saving settings", Toast.LENGTH_LONG).show();

                        }
                    });
                }


        }

    }




    private void startLogOutThread(){

        Value = 0;
        mThreadLogOut = new ThreadLogOut(mAct);
        mThreadLogOut.start();
        handler = new Handler(Looper.getMainLooper());

    }

    private void log_out()
    {

        // Start and intent for the dispatch activity
        Intent  intent = new Intent(SettingsActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }



    private void showLegalPrivacy()
    {

        new MaterialDialog.Builder(this)
                .title(R.string.terms)
                .items(R.array.legal_privacy)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showSnackbar(which + ": " + text);
                    }
                })
                .show();

    }

    private void  showSnackbar(String msg)
    {
         Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
       // Snackbar.make(get, msg, Snackbar.LENGTH_LONG).show();
    }



    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    public class ThreadLogOut extends Thread implements Runnable {
        AppCompatActivity act;

        public ThreadLogOut(AppCompatActivity mAct)
        {
            this.act=mAct;
            dialog = new ProgressDialog(act);
            dialog.setMessage(getString(R.string.progress_logout));
            dialog.show();

        }

        @Override
        public void run() {
            Looper.prepare();


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ParseUser.logOut();
                    dialog.dismiss();
                    log_out();
                }
            }, 2500);


        }
    }


}
