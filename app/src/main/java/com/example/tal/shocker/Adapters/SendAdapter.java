package com.example.tal.shocker.Adapters;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.example.tal.shocker.R;
import com.example.tal.shocker.fragment.UserSendFragment;
import com.example.tal.shocker.fragment.UserSendSelectFragment;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;





/*
 * The InvitesAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for Invites, including a
 */

public class SendAdapter extends RecyclerView.Adapter<SendAdapter.ViewHolder> {


    private List<ParseUser> list;
    private int rowLayout;
    private UserSendFragment uFrag;
    private UserSendSelectFragment sFrag;
    private boolean flag;


    public SendAdapter(List<ParseUser> users, int rowLayout, UserSendFragment frag) {
        this.list = users;
        this.rowLayout = rowLayout;
        this.uFrag = frag;
        this.flag=true;
    }

    public SendAdapter(List<ParseUser> users, int rowLayout, UserSendSelectFragment frag) {
        this.list = users;
        this.rowLayout = rowLayout;
        this.sFrag = frag;
        this.flag=false;
    }

    public void clearUsers() {
        int size = this.list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                list.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void addUsers(List<ParseUser> users) {
        this.list.addAll(users);
        this.notifyItemRangeInserted(0, users.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        final ParseUser user = list.get(i);
        final int pos=i;
        //init
        String userName=null;
        String id=null;



        if (user.has("profile")) {
            JSONObject userProfile = user.getJSONObject("profile");
            try {

                userName=userProfile.getString("name");
                viewHolder.imageProfile.setProfileId(userProfile.getString("facebookId"),true);
                viewHolder.imageProfile.setCropped(true);

            } catch (JSONException e) {
                Toast.makeText(sFrag.getActivity(), "Error parsing saved user data.", Toast.LENGTH_LONG).show();
            }
        }
        else
            userName=user.getUsername();

        //set User Name
        viewHolder.title.setText(userName);


        //set circular button
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( viewHolder.button.getProgress() == 0) {
                    simulateSuccessProgress(viewHolder.button);
                    //add
                    if(!flag)
                        sFrag.addToHash(user,pos);
                      else
                        uFrag.addToHash(user,pos);
                } else {
                    viewHolder.button.setProgress(0);
                    //remove
                    if(!flag)
                        sFrag.removeFromHash(pos);
                   else
                        uFrag.removeFromHash(pos);
                }
            }
        });
    }

//buttonAnimation
    private void simulateSuccessProgress(final CircularProgressButton button) {
        ValueAnimator  widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }

    private void simulateErrorProgress(final CircularProgressButton button) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 99);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
                if (value == 99) {
                    button.setProgress(-1);
                }
            }
        });
        widthAnimation.start();
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ProfilePictureView imageProfile;
        public TextView title ;
        public CircularProgressButton button;

        public ViewHolder(View itemView) {
            super(itemView);

            // user Image
            imageProfile = (ProfilePictureView) itemView.findViewById(R.id.userImage);
            // user-first name and picture
            title = ((TextView) itemView.findViewById(R.id.userName));
            //circular button
            button=(CircularProgressButton)itemView.findViewById(R.id.circularButton);

        }

    }

}
