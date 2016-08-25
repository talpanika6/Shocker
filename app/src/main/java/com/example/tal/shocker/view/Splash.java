package com.example.tal.shocker.view;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Window;
import android.widget.Toast;

import com.example.tal.shocker.R;


public class Splash extends AppCompatActivity {


    ThreadImage mThreadImage;
    int ImageValue=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

         startImageThread();
    }

    private void startImageThread(){
        // start ImageView

        ImageValue = 0;
        mThreadImage = new ThreadImage();
        mThreadImage.start();


    }



    public class ThreadImage extends Thread implements Runnable {

        @Override
        public void run() {

            while( ImageValue <= 15 && !mThreadImage.isInterrupted()) {
                try{

                    ImageValue+=2;

                    Thread.sleep(200);
                } catch (InterruptedException e){
                    e.printStackTrace();
                    break;
                }
            }

            startActivityHome();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onStop() {
        super.onStop();
     //   startActivityHome();
    }
    private void startActivityHome()
    {
        Intent intent=new Intent(getApplicationContext(), DispatchActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}