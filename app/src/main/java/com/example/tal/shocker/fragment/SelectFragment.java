package com.example.tal.shocker.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.tal.shocker.R;
import com.example.tal.shocker.model.UserPhoto;
import com.example.tal.shocker.view.UserPhotoActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class SelectFragment extends Fragment {


        private AppCompatEditText photoDesc;
        private ImageView photoPreview;
        private ParseFile photoFile;
        private String path;
        private AppCompatEditText locationEdit;
         private LocationManager locationManager;

        private final int MY_PERMISSIONS_REQUEST_LOCATION=9;

        //YOU CAN EDIT THIS TO WHATEVER YOU WANT
        private static final int SELECT_PICTURE = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.primeryMain));

        ///SelectPhoto
        selectPhoto();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle SavedInstanceState) {
         ImageView save;
         Toolbar selectLayout;
         FloatingActionButton fab;

        View v = inflater.inflate(R.layout.fragment_photo_info_select, parent, false);



        ///set Toolbar
        selectLayout = (Toolbar) v.findViewById(R.id.selectLayout);
        selectLayout.setTitle(getString(R.string.discard));
        selectLayout.setTitleTextColor(getResources().getColor(R.color.white));
        selectLayout.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_18dp));
        selectLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //saveToDB
            save = ((ImageView) v.findViewById(R.id.arrow_forward));
            save.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    SaveToModel();

                }
            });


        //location
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationEdit = (AppCompatEditText) v.findViewById(R.id.location_dis);
        locationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();
            }
        });


        ///fab
        fab=(FloatingActionButton)v.findViewById(R.id.rep_select);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Discrad();
            }
        });
            //photo des
            photoDesc = ((AppCompatEditText) v.findViewById(R.id.photo_dis));

            // user photo Preview ,
            photoPreview = (ImageView) v.findViewById(R.id.user_image_select);

        return v;

        }



    private void getLocation() {

         double latitude, longitude;
         String locationInput;

        String locationProvider = LocationManager.NETWORK_PROVIDER;
// Or use LocationManager.GPS_PROVIDER


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        //   || ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);


            return;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);

        if(location==null) {
            //Snackbar.make(getCurrentFocus(), "Turn On Your GPS...", Snackbar.LENGTH_LONG).show();
            Toast.makeText(getContext(), "Turn On Your GPS...", Toast.LENGTH_LONG).show();
            return;
        }

        latitude=location.getLatitude();
        longitude=location.getLongitude();



        Geocoder gCoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {


            addresses = gCoder.getFromLocation(latitude, longitude, 1);

            if (addresses.isEmpty()) {

                Toast.makeText(getContext(), "Waiting for location....", Toast.LENGTH_LONG).show();

            }
            else if    (addresses != null && addresses.size() > 0) {
                locationInput=addresses.get(0).getLocality()+","+ addresses.get(0).getCountryName();
                locationEdit.setText(locationInput);
                /*
                Toast.makeText(getApplicationContext(), addresses.get(0).getFeatureName() + ","
                         + addresses.get(0).getLocality() +","
                         + addresses.get(0).getAdminArea() + ","
                         + addresses.get(0).getCountryName(), Toast.LENGTH_LONG).show();
                         */
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "exception....", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();

                } else {
                    Snackbar.make(getView(), "LOCATION PERMISSION IS DENIED", Snackbar.LENGTH_LONG).show();
                }

            }

            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

        private void selectPhoto(){

            // select a file
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, ""), SELECT_PICTURE);
        }




        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode,data);

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_PICTURE) {

                    Uri selectedImageURI = data.getData();
                    //compress Image
                    saveScaledPhoto(getPath(selectedImageURI),selectedImageURI);

                }
                else
                    Toast.makeText(getActivity(), "You haven't picked Image",
                            Toast.LENGTH_LONG).show();

            }
        }

        //get Image From Gallery Path
        private String getPath(Uri uri) {
            if( uri == null ) {

                Toast.makeText(getActivity(),
                        "uri null: ",
                        Toast.LENGTH_LONG).show();
                return null;
            }

            String[] projection = { MediaStore.Images.Media.DATA };

            android.database.Cursor cursor;


            cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            try
            {
                int column_index = cursor.getColumnIndex(projection[0]);

                path = cursor.getString(column_index);

                cursor.close();
            }
            catch(NullPointerException e) {
                Toast.makeText(getActivity(),
                        "curset failed",
                        Toast.LENGTH_LONG).show();
                cursor.close();
            }
            return path;
        }

    public  int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public  Bitmap decodeSampledBitmapFromResource( String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return  BitmapFactory.decodeFile(path, options);
    }

        private void saveScaledPhoto(String path,Uri uri) {

            // Resize photo from camera byte array

            if (path!=null) {

                Bitmap userImageScaled = decodeSampledBitmapFromResource(path, 600, 400);

                // Override Android default landscape orientation and save portrait.
                Matrix matrix = new Matrix();
                matrix.postRotate(0);
                Bitmap rotatedScaledUserImage = Bitmap.createBitmap(userImageScaled, 0,
                        0, userImageScaled.getWidth(), userImageScaled.getHeight(),
                        matrix, true);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                rotatedScaledUserImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                byte[] scaledData = bos.toByteArray();

                // Save the scaled image to Parse
                photoFile = new ParseFile("user_photo.jpg", scaledData);
                photoFile.saveInBackground(new SaveCallback() {

                    public void done(ParseException e) {
                        if (e == null) {
                            //save photo file to model
                            ((UserPhotoActivity) getActivity()).getCurrentUserPhoto().setPhotoFile(
                                    photoFile);

                        } else {

                            Toast.makeText(getActivity(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });



                ///set image to preview
                if (photoFile!=null) {
                    ((UserPhotoActivity) getActivity()).getCurrentUserPhoto().setUri(uri.toString());
                    Context context = photoPreview.getContext();
                    Picasso.with(context).load(uri).into(photoPreview);
                }

            }

        }

        private void SaveToModel()
        {
            UserPhoto photo = ((UserPhotoActivity) getActivity()).getCurrentUserPhoto();


           ///save title and author to model
            photo.setTitle(photoDesc.getText().toString());
            //location
            photo.setLocation(locationEdit.getText().toString());
            photo.setIsSocked(false);
            photo.setAuthor(ParseUser.getCurrentUser());

            //replace fragment to userSendSelectFragment
            Fragment userSend = new UserSendSelectFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, userSend);
            transaction.addToBackStack("SelectFragment");
            transaction.commit();
        }

    /*
	 * On resume, check and see if a meal photo has been set from the
	 * CameraFragment. If it has, load the image in this fragment and make the
	 * preview image visible.
	 */
    @Override
    public void onResume() {
        super.onResume();
        ParseFile photoFile = ((UserPhotoActivity) getActivity())
                .getCurrentUserPhoto().getPhotoFile();

        if (photoFile!=null) {
            Uri uri = Uri.parse(photoFile.getUrl());
            Context context = photoPreview.getContext();
            Picasso.with(context).load(uri).into(photoPreview);
        }

    }
        ///Discrad intent
        public void Discrad()
        {

            String objId="1";

            // Start and intent for the UserPhoto activity - obj1

            Intent intent = new Intent(getActivity(), UserPhotoActivity.class);
            intent.putExtra("FragmentId", objId);
         //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }




}
