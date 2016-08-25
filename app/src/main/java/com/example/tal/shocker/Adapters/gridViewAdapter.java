package com.example.tal.shocker.Adapters;



import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.tal.shocker.R;
import com.example.tal.shocker.model.PhotoShare;
import com.example.tal.shocker.model.UserPhoto;
import com.example.tal.shocker.view.ViewActivity;
import com.parse.ParseException;
import com.parse.ParseFile;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/*
 * The InvitesAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for Invites, including a
 */

public class gridViewAdapter extends RecyclerView.Adapter<gridViewAdapter.ViewHolder> {


    private List<PhotoShare> list;
    private int rowLayout;
    private ViewActivity pAct;


    public gridViewAdapter(List<PhotoShare> images, int rowLayout, ViewActivity act) {
        this.list = images;
        this.rowLayout = rowLayout;
        this.pAct = act;
    }

    public void clearImages() {
        int size = this.list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                list.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }



    public void addImages(List<PhotoShare> images) {
        this.list.addAll(images);
        this.notifyItemRangeInserted(0, images.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        final PhotoShare photoShare = list.get(i);



        UserPhoto photo=null;
        ParseFile photoFile=null;
        try {
            photo=photoShare.getUserPhotoShock().fetchIfNeeded();
            //getting big photo
              photoFile = photo.getParseFile("photo");
        } catch (ParseException e) {
            e.printStackTrace();
        }




        if (photoFile!=null) {
            Uri uri = Uri.parse(photoFile.getUrl());
            Context context = viewHolder.image.getContext();
            Picasso.with(context)
                    .load(uri)
                    .error(R.drawable.ic_highlight_remove_black_24dp)
                    .fit()
                    .into(viewHolder.image, new Callback() {

                        @Override
                        public void onSuccess() {
                            viewHolder.image.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onError() {

                            viewHolder.image.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }



    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView image;


        public ViewHolder(View itemView) {
            super(itemView);

            // shock Image
            image = (CircleImageView) itemView.findViewById(R.id.shockImage);


        }

    }

}
