package com.example.tal.shocker.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.parse.ParseFile;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PhotoInfoFragment extends Fragment {


    private AppCompatEditText photoDesc;
    private ImageView photoPreview;
    private AppCompatEditText locationEdit;
    private LocationManager locationManager;
    private final int MY_PERMISSIONS_REQUEST_LOCATION=8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle SavedInstanceState) {
        Toolbar photoInfoLayout;
        ImageView saveButton;
        FloatingActionButton fab;

        View v = inflater.inflate(R.layout.fragment_photo_info, parent, false);


        photoInfoLayout = (Toolbar) v.findViewById(R.id.photoInfoLayout);
        photoInfoLayout.setTitle(getString(R.string.discard));
        photoInfoLayout.setTitleTextColor(getResources().getColor(R.color.white));
        photoInfoLayout.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_18dp));
        photoInfoLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();
            }
        });

        photoDesc = ((AppCompatEditText) v.findViewById(R.id.photo_dis));

        saveButton = ((ImageView) v.findViewById(R.id.arrow_forward));
        saveButton.setOnClickListener(new View.OnClickListener() {
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
        fab=(FloatingActionButton)v.findViewById(R.id.rep);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Discrad();
            }
        });
        // user photo Preview ,
        photoPreview = (ImageView) v.findViewById(R.id.user_image);


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


    public void SaveToModel() {
        UserPhoto photo = ((UserPhotoActivity) getActivity()).getCurrentUserPhoto();


        // When the user clicks "Save," upload the meal to Parse
        // Add data to the meal object:
        photo.setTitle(photoDesc.getText().toString());
        //location
        photo.setLocation(locationEdit.getText().toString());
        // Associate the meal with the current user
        photo.setAuthor(ParseUser.getCurrentUser());
        photo.setIsSocked(false);

        //replace fragment and go to photo Send
        Fragment photoSend = new UserSendFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragmentContainer, photoSend);
        transaction.addToBackStack("PhotoInfoFragment");
        transaction.commit();
    }

    //start camera fragment again
    public void Discrad() {

        // Start camera again

        String objId = "2";
        Intent intent = new Intent(getActivity(), UserPhotoActivity.class);
        intent.putExtra("FragmentId", objId);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    /*
	 * On resume, check and see if a meal photo has been set from the
	 * CameraFragment. If it has, load the image in this fragment and make the
	 * preview image visible.
	 */
    @Override
    public void onResume() {
        super.onResume();
        ParseFile photoFile = ((UserPhotoActivity) getActivity()).getCurrentUserPhoto().getParseFile("photo");
        String uriString=((UserPhotoActivity) getActivity()).getCurrentUserPhoto().getUri();
        Uri uri=Uri.parse(uriString);

        if (photoFile != null) {
            Context context = photoPreview.getContext();
            Picasso.with(context).load(uri).into(photoPreview);

        }
    }
}