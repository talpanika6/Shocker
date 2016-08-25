package com.example.tal.shocker.Adapters;


import android.content.Context;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tal.shocker.R;
import com.example.tal.shocker.Utility.CircularProgressBar;
import com.example.tal.shocker.fragment.HomeFragment;
import com.example.tal.shocker.model.UserPhoto;
import com.facebook.login.widget.ProfilePictureView;

import com.parse.ParseFile;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;



public class NewsFeedsAdapter extends RecyclerView.Adapter<NewsFeedsAdapter.ViewHolder>{


    private List<UserPhoto> list;
    private int rowLayout;
    private HomeFragment mAct;
    boolean canRemoveData = true;


    public NewsFeedsAdapter(List<UserPhoto> photos, int rowLayout, HomeFragment act) {
        this.list = photos;
        this.rowLayout = rowLayout;
        this.mAct = act;
    }

    public void clearFeeds() {
        int size = this.list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                list.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void addUsers(List<UserPhoto> photos) {
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
        final int index=i;



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




         //photo description
        String title=photo.getTitle().replaceAll("^\\s+|\\s+$", "");
        viewHolder.titleTextView.setText(title);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAct.startView(photo,viewHolder);
            }
        });
        /*


*/
        //create popupmenu
        viewHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View newV=view;

                PopupMenu popupMenu = new PopupMenu(mAct.getActivity(), view);
                popupMenu.inflate(R.menu.menu_list_photo);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {


                            case R.id.edit_photo:
                                mAct.editPhoto(photo);
                                return true;
                            case R.id.delete_photo:

                                //remove item from recycleview
                                removeAt(index);

                                Snackbar snackbar= Snackbar.make(newV, "Photo deleted... ", Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v){

                                                canRemoveData = false;
                                                mAct.refreshRecyclerView();

                                            }

                                        });

                                snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                                    @Override
                                    public void onViewAttachedToWindow(View v) {
                                        canRemoveData=true;
                                    }

                                    @Override
                                    public void onViewDetachedFromWindow(View v) {
                                        if(canRemoveData){

                                            mAct.deletePhoto(photo);
                                        }


                                    }
                                });
                                snackbar.show();



                                return true;
                            case R.id.share_photo:


                                mAct.sharePhoto(photo);
                                return true;

                            default:
                                return false;
                        }
                    }
                });

                // Force icons to show
                Object menuHelper;
                Class[] argTypes;
                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    menuHelper = fMenuHelper.get(popupMenu);
                    argTypes = new Class[] { boolean.class };
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {


                    popupMenu.show();
                    return;
                }


                popupMenu.show();

                // Try to force some horizontal offset
                try {
                    Field fListPopup = menuHelper.getClass().getDeclaredField("mPopup");
                    fListPopup.setAccessible(true);
                    Object listPopup = fListPopup.get(menuHelper);
                    argTypes = new Class[] { int.class };
                    Class listPopupClass = listPopup.getClass();

                    // Get the width of the popup window
                    int width = (Integer) listPopupClass.getDeclaredMethod("getWidth").invoke(listPopup);

                    // Invoke setHorizontalOffset() with the negative width to move left by that distance
                    listPopupClass.getDeclaredMethod("setHorizontalOffset", argTypes).invoke(listPopup, -width);

                    // Invoke show() to update the window's position
                    listPopupClass.getDeclaredMethod("show").invoke(listPopup);
                } catch (Exception e) {
                    // Again, an exception here indicates a programming error rather than an exceptional condition
                    // at runtime

                }



            }
        });


    }
    public void removeAt(int position) {
        list.remove(position);
        this.notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }






    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public ImageView userImage,menu ;
        public  ProgressWheel progress;

        public ViewHolder(View itemView) {
            super(itemView);

            //big photo
            userImage = (ImageView) itemView.findViewById(R.id.icon_big);
            //photo Description
            titleTextView = (TextView) itemView.findViewById(R.id.feed_dis);

            //menu
            menu=(ImageView)itemView.findViewById(R.id.feed_menu);


        }

    }
}
