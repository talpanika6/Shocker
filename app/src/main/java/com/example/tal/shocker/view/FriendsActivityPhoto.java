package com.example.tal.shocker.view;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tal.shocker.R;
import com.example.tal.shocker.model.UserPhoto;
import com.example.tal.shocker.model.imageTime;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

public class FriendsActivityPhoto extends AppCompatActivity {


    private UserPhoto photo;
    private TextView title,userName,location_text,time_text;
    private ProfilePictureView userImage;
    private ImageView bigImage,img_location;
    private ImageButton imageDetails;
    LinearLayout revealView, layoutButtons;
    Animation alphaAnimation;
    float pixelDensity;
    boolean flag = true;
    ParseUser user;
    String  objId;




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

        setContentView(R.layout.activity_friends_photo);

        Toolbar photoLayout;



        //Toolbar
        photoLayout=(Toolbar)findViewById(R.id.photoLayout);
        setSupportActionBar(photoLayout);


        //get  ObjectId from intent
        Intent intent=getIntent();
        objId=intent.getStringExtra("PhotoId");

        pixelDensity = getResources().getDisplayMetrics().density;

        revealView = (LinearLayout) findViewById(R.id.linearView);
        layoutButtons = (LinearLayout) findViewById(R.id.layoutButtons);

        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);

        //getting views
        userName = (TextView) findViewById(R.id.userName);
        title = (TextView) findViewById(R.id.title);
        userImage=(ProfilePictureView)findViewById(R.id.userImage);
        bigImage=(ImageView)findViewById(R.id.friend_photo_big);
        //location text
        location_text=(TextView) findViewById(R.id.text_location);
        //location image
        img_location=(ImageView)findViewById(R.id.image_location);
        //time
        time_text=(TextView)findViewById(R.id.text_time);
        imageDetails = (ImageButton) findViewById(R.id.photo_detail);
        imageDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDetail();            }
        });



        photoLayout.setTitle(getString(R.string.explore));

        photoLayout.setNavigationIcon(getDrawable(R.drawable.ic_arrow_back_white_18dp));
        photoLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //startQuery
        startQuery();


    }

    private void launchDetail(){

         /*
         MARGIN_RIGHT = 16;
         FAB_BUTTON_RADIUS = 28;
         */
        int x = bigImage.getRight();
        int y = bigImage.getBottom();
        x -= ((28 * pixelDensity) + (16 * pixelDensity));

        int hypotenuse = (int) Math.hypot(bigImage.getWidth(), bigImage.getHeight());

        if (flag) {

            imageDetails.setBackgroundResource(R.drawable.rounded_cancel_button);
            imageDetails.setImageResource(R.drawable.ic_close_black_24dp);

            FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams) revealView.getLayoutParams();
            parameters.height = bigImage.getHeight();
            revealView.setLayoutParams(parameters);

            Animator anim = ViewAnimationUtils.createCircularReveal(revealView, x, y, 0, hypotenuse);
            anim.setDuration(700);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {


                }

                @Override
                public void onAnimationEnd(Animator animator) {

                    layoutButtons.setVisibility(View.VISIBLE);
                    layoutButtons.startAnimation(alphaAnimation);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            revealView.setVisibility(View.VISIBLE);
            anim.start();

            flag = false;
        } else {

            imageDetails.setBackgroundResource(R.drawable.rounded_button);
            imageDetails.setImageResource(R.drawable.ic_person_pin_black_24dp);

            Animator anim = ViewAnimationUtils.createCircularReveal(revealView, x, y, hypotenuse, 0);
            anim.setDuration(400);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    revealView.setVisibility(View.GONE);
                    layoutButtons.setVisibility(View.GONE);
                //    userImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            anim.start();
            flag = true;
        }
    }



    private void startQuery()
    {
        //prog.setVisibility(View.VISIBLE);
        ParseQuery<UserPhoto> query = new ParseQuery<>("UserPhoto");
        query.getInBackground(objId, new GetCallback<UserPhoto>() {
            public void done(UserPhoto object, ParseException e) {
                if (e == null) {
                    photo = object;
                           settingView();
                } else {
                    Toast.makeText(getApplicationContext(), "no photo found....", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //put the view from the query
    public void settingView()
    {
        if (photo!=null) {


            //set Title Image
            title.setText(photo.getTitle());

            //photo location
            if(photo.getLocation()!=null)
            {
                location_text.setText(photo.getLocation());
                location_text.setVisibility(View.VISIBLE);
                img_location.setVisibility(View.VISIBLE);
            }
            else{
                location_text.setVisibility(View.INVISIBLE);
                img_location.setVisibility(View.INVISIBLE);
            }

            //photo time
            if (photo.getPhotoDate()!=null) {

                imageTime imageT=new imageTime(photo.getPhotoDate());
                String ago=imageT.getCalcEstimateTimeDate();
                time_text.setText(ago);
            }



            //getting big photo
            ParseFile photoFile = photo.getParseFile("photo");
            if (photoFile != null)
            {

                Uri uri = Uri.parse(photoFile.getUrl());
                Context context = bigImage.getContext();
                Picasso.with(context)
                        .load(uri)
                        .placeholder(R.drawable.ic_crop_original_black_48dp)
                        .noFade()
                        .error(R.drawable.ic_highlight_remove_black_24dp)
                        .fit()
                        .into(bigImage, new Callback() {

                            @Override
                            public void onSuccess() {

                                  bigImage.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onError() {

                                   bigImage.setVisibility(View.INVISIBLE);
                            }
                        });

            }




            try {
                user = photo.getAuthor().fetchIfNeeded();

            } catch (ParseException e) {
                e.printStackTrace();
            }

            String UserName = null;

            //set UserName

            if (user.has("profile")) {
                JSONObject userProfile = user.getJSONObject("profile");
                try {


                    userImage.setProfileId(userProfile.getString("facebookId"),true);
                    userImage.setCropped(true);

                    UserName = userProfile.getString("name");

                }
                catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error parsing saved user data.", Toast.LENGTH_LONG).show();
                }

            } else
                UserName = user.getUsername();

            //set UserName
            userName.setText(UserName);




        }
        else
            Toast.makeText(this, "photo null", Toast.LENGTH_LONG).show();


    }



    @Override
    public void onBackPressed() {

            super.onBackPressed();

    }



}
