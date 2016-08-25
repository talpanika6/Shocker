package com.example.tal.shocker.fragment;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tal.shocker.Adapters.NotificationsAdapter;
import com.example.tal.shocker.R;
import com.example.tal.shocker.Utility.HidingScrollListener;
import com.example.tal.shocker.model.PhotoShare;
import com.example.tal.shocker.view.MainActivity;
import com.example.tal.shocker.view.ShockActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment  {

    MainActivity act;
    private NotificationsAdapter notAdapter;
    RecyclerView mRecyclerView;
    private List<PhotoShare> applicationList = new ArrayList<PhotoShare>();
    ProgressWheel progress;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ImageView image;

    private int resError=R.drawable.ic_cloud_off_black_48dp;
    private int resNot=R.drawable.ic_notifications_black_48dp;
    int fabId;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        act=(MainActivity)getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_notification, container, false);


        //image back
        image=(ImageView)v.findViewById(R.id.back_image);
        image.setVisibility(View.INVISIBLE);

        //setting progress bar
        progress=(ProgressWheel)v.findViewById(R.id.progress_wheel_not);
        progress.setVisibility(View.VISIBLE);



         mRecyclerView = (RecyclerView) v.findViewById(R.id.list_notification);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(act));

     //   mRecyclerView.setItemAnimator(new CustomItemAnimator());
     //   mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setVisibility(View.INVISIBLE);

        //set adapter to RecycleView
        notAdapter = new NotificationsAdapter(new ArrayList<PhotoShare>(), R.layout.item_list_not,this);
        mRecyclerView.setAdapter(notAdapter);
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

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_notification);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.primery);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new InitializePhotos(act).execute();
            }
        });


        new InitializePhotos(act).execute();
        return v;


    }


    /**
     * ************************
     * start instalize photos
     * ********
     */
    private class InitializePhotos extends AsyncTask<Void, Void, Void>
    {


        MainActivity mActl;

        public InitializePhotos(MainActivity act)
        {
            this.mActl=act;
        }

        @Override
        protected void onPreExecute() {

               notAdapter.clearPhotos();


              //start progress bar
                image.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            applicationList.clear();


            ParseQuery<PhotoShare> query = new ParseQuery<>("PhotoShare");
            query.whereEqualTo("read", false);
            query.whereEqualTo("receiver", ParseUser.getCurrentUser());
            query.orderByDescending("updatedAt");
            query.findInBackground(new FindCallback<PhotoShare>() {
                public void done(List<PhotoShare> objects, ParseException e) {

                    if (e == null) {
                        if(!objects.isEmpty())
                            notAdapter.addUsers(objects);

                        //stop progress bar
                        progress.setVisibility(View.INVISIBLE);
                        image.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (objects.isEmpty()) {
                            DialogError("No Notification");
                            setImageBack(resNot);
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
     end instalize photos
     **********/



    public void  DialogError(String msg)

    {
        // Toast.makeText(act,"Network Error!!!",Toast.LENGTH_LONG).show();
        Snackbar.make(act.getCurrentFocus(), msg, Snackbar.LENGTH_LONG).show();
    }


    public void  setImageBack(int resId)
    {

        image.setVisibility(View.VISIBLE);
        image.setImageResource(resId);

        ((MainActivity) getActivity()).enterReveal(fabId);

    }

    public void startShock(PhotoShare photoShare)
    {



        String objId=photoShare.getObjectId();

        // Start and intent for the View activity
        Intent intent = new Intent(act, ShockActivity.class);
        intent.putExtra("ShockId", objId);
        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }




}
