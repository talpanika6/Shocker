package com.example.tal.shocker.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.tal.shocker.Adapters.NewsFeedsAdapter;
import com.example.tal.shocker.R;
import com.example.tal.shocker.Utility.HidingScrollListener;
import com.example.tal.shocker.model.PhotoShare;
import com.example.tal.shocker.model.UserPhoto;
import com.example.tal.shocker.view.EditFeedPhotoActivity;
import com.example.tal.shocker.view.MainActivity;
import com.example.tal.shocker.view.ViewActivity;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {

    private List<UserPhoto> applicationList = new ArrayList<UserPhoto>();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    ImageView image;

    private int resError=R.drawable.ic_cloud_off_black_48dp;
    private int resNew=R.drawable.ic_stars_black_48dp;
    MainActivity act;
     int fabId;
    final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE=2;

    private NewsFeedsAdapter feedsAdapter;
    ProgressWheel progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


     act=(MainActivity)getActivity();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);


        // image back
        image=(ImageView)v.findViewById(R.id.back_image);
        image.setVisibility(View.INVISIBLE);

        //setting progress bar
        progress=(ProgressWheel)v.findViewById(R.id.progress_wheel);
        progress.setVisibility(View.VISIBLE);


        //RecycleView set up
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list_main);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mRecyclerView.setVisibility(View.INVISIBLE);


        //create news feeds Adapter & set adapter to RecycleView
        feedsAdapter = new NewsFeedsAdapter(new ArrayList<UserPhoto>(), R.layout.item_list_feeds, this);
        mRecyclerView.setAdapter(feedsAdapter);
        fabId=((MainActivity) getActivity()).getFabId();
        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {

                ((MainActivity) getActivity()).exitReveal(fabId);

            }

            @Override
            public void onShow() {

                ((MainActivity) getActivity()).enterReveal(fabId);

            }
        });



        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_main);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.dark_gray);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new InitializeFeeds(act).execute();
            }
        });

       // callCloud();
        new InitializeFeeds(act).execute();

        return v;

    }



    /**
     * ************************
     * start Initialize Feeds
     * ********
     */
    class InitializeFeeds extends AsyncTask<Void, Void, Void>
    {

        MainActivity mActl;

        public InitializeFeeds(MainActivity act)
        {
            this.mActl=act;
        }
        @Override
        protected void onPreExecute() {

           feedsAdapter.clearFeeds();

              //progress bar start

                progress.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            applicationList.clear();


            ParseQuery<UserPhoto> query = new ParseQuery<>("UserPhoto");
            query.whereEqualTo("author", ParseUser.getCurrentUser());
            query.whereEqualTo("shock", false);
            query.orderByDescending("updatedAt");
            query.findInBackground(new FindCallback<UserPhoto>() {
                public void done(List<UserPhoto> objects, ParseException e) {

                    if (e == null) {
                        if (!objects.isEmpty())
                            feedsAdapter.addUsers(objects);


                        //progressbar finish
                        progress.setVisibility(View.INVISIBLE);
                        image.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (objects.isEmpty()) {
                            DialogError("Start Shock Your Friends, Press The Fab On the Right->");
                            setImageBack(resNew);
                        }

                    } else {

                        //progressbar finish
                        progress.setVisibility(View.INVISIBLE);

                        DialogError("Connection Error..!!!");
                        setImageBack(resError);
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {



            super.onPostExecute(result);
        }


    }


    /***************************
     end Initialize Feeds
     **********/




    public void startView(UserPhoto photo,final NewsFeedsAdapter.ViewHolder viewHolder)
    {

        String objId=photo.getObjectId();

        // Start and intent for the View activity
        Intent intent = new Intent(act, ViewActivity.class);
        intent.putExtra("PhotoId", objId);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                act,

                // Now we provide a list of Pair items which contain the view we can transitioning
                // from, and the name of the view it is transitioning to, in the launched activity
                new Pair<View, String>(viewHolder.userImage, ViewActivity.VIEW_NAME_HEADER_IMAGE),
                new Pair<View, String>(viewHolder.titleTextView,ViewActivity.VIEW_NAME_HEADER_TITLE));

        // Now we can start the Activity, providing the activity options as a bundle
        ActivityCompat.startActivity(act, intent, activityOptions.toBundle());


    }

    public void refreshRecyclerView() {new InitializeFeeds(act).execute();}




    public void sharePhoto(UserPhoto photo) {


        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant

            return ;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Sharing..");
        dialog.show();

        final String title = photo.getTitle();
        ParseFile File = photo.getParseFile("photo");
        if (File != null) {
            File.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Uri uri = getImageUri(getActivity(), bmp, title);
                    dialog.dismiss();
                    if (uri != null) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(shareIntent, "Share image using Shocker"));
                    } else
                        Toast.makeText(act, "Uri null", Toast.LENGTH_LONG).show();

                }
            });

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage,String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }

    public void editPhoto(UserPhoto photo)
    {

        String objId=photo.getObjectId();

        // Start and intent for the View activity
        Intent intent = new Intent(act, EditFeedPhotoActivity.class);
        intent.putExtra("PhotoId", objId);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void deletePhoto(UserPhoto photo) {


        photo.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    new InitializeFeeds(act).execute();

                } else
                    Toast.makeText(act, "Error delete photo", Toast.LENGTH_LONG).show();

            }
        });


        /**
         * needed cloud code for deleting userPhoto from photoShare and all his shocks
         */

        callAfterDelete();


    }

    private void callAfterDelete()
    {
        ParseCloud.callFunctionInBackground("UserPhoto", new HashMap<String, Object>(), new FunctionCallback<String>() {
            public void done(String result, ParseException e) {

                if (e == null)
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    DialogError("STORAGE PERMISSION IS GRANTED");
                 else
                    DialogError("STORAGE PERMISSION IS DENIED");
            }

            break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    public void  DialogError(String msg)

    {
       // Toast.makeText(act,"Network Error!!!",Toast.LENGTH_LONG).show();
        Snackbar.make(act.getCurrentFocus(),msg, Snackbar.LENGTH_LONG).show();
    }


    public void  setImageBack(int resId)
    {

        image.setVisibility(View.VISIBLE);
        image.setImageResource(resId);

        ((MainActivity) getActivity()).enterReveal(fabId);

    }


}
