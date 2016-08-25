package com.example.tal.shocker.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tal.shocker.Adapters.FriendsAdapter;
import com.example.tal.shocker.R;
import com.example.tal.shocker.Utility.HidingScrollListener;
import com.example.tal.shocker.model.UserPhoto;
import com.example.tal.shocker.view.FriendsActivityPhoto;
import com.example.tal.shocker.view.MainActivity;
import com.example.tal.shocker.view.ViewActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {


    private List<UserPhoto> applicationList = new ArrayList<UserPhoto>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    int fabId;
    ImageView image;

    private int resError=R.drawable.ic_cloud_off_black_48dp;
    private int resFriend=R.drawable.ic_person_outline_black_18dp;
    MainActivity act;

    private FriendsAdapter friendsListAdapter;
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
        View v= inflater.inflate(R.layout.fragment_friends, container, false);


        // image back
        image=(ImageView)v.findViewById(R.id.back_image);
        image.setVisibility(View.INVISIBLE);

        //setting progress bar
        progress=(ProgressWheel)v.findViewById(R.id.progress_wheel_friend);
        progress.setVisibility(View.VISIBLE);


        //RecycleView set up
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list_friends);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // mRecyclerView.setItemAnimator(new CustomItemAnimator());
        mRecyclerView.setVisibility(View.INVISIBLE);
        //mRecyclerView.setItemAnimator(new ReboundItemAnimator());

        //create news feeds Adapter & set adapter to RecycleView
        friendsListAdapter = new FriendsAdapter(new ArrayList<UserPhoto>(), R.layout.item_list_friends_list, this);
        mRecyclerView.setAdapter(friendsListAdapter);
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


        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_friends);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.primery);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new InitializeFriends(act).execute();
            }
        });

        new InitializeFriends(act).execute();

        return v;

    }


    /**
     * ************************
     * start Friends
     * ************************
     */
    class InitializeFriends extends AsyncTask<Void, Void, Void>
    {

        MainActivity mActl;

        public InitializeFriends(MainActivity act)
        {
            this.mActl=act;
        }
        @Override
        protected void onPreExecute() {

            friendsListAdapter.clearFriendsPhotos();

                //progress bar start

                progress.setVisibility(View.VISIBLE);

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            applicationList.clear();


            ParseQuery query = new ParseQuery("UserPhoto");
            query.whereNotEqualTo("author", ParseUser.getCurrentUser());
            query.whereEqualTo("shock", false);
            query.orderByDescending("updatedAt");
            query.findInBackground(new FindCallback<UserPhoto>() {
                public void done(List<UserPhoto> objects, ParseException e) {

                    if (e == null) {
                        if (!objects.isEmpty())
                            friendsListAdapter.addFriendsPhotos(objects);


                        //progressbar finish
                        progress.setVisibility(View.INVISIBLE);
                        image.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);


                        mSwipeRefreshLayout.setRefreshing(false);

                        if (objects.isEmpty()) {
                            DialogError("No Friends");
                            setImageBack(resFriend);
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


    /**
     * ************************
     * End Friends
     * ************************
     */


    public void showPhoto(UserPhoto photo)
    {

        String objId=photo.getObjectId();

        // Start and intent for the View activity
        Intent intent = new Intent(act, FriendsActivityPhoto.class);
        intent.putExtra("PhotoId", objId);

        // Now we can start the Activity, providing the activity options as a bundle
        ActivityCompat.startActivity(act, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(act).toBundle());


    }

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

}
