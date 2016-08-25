package com.example.tal.shocker.view;



import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tal.shocker.Shocker;
import com.example.tal.shocker.R;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;


public class ProfileActivity extends AppCompatActivity {


    Shocker app;
    private Transition.TransitionListener mEnterTransitionListener;
    private ImageView imageProfile;

    private CollapsingToolbarLayout collapsToolbarLayout;
    int viewId=R.id.root_layout;
    int cx,cy;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile);

        //get  ObjectId from intent
        Intent intent=getIntent();
        cx= intent.getIntExtra("cx",0);
        cy=intent.getIntExtra("cx",0);



        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                enterReveal(viewId, cx, cy);

                //setting View
                settingView();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };
        getWindow().getEnterTransition().addListener(mEnterTransitionListener);





    }

    private void settingView(){

         Toolbar profileTool;
         FloatingActionButton fabEdit;
         TextView mail,birthday,location,gender;


        profileTool = (Toolbar) findViewById(R.id.profileLayout);
        setSupportActionBar(profileTool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.colapseLayout) ;


        ///set backlistener

        profileTool.setTitleTextColor(getResources().getColor(R.color.white));
        profileTool.setNavigationIcon(getDrawable(R.drawable.ic_arrow_back_white_18dp));
        profileTool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //getting  user Image
        imageProfile = (ImageView) findViewById(R.id.userImage);

        //mail
        mail=(TextView)findViewById((R.id.email));
        //location
        location=(TextView)findViewById((R.id.location));
        //birthday
        birthday=(TextView)findViewById((R.id.birthday));

        //gender
        gender=(TextView)findViewById((R.id.gender));


        ParseUser currentUser = ParseUser.getCurrentUser();
        Context mContext = imageProfile.getContext();
        Uri uri=ResourceToUri(mContext, R.drawable.ic_timer_auto_black_24dp);

        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {


                //user title
                setCollapsingToolbarLayoutTitle(userProfile.getString("name"));

                //user pic
                String facebookId =userProfile.getString("facebookId");
                String facebookProfilePicUrl = "https://graph.facebook.com/"
                        + facebookId + "/picture?height=256&width=360";


                Picasso.with(mContext)
                        .load(facebookProfilePicUrl)
                        .error(R.drawable.ic_highlight_remove_black_24dp)
                        .into(imageProfile, new Callback() {

                            @Override
                            public void onSuccess() {
                                imageProfile.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onError() {

                                imageProfile.setVisibility(View.INVISIBLE);
                            }
                        });



                mail.setText(userProfile.getString("email"));



                if (userProfile.has("location")) {
                    JSONObject userLocation =new JSONObject(userProfile.getString("location"));
                    location.setText(userLocation.getString("name"));
                }
                else
                    location.setText("");

                //Toast.makeText(getApplicationContext(), userProfile.getString("location"), Toast.LENGTH_LONG).show();

                if (userProfile.has("birthday"))
                    birthday.setText(userProfile.getString("birthday"));
                else
                    birthday.setText("");

                if (userProfile.has("gender"))
                    gender.setText(userProfile.getString("gender"));
                else
                    gender.setText("");
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error parsing saved user data. profile", Toast.LENGTH_LONG).show();
            }
        }
        else {
            //user title
            setCollapsingToolbarLayoutTitle(currentUser.getUsername());

            Picasso.with(mContext)
                    .load(uri)
                    .error(R.drawable.ic_highlight_remove_black_24dp)
                    .into(imageProfile, new Callback() {

                        @Override
                        public void onSuccess() {
                            imageProfile.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onError() {

                            imageProfile.setVisibility(View.INVISIBLE);
                        }
                    });

            if (currentUser.has("account")) {
                JSONObject userAccount = currentUser.getJSONObject("account");
                try {

                    mail.setText(userAccount.getString("email"));

                    if (userAccount.has("location"))
                        location.setText(userAccount.getString("location"));
                    else
                        location.setText("");

                    if (userAccount.has("birthday"))
                        birthday.setText(userAccount.getString("birthday"));
                    else
                        birthday.setText("");

                    if (userAccount.has("gender"))
                        gender.setText(userAccount.getString("gender"));
                    else
                        gender.setText("");

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error parsing saved user data account.", Toast.LENGTH_LONG).show();
                }
            }

        }


        //fab
        fabEdit=(FloatingActionButton) findViewById(R.id.fab_button_edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditAccountActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        collapsToolbarLayout.setTitle(title);
        collapsToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        collapsToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
    }

    public static Uri ResourceToUri (Context context,int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID) );
    }


    @Override
    public void onResume()
    {
        super.onResume();
        // If the user is offline, let them know they are not connected
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni == null) || (!ni.isConnected()))
        {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.device_offline_message),
                    Toast.LENGTH_LONG).show();
        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitReveal(viewId,cx,cy);

    }

    /**
     * Transition methods
     *
     */

    void enterReveal(int id,int cx,int cy) {

        // previously invisible view
        final View myView = findViewById(id);

        // get the center for the clipping circle
      //  int cx = myView.getMeasuredWidth() / 2;
      //  int cy = myView.getMeasuredHeight() /2;

      //  int cx = (myView.getLeft() + myView.getRight()) / 2;
      //  int cy = myView.getTop();

        // get the final radius for the clipping circle
        int finalRadius = myView.getHeight();
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

       // myView.setBackgroundColor(getResources().getColor(R.color.blue_pressed));
        myView.setVisibility(View.VISIBLE);

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    void exitReveal(int id,int cx,int cy) {
        // previously visible view
        final View myView = findViewById(id);



        // get the initial radius for the clipping circle
        int initialRadius = myView.getHeight() ;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }
}