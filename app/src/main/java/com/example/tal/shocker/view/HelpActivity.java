package com.example.tal.shocker.view;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.view.View;
import android.view.Window;

import com.example.tal.shocker.R;

public class HelpActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_help);

        Toolbar helpToolbar = (Toolbar) findViewById(R.id.helpLayout);
        setSupportActionBar(helpToolbar);

        ///set backlistener
        helpToolbar.setTitle(getString(R.string.title_activity_settings));
        helpToolbar.setNavigationIcon(getDrawable(R.drawable.ic_arrow_back_white_18dp));
        helpToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

}
