package com.example.tal.shocker.Adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tal.shocker.R;
import com.example.tal.shocker.fragment.NotificationFragment;
import com.example.tal.shocker.model.PhotoShare;
import com.example.tal.shocker.model.UserPhoto;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tal on 1/7/2015.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {


        private List<PhotoShare> list;
        private int rowLayout;
        private NotificationFragment nAct;
        private android.os.Handler handler;



        public NotificationsAdapter(List<PhotoShare> photoShare, int rowLayout, NotificationFragment act) {
            this.list = photoShare;
            this.rowLayout = rowLayout;
            this.nAct = act;
        }

        public void clearPhotos() {
            int size = this.list.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    list.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }

        public void addUsers(List<PhotoShare> photoShare) {
            this.list.addAll(photoShare);
            this.notifyItemRangeInserted(0, photoShare.size() - 1);
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {

            final PhotoShare photoShare = list.get(i);
            /*
            Date time=photoShare.getPhotoShareDate();

            viewHolder.desc.setText(time.toString());

*/


            ParseUser user= null;
            UserPhoto photo=null;


            try {
                user = photoShare.getSender().fetchIfNeeded();
                photo=photoShare.getUserPhoto().fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //init
            Context mContext = viewHolder.imageProfile.getContext();
            Uri uri=ResourceToUri(mContext,R.drawable.ic_account_circle_black_18dp);
            if (user!=null) {
                if (user.has("profile")) {
                    JSONObject userProfile = user.getJSONObject("profile");
                    try {


                        String facebookId = userProfile.getString("facebookId");

                        String facebookProfilePicUrl = "https://graph.facebook.com/"
                                + facebookId + "/picture?height=50&width=50";

                        Picasso.with(mContext)
                                .load(facebookProfilePicUrl)
                                .error(R.drawable.ic_highlight_remove_black_18dp)
                                .into(viewHolder.imageProfile, new Callback() {

                                    @Override
                                    public void onSuccess() {
                                        viewHolder.imageProfile.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onError() {

                                        viewHolder.imageProfile.setVisibility(View.INVISIBLE);
                                    }
                                });


                    } catch (JSONException e) {
                        Toast.makeText(nAct.getActivity(), "Error parsing saved user data.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Picasso.with(mContext)
                            .load(uri)
                            .error(R.drawable.ic_highlight_remove_black_24dp)
                            .into(viewHolder.imageProfile, new Callback() {

                                @Override
                                public void onSuccess() {
                                    viewHolder.imageProfile.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onError() {

                                    viewHolder.imageProfile.setVisibility(View.INVISIBLE);
                                }
                            });
                }

            }
                //set desc
                if (photo != null)
                    viewHolder.desc.setText(photo.getTitle());

                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        nAct.startShock(photoShare);

                    }
                });


        }



    public static Uri ResourceToUri (Context context,int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID) );
    }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public CircleImageView imageProfile;
            public TextView desc;


            public ViewHolder(View itemView) {
                super(itemView);



                // user Image
                imageProfile = (CircleImageView) itemView.findViewById(R.id.userImage);
                //  desc
                desc=((TextView) itemView.findViewById(R.id.descImage));


            }





        }



}
