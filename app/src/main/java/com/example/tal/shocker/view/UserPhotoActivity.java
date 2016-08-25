package com.example.tal.shocker.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.tal.shocker.R;
import com.example.tal.shocker.fragment.CameraFragment;
import com.example.tal.shocker.fragment.SelectFragment;
import com.example.tal.shocker.model.UserPhoto;

/*
* UserPhotoActivity contains two fragments that handle
* data entry and capturing a photo of a given userPhoto.
* The Activity manages the overall userPhoto data.
*/
public class UserPhotoActivity extends FragmentActivity {

    private UserPhoto photo;

    private String objId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        photo = new UserPhoto();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_photo);

        //get  ObjectId from intent
        Intent intent=getIntent();
        objId=intent.getStringExtra("FragmentId");


        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
       //starting with cameraFrament
        if (fragment == null && objId.equals("2")) {
            fragment = new CameraFragment();
            fragment.setRetainInstance(true);
            manager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit();
        }
       else
        //starting with SelectFragment
        if (fragment == null && objId.equals("1") ) {
            fragment = new SelectFragment();

            manager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

       public UserPhoto getCurrentUserPhoto() {
                 return photo;
             }





    @Override
    public void onBackPressed() {

           super.onBackPressed();
        }

    }



