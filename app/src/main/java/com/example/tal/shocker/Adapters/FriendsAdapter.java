package com.example.tal.shocker.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tal.shocker.R;
import com.example.tal.shocker.fragment.FriendsFragment;
import com.example.tal.shocker.model.UserPhoto;
import com.parse.ParseFile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;



public class FriendsAdapter  extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{


        private List<UserPhoto> list;
        private int rowLayout;
        private FriendsFragment mAct;




        public FriendsAdapter(List<UserPhoto> photos, int rowLayout, FriendsFragment act) {
            this.list = photos;
            this.rowLayout = rowLayout;
            this.mAct = act;
        }

        public void clearFriendsPhotos() {
            int size = this.list.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    list.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }

        public void addFriendsPhotos(List<UserPhoto> photos) {
            this.list.addAll(photos);
            this.notifyItemRangeInserted(0, photos.size() - 1);
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {

            final UserPhoto photo= list.get(i);


            //getting big photo
             ParseFile photoFile = photo.getParseFile("photo");


            if (photoFile!=null) {
                Uri uri = Uri.parse(photoFile.getUrl());
                Context context = viewHolder.userImage.getContext();
                Picasso.with(context)
                        .load(uri)
                        .error(R.drawable.ic_highlight_remove_black_24dp)
                        .fit()
                        .into(viewHolder.userImage, new Callback() {

                            @Override
                            public void onSuccess() {
                                viewHolder.userImage.setVisibility(View.VISIBLE);


                            }

                            @Override
                            public void onError() {

                                viewHolder.userImage.setVisibility(View.INVISIBLE);
                            }
                        });
            }



           //big view
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   mAct.showPhoto(photo);

                }
            });


        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView userImage ;


            public ViewHolder(View itemView) {
                super(itemView);

                //big photo
                userImage = (ImageView) itemView.findViewById(R.id.friend_image);

            }

        }


}
