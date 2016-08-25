package com.example.tal.shocker.view;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tal.shocker.Adapters.gridViewAdapter;
import com.example.tal.shocker.R;
import com.example.tal.shocker.model.PhotoShare;
import com.example.tal.shocker.model.UserPhoto;
import com.example.tal.shocker.model.imageTime;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ViewActivity extends AppCompatActivity {


    private UserPhoto photo;
    private ImageView image,img_location;
    private TextView textView,time_text,location_text;
    private gridViewAdapter gridAdapter;
    private ImageButton imageDetails;
    LinearLayout revealView, layoutButtons;
    Animation alphaAnimation;
    float pixelDensity;
    boolean flag = true;
    private List<PhotoShare> applicationList = new ArrayList<PhotoShare>();
    String  objId;

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inside your activity
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);

        Toolbar viewLayout;
        FloatingActionButton fab;

        //get  ObjectId from intent
        Intent intent=getIntent();
        objId=intent.getStringExtra("PhotoId");

        //Toolbar
        viewLayout=(Toolbar)findViewById(R.id.viewLayout);
        setSupportActionBar(viewLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //getting views
        revealView = (LinearLayout) findViewById(R.id.linearView);
        layoutButtons = (LinearLayout) findViewById(R.id.layoutButtons);
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        pixelDensity = getResources().getDisplayMetrics().density;
        image=(ImageView)findViewById(R.id.mainView);
        textView=(TextView)findViewById(R.id.textview_title);
        //location text
        location_text=(TextView) findViewById(R.id.text_location);
        //location image
        img_location=(ImageView)findViewById(R.id.image_location);
        //time
        time_text=(TextView)findViewById(R.id.text_time);

        imageDetails = (ImageButton) findViewById(R.id.photo_shocks);
        imageDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealStart();
            }

        });


        /**
         * Set the name of the view's which will be transition to, using the static values above.
         * This could be done in the layout XML, but exposing it via static variables allows easy
         * querying from other Activities
         */
        ViewCompat.setTransitionName(image, VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(textView, VIEW_NAME_HEADER_TITLE);

        ///set backlistener
        viewLayout.setTitleTextColor(getResources().getColor(R.color.white));
        viewLayout.setNavigationIcon(getDrawable(R.drawable.ic_arrow_back_white_18dp));
        viewLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });


        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.grid_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));


        //set adapter to RecycleView
        gridAdapter = new gridViewAdapter(new ArrayList<PhotoShare>(), R.layout.item_grid_list, ViewActivity.this);
        mRecyclerView.setAdapter(gridAdapter);

        //startQuery
        startQuery();




    }



   private void startQuery()
   {


       ParseQuery<UserPhoto> query = new ParseQuery<>("UserPhoto");
       query.orderByDescending("updatedAt");
       query.getInBackground(objId, new GetCallback<UserPhoto>() {
           @Override
           public void done(UserPhoto object, ParseException e) {

               if (e == null) {

                   photo = object;


                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener())
                       settingView();

                   gridAdapter.clearImages();



                   ParseQuery<PhotoShare> query2 = new ParseQuery<>("PhotoShare");
                   query2.whereEqualTo("photo", photo);
                   query2.whereEqualTo("read", true);
                   query2.orderByDescending("updatedAt");
                   query2.findInBackground(new FindCallback<PhotoShare>() {
                       public void done(List<PhotoShare> objects, ParseException e) {

                           if (e == null) {

                               gridAdapter.addImages(objects);

                           } else {
                               Toast.makeText(getApplicationContext(), "no shock photos found....", Toast.LENGTH_LONG).show();
                           }
                       }
                   });


               } else {
                   Toast.makeText(getApplicationContext(), "no photo found....", Toast.LENGTH_LONG).show();
               }
           }


       });



   }

    //put the view from the query
    private void settingView() {
        if (photo != null) {



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

            //set Title Image
            textView.setText(photo.getTitle());

            //getting big photo
            ParseFile photoFile = photo.getParseFile("photo");
            if (photoFile != null) {

                Uri uri = Uri.parse(photoFile.getUrl());
                Context context = image.getContext();
                Picasso.with(context)
                        .load(uri)
                        .placeholder(R.drawable.ic_crop_original_black_48dp)
                        .noFade()
                        .error(R.drawable.ic_highlight_remove_black_24dp)
                        .fit()
                        .into(image, new Callback() {

                            @Override
                            public void onSuccess() {
                                image.setVisibility(View.VISIBLE);


                            }

                            @Override
                            public void onError() {

                                image.setVisibility(View.INVISIBLE);
                            }
                        });
            }



        }
        else
            Toast.makeText(this, "null", Toast.LENGTH_LONG).show();

    }



    private void revealStart()
    {

         /*
         MARGIN_RIGHT = 16;
         FAB_BUTTON_RADIUS = 28;
         */
        int x = image.getRight();
        int y = image.getBottom();
        x -= ((28 * pixelDensity) + (16 * pixelDensity));

        int hypotenuse = (int) Math.hypot(image.getWidth(), image.getHeight());

        if (flag) {

            imageDetails.setBackgroundResource(R.drawable.rounded_cancel_button);
            imageDetails.setImageResource(R.drawable.ic_close_black_24dp);

            FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams) revealView.getLayoutParams();
            parameters.height = image.getHeight();
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

            imageDetails.setBackgroundResource(R.drawable.rounded_button_view);
            imageDetails.setImageResource(R.drawable.ic_autorenew_white_24dp);

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





        @Override
        public void onBackPressed() {

            super.onBackPressed();
        }



    /**
     * Try and add a {@link Transition.TransitionListener} to the entering shared element
     * {@link Transition}. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return true if we were successful in adding a listener to the enter transition
     */
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                    settingView();

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }

        // If we reach here then we have not added a listener
        return false;
    }



}
