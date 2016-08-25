package com.example.tal.shocker.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.tal.shocker.R;
import com.example.tal.shocker.model.UserPhoto;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;




public class EditFeedPhotoActivity extends AppCompatActivity {



    private UserPhoto photo;
    private EditText edit_title;
    private ImageView bigImage;
    String  objId;
    private ProgressWheel prog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_feed_photo);

        Toolbar editLayout;
        FloatingActionButton edit_done;

        //get  ObjectId from intent
        Intent intent=getIntent();
        objId=intent.getStringExtra("PhotoId");

        //Toolbar
        editLayout=(Toolbar)findViewById(R.id.EditLayout);
        setSupportActionBar(editLayout);

        edit_done=(FloatingActionButton)findViewById(R.id.done_edit);

        edit_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveObject();
            }
        });
        //getting views
        edit_title = (EditText) findViewById(R.id.title_photo);

        bigImage=(ImageView)findViewById(R.id.photo_big);



        prog=(ProgressWheel)findViewById(R.id.progressBar);
        //Toolbar
        ///set backlistener
        editLayout.setTitle(getString(R.string.title_activity_edit_feed_photo));
        //   photoLayout.setTitleTextColor(R.color.white);
        editLayout.setNavigationIcon(getDrawable(R.drawable.ic_arrow_back_white_18dp));
        editLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        //startQuery
        startQuery();

    }

    private void startQuery()
    {


        ParseQuery query = new ParseQuery("UserPhoto");
        query.getInBackground(objId, new GetCallback<UserPhoto>() {
            public void done(UserPhoto object, ParseException e) {
                if (e == null) {
                    photo = object;
                    settingView();
                } else {
                    Toast.makeText(getApplicationContext(), "no photo found....", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //put the view from the query
    public void settingView()
    {
        if (photo!=null) {
            //set Title Image
            prog.setVisibility(View.VISIBLE);
            edit_title.setText(photo.getTitle());


            //getting big photo
            ParseFile photoFile = photo.getParseFile("photo");
            if (photoFile != null) {

                Uri uri = Uri.parse(photoFile.getUrl());
                Context context = bigImage.getContext();
                Picasso.with(context)
                        .load(uri)
                        .error(R.drawable.ic_highlight_remove_black_24dp)
                        .fit()
                        .into(bigImage, new Callback() {

                            @Override
                            public void onSuccess() {
                                prog.setVisibility(View.INVISIBLE);
                                bigImage.setVisibility(View.VISIBLE);


                            }

                            @Override
                            public void onError() {

                                bigImage.setVisibility(View.INVISIBLE);
                            }
                        });
                /*
                bigImage.setParseFile(photoFile);
                bigImage.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        prog.setVisibility(View.INVISIBLE);
                    }
                });

                */
            }

        }
        else
            Toast.makeText(this, "photo null", Toast.LENGTH_LONG).show();

    }

    private void saveObject()
    {
        photo.setTitle(edit_title.getText().toString());
        photo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Toast.makeText(getApplicationContext(), "error saving changes..!!!", Toast.LENGTH_LONG).show();

            }
        });

        Toast.makeText(EditFeedPhotoActivity.this, "Saving Changes...", Toast.LENGTH_LONG).show();

        startIntent();
    }


    private void startIntent()
    {
        // Start and intent for the Profile activity

        Intent intent = new Intent(EditFeedPhotoActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }



    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }


}
