package com.example.tal.shocker.view;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.tal.shocker.R;
import com.example.tal.shocker.fragment.FriendsFragment;
import com.example.tal.shocker.fragment.HomeFragment;
import com.example.tal.shocker.fragment.NotificationFragment;
import com.example.tal.shocker.fragment.RequestFragment;
import com.example.tal.shocker.fragment.SheetDialogFragment;
import com.example.tal.shocker.model.PhotoShare;
import com.example.tal.shocker.model.UserSettings;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {


    private CircleImageView profileImage;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private AppCompatActivity act;
    private TextView notifCount;
    static boolean homeFlag;
    String itemSelected = "Home";
    int backStack;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Toolbar mainLayout;
        TextView profileName;
        TextView profileMail;
        ImageView profile_layout;
        FloatingActionButton fab;

        //setting View
        setContentView(R.layout.activity_main);


        //Toolbar
        mainLayout = (Toolbar) findViewById(R.id.toolbar);

        if (mainLayout != null) {
            setSupportActionBar(mainLayout);
        }


        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //navigation
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        View drawerHeader = navigationView.inflateHeaderView(R.layout.drawer_header);


        //names and image
        profile_layout = (ImageView) drawerHeader.findViewById(R.id.drawer_profile);
        if (profile_layout != null) {
            //On Click Profile
            profile_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    // Start and intent for the Profile activity
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("cx", (int) v.getX());
                    intent.putExtra("cy", (int) v.getY());
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ActivityCompat.startActivity(act, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(act).toBundle());

                    mDrawerLayout.closeDrawers();
                }
            });
            profileImage = (CircleImageView) drawerHeader.findViewById(R.id.drawerProfileFace);
            profileName = (TextView) drawerHeader.findViewById(R.id.txtViewName);
            profileMail = (TextView) drawerHeader.findViewById(R.id.txtViewEmail);


            String userName = null;
            ParseUser currentUser = ParseUser.getCurrentUser();
            //init
            Context mContext = profileImage.getContext();
            Uri uri = ResourceToUri(mContext, R.drawable.ic_account_circle_black_18dp);

            //Name and Profile Picture
            if (currentUser.has("profile")) {
                JSONObject userProfile = currentUser.getJSONObject("profile");
                try {


                    String facebookId = userProfile.getString("facebookId");
                    String facebookProfilePicUrl = "https://graph.facebook.com/"
                            + facebookId + "/picture?height=70&width=70";

                    Picasso.with(mContext)
                            .load(facebookProfilePicUrl)
                            .error(R.drawable.ic_highlight_remove_black_24dp)
                            .into(profileImage, new Callback() {

                                @Override
                                public void onSuccess() {
                                    profileImage.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onError() {

                                    profileImage.setVisibility(View.INVISIBLE);
                                }
                            });


                    userName = userProfile.getString("name");
                    profileMail.setText(userProfile.getString("email"));


                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error parsing saved user data profile.", Toast.LENGTH_LONG).show();
                }


            } else {
                userName = ParseUser.getCurrentUser().getUsername();
                Picasso.with(mContext)
                        .load(uri)
                        .error(R.drawable.ic_highlight_remove_black_24dp)
                        .into(profileImage, new Callback() {

                            @Override
                            public void onSuccess() {
                                profileImage.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onError() {

                                profileImage.setVisibility(View.INVISIBLE);
                            }
                        });

                if (currentUser.has("account")) {
                    JSONObject userAccount = currentUser.getJSONObject("account");
                    try {

                        profileMail.setText(userAccount.getString("email"));

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Error parsing saved user data account.", Toast.LENGTH_LONG).show();
                    }
                }
            }
            //
            profileName.setText(userName);

        }
        act = this;


        ///fab
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bsdFragment = SheetDialogFragment.newInstance();

                bsdFragment.show(MainActivity.this.getSupportFragmentManager(), "BSDialog");

            }
        });


        //set Home Fragment to Run First
        FirstRun();
        setupSettings();
        onReceivePush();


    }

    /**
     * First Run after OnCreate
     */

    private void FirstRun() {
        //first Time
        Fragment fragment = null;
        homeFlag = true;

        Class<HomeFragment> fragmentClass = HomeFragment.class;

        setTitle("Home");

        try {
            fragment = fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        backStack = 0;
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.flContent, fragment, "Home")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();

        Menu menu = navigationView.getMenu();

        switch (itemSelected) {

            case "Home":

                MenuItem item = menu.findItem(R.id.nav_home);
                item.setChecked(false);

                break;

            case "Friends":
                MenuItem item2 = menu.findItem(R.id.nav_friends);
                item2.setChecked(false);
                break;


            case "Request":

                MenuItem item4 = menu.findItem(R.id.nav_request);
                item4.setChecked(false);

                break;

            default:
                item = menu.findItem(R.id.nav_home);
                item.setChecked(false);

                break;


        }


        MenuItem item = menu.findItem(R.id.nav_home);
        item.setChecked(true);
        setTitle(item.getTitle());

        updateBadgeCounterNotification();


    }

    /**
     * create settings to user
     */

    private void setupSettings() {

        ParseQuery<UserSettings> query = new ParseQuery<>("UserSettings");
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<UserSettings>() {
            @Override
            public void done(List<UserSettings> list, ParseException e) {

                if (e == null) {
                    if (!list.isEmpty()) {
                        SettingsActivity.soundOn = list.get(0).getSound();
                        SettingsActivity.pushOn = list.get(0).getPush();

                    } else
                        createSettings();

                } else
                    showSnackbar("Error fatching user setting !!!");


            }
        });


    }

    private void createSettings() {
        UserSettings settings = new UserSettings();

        settings.setAuthor(ParseUser.getCurrentUser());
        settings.setPush(true);
        settings.setSound(true);


        settings.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null)
                    showSnackbar("Error create user setting !!!");


            }
        });
    }

    private void onReceivePush() {
        final String NOTIFICATION_STRING = "NOTIFICATION_ID";

        //cancel push
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = getIntent().getIntExtra(NOTIFICATION_STRING, -1);
        manager.cancel(notificationId);

    }


    /**
     * Fab animation
     *
     * @param id
     */

    public void enterReveal(int id) {
        // previously invisible view
        final View myView = findViewById(id);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void exitReveal(int id) {
        // previously visible view
        final View myView = findViewById(id);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

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

    public int getFabId() {
        return R.id.fab;
    }


    /**
     * Option Menu Methods
     */


    private void updateBadgeCounterNotification() {

        //update badge


        ParseQuery<PhotoShare> query = new ParseQuery<>("PhotoShare");
        query.whereEqualTo("read", false);
        query.whereEqualTo("receiver", ParseUser.getCurrentUser());
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {

                if (e == null) {
                    if (i != 0) {
                        notifCount.setVisibility(View.VISIBLE);
                        notifCount.setText(String.valueOf(i));

                    } else {
                        notifCount.setVisibility(View.INVISIBLE);
                    }

                } else
                    showSnackbar("Connection Error..!!!");


            }

        });
    }

    private void startNotificationFragment() {
        Fragment fragment = null;
        homeFlag = false;
        String Tag = "Notification";
        Class<NotificationFragment> fragmentClass = NotificationFragment.class;


        try {
            fragment = fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        backStack++;

        //replace fragment
        startNewFragment(fragment, Tag);

        setTitle(Tag);

    }


    /**
     * Navigation Drawer
     *
     * @param navigationView
     */

    private void setupDrawerContent(NavigationView navigationView) {


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        String Tag = null;
        Intent intent = null;
        Boolean intentFlag = false, defFlag = false;

        Fragment fragment = null;

        Class fragmentClass = null;
        switch (menuItem.getItemId()) {

            case R.id.nav_home:
                Tag = "Home";
                fragmentClass = HomeFragment.class;
                homeFlag = true;
                break;
            case R.id.nav_friends:
                Tag = "Friends";
                fragmentClass = FriendsFragment.class;
                homeFlag = false;
                break;
            case R.id.nav_request:
                Tag = "Request";
                fragmentClass = RequestFragment.class;
                homeFlag = false;
                break;
            case R.id.nav_feedback:
                intentFlag = true;
                showFeedback();
                break;
            case R.id.nav_help:
                intentFlag = true;
                // Start and intent for the settings activity
                intent = new Intent(MainActivity.this, HelpActivity.class);
                ActivityCompat.startActivity(this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                break;
            case R.id.nav_settings:
                intentFlag = true;
                // Start and intent for the settings activity
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                ActivityCompat.startActivity(this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                break;

            default:
                Tag = "Home";
                defFlag = true;
                homeFlag = true;
                fragmentClass = HomeFragment.class;
                break;
        }

        if (!intentFlag) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();

                showSnackbar("Tag failed to get new instance");
            }

            backStack++;
            itemSelected = Tag;

            //replace fragment
            startNewFragment(fragment, Tag);

            // Highlight the selected item, update the title, and close the drawer
            menuItem.setChecked(true);
            if (!defFlag)
                setTitle(menuItem.getTitle());

        }

        mDrawerLayout.closeDrawers();

    }

    private void showFeedback() {

        new MaterialDialog.Builder(this)
                .title(R.string.feedback)
                .items(R.array.send_Feedback)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        showSnackbar(which + ": " + text);
                    }
                })
                .show();

    }

    /**
     * Help Methods
     */

    public static Uri ResourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    private void startNewFragment(Fragment fragment, String Tag) {
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.flContent, fragment, Tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    public void showSnackbar(String msg) {
        // Toast.makeText(this,"Error!!Please Check Your Network Connection!!!",Toast.LENGTH_LONG).show();
        Snackbar.make(getCurrentFocus(), msg, Snackbar.LENGTH_LONG).show();
    }


    /**
     * Override Methods
     */

    @Override
    public void onResume() {
        super.onResume();
        // If the user is offline, let them know they are not connected
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni == null) || (!ni.isConnected())) {
            showSnackbar("Connection Error...!!!");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.tool_notification);
        MenuItemCompat.setActionView(item, R.layout.notification_icon);
        final View view = MenuItemCompat.getActionView(item);
        notifCount = (TextView) view.findViewById(R.id.not_badge);
        ImageView image = (ImageView) view.findViewById(R.id.not_icon);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateBadgeCounterNotification();
                startNotificationFragment();
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.ref:

                FirstRun();

                break;
        }
        return true;
    }



    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        if (!homeFlag)
            FirstRun();
        else
            this.finish();


    }


}
