package com.example.tal.shocker.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.example.tal.shocker.Adapters.SendAdapter;
import com.example.tal.shocker.R;
import com.example.tal.shocker.model.PhotoShare;
import com.example.tal.shocker.model.UserPhoto;
import com.example.tal.shocker.view.MainActivity;
import com.example.tal.shocker.view.UserPhotoActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class UserSendFragment extends Fragment {

    private ParseUser currUser;
    private List<ParseUser> applicationList = new ArrayList<ParseUser>();
    private SendAdapter friendsAdapter;
    ProgressWheel progress;
    RecyclerView mRecyclerView;
    HashMap<Integer,ParseUser> hash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Toolbar sendLayout;
        ImageView done;

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_user_send, container, false);


        sendLayout = (Toolbar) v.findViewById(R.id.sendLayout);


        //create hash
        hash=new HashMap<Integer,ParseUser>();

        //done
        done=(ImageView)v.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        ///set backlistener
        sendLayout.setTitle(getString(R.string.select));
        sendLayout.setTitleTextColor(getResources().getColor(R.color.white));
        sendLayout.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_18dp));
        sendLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackTo();
            }
        });


        //setting progress bar
        progress=(ProgressWheel)v.findViewById(R.id.progressSend);
        progress.setVisibility(View.VISIBLE);

         mRecyclerView = (RecyclerView) v.findViewById(R.id.list_friends);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      //  mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setVisibility(View.INVISIBLE);

        //set adapter to RecycleView
        friendsAdapter = new SendAdapter(new ArrayList<ParseUser>(), R.layout.item_list_friends,this);
        mRecyclerView.setAdapter(friendsAdapter);

        new InitializeUsers().execute();

        return v;

    }

    /**
     * ************************
     * start instalize users
     * ********
     */
    private class InitializeUsers extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
          friendsAdapter.clearUsers();

            progress.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            applicationList.clear();


            ParseQuery<ParseUser> query = ParseUser.getQuery();
            String object=ParseUser.getCurrentUser().getUsername();
            query.whereNotEqualTo("username",object);
            query.orderByDescending("updatedAt");
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {

                    if (e == null) {
                        friendsAdapter.addUsers(objects);

                        //progressbar finish
                        progress.setVisibility(View.INVISIBLE);

                        mRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        progress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Error Loading Users....", Toast.LENGTH_LONG).show();
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
     end instalize users
     **********/


    //Hash taking care
    public void addToHash(ParseUser user,int pos)
    {
        if(!hash.containsKey(pos))
        {
            ParseUser parse;
            parse=user;
            hash.put(pos,parse);
        }
        else
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "user Exist",
                    Toast.LENGTH_SHORT).show();


    }
    public void removeFromHash(int pos)
    {
        if (!hash.isEmpty())
        {
            if(hash.containsKey(pos))
            {
                hash.remove(pos);
            }
            else {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "user not Exist",
                        Toast.LENGTH_SHORT).show();

            }
        }
        else {
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Empty",
                    Toast.LENGTH_SHORT).show();

        }

    }

    private void send()
    {
        if(hash.isEmpty()) {

            new MaterialDialog.Builder(getActivity())
                    .iconRes(R.drawable.ic_shocker_36dp)
                    .limitIconToDefaultSize()
                    .title("Friend Not Selected")
                    .content("Choose A Friend...!!")
                    .positiveText("Ok")
                    .negativeText("Cancel")
                    .show();

        }
        else
        {
            new MaterialDialog.Builder(getActivity())
                .iconRes(R.drawable.ic_shocker_36dp)
                .limitIconToDefaultSize()
                .title("Select A Friend")
                .content("Are you sure you?")
                .positiveText("Send")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        SaveToDb();
                    }


                    @Override
                    public void onNegative(MaterialDialog dialog) {
                      //Do nothing
                    }
                })
                .show();

        }
    }





    private void SaveToDb() {
        // PhotoShare photoShare = ((UserPhotoActivity) getActivity()).getPhotoShare();
        List<PhotoShare> share=new ArrayList<PhotoShare>();

        UserPhoto photo = ((UserPhotoActivity) getActivity()).getCurrentUserPhoto();
        //save to Model

        Iterator<Integer> keySetIterator = hash.keySet().iterator();

        while(keySetIterator.hasNext()){
            Integer key = keySetIterator.next();

            //create
            PhotoShare photoShare=new PhotoShare();
            photoShare.setUserPhoto(photo);
            photoShare.setReadBy(false);
            photoShare.setSender(ParseUser.getCurrentUser());
            photoShare.setReceiver(hash.get(key));
            share.add(photoShare);

        }



        //SaveToDb

        // Save the PhotoShare and return
        PhotoShare.saveAllInBackground(share, new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {

                    goBackToMain();


                } else {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Error saving: PhotoShare objects" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

        });


        // Save the UserPhoto and return
        photo.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Error saving: UserPhoto" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

        Toast.makeText(getActivity(), "Sending...", Toast.LENGTH_LONG).show();

        String notificationTitle = "Shocker";
        String notificationText = "You Were Shocked by " + ParseUser.getCurrentUser().getUsername();
        JSONObject obj = null;


        try {
            obj = new JSONObject();
            obj.put("Title", notificationTitle);
            obj.put("Text", notificationText);


        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error parsing JSON", Toast.LENGTH_LONG).show();
        }

        ParsePush push = new ParsePush();
        push.setChannel(notificationTitle);
        push.setData(obj);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    Log.d("com.parse.push", "sending push...");
                else
                    Log.d("com.parse.push", "push not send...");
            }
        });

    }


    private void BackTo()
   {
       FragmentManager fm = getActivity().getSupportFragmentManager();
       fm.popBackStack("PhotoInfoFragment",
               FragmentManager.POP_BACK_STACK_INCLUSIVE);
   }



    public void goBackToMain()
    {
        getActivity().setResult(Activity.RESULT_OK);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
