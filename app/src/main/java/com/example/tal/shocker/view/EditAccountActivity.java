package com.example.tal.shocker.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;

import android.view.View;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tal.shocker.R;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditAccountActivity extends AppCompatActivity {



    private EditText emailText,birthday,locationText;
    private LocationManager locationManager;
    private String locationInput;
    private String sBirth=null;
    private RadioGroup rGroup;
    private boolean bSexFlag=false;

    final int MY_PERMISSIONS_REQUEST_LOCATION=3;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    AppCompatActivity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar accountTool;
        FloatingActionButton fab;

        setContentView(R.layout.activity_edit_account);



        act=this;
        accountTool = (Toolbar) findViewById(R.id.accountLayout);
        setSupportActionBar(accountTool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //location
       // locationImage = (ImageView) findViewById(R.id.icon_location);
        locationText = (EditText) findViewById(R.id.location);
        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();
            }
        });


       //fab
       fab=(FloatingActionButton)findViewById(R.id.fab_button_done_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buildAccount();
            }
        });

        //email
        emailText=(EditText)findViewById(R.id.email_edit);

        //calander
         myCalendar = Calendar.getInstance();

         date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateBirthdayLabel();
            }

        };

        //datePicker pop up
        birthday=(EditText)findViewById(R.id.birth_edit);
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(act, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //gender radiobox
        rGroup=(RadioGroup) findViewById(R.id.rGender);


        ///set backlistener
      //  accountTool.setTitleTextColor(getResources().getColor(color.white, Resources.Theme.);
        accountTool.setNavigationIcon(getDrawable(R.drawable.ic_arrow_back_white_18dp));
        accountTool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getLocation() {

        double latitude, longitude;

        String locationProvider = LocationManager.NETWORK_PROVIDER;
// Or use LocationManager.GPS_PROVIDER


        if (ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
             //   || ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);


            return;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);

        if(location==null) {
            //Snackbar.make(getCurrentFocus(), "Turn On Your GPS...", Snackbar.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Turn On Your GPS...", Toast.LENGTH_LONG).show();
            return;
        }

        latitude=location.getLatitude();
        longitude=location.getLongitude();



        Geocoder gCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        try {


            addresses = gCoder.getFromLocation(latitude, longitude, 1);

            if (addresses.isEmpty()) {

                Toast.makeText(getApplicationContext(), "Waiting for location....", Toast.LENGTH_LONG).show();

            }
            else if    (addresses != null && addresses.size() > 0) {
                locationInput=addresses.get(0).getLocality()+","+ addresses.get(0).getCountryName();
                locationText.setText(locationInput);
                /*
                Toast.makeText(getApplicationContext(), addresses.get(0).getFeatureName() + ","
                         + addresses.get(0).getLocality() +","
                         + addresses.get(0).getAdminArea() + ","
                         + addresses.get(0).getCountryName(), Toast.LENGTH_LONG).show();
                         */
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "exception....", Toast.LENGTH_LONG).show();
        }


    }

    private void updateBirthdayLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        sBirth=sdf.format(myCalendar.getTime());
        birthday.setText(sBirth);
    }

    private void buildAccount()
    {
        RadioButton rSexButton;


        ParseUser user=ParseUser.getCurrentUser();
        JSONObject userAccount = new JSONObject();

        String email=emailText.getText().toString();
        String location=locationText.getText().toString();

        //get selected radio button from radioGroup
        int selectedId = rGroup.getCheckedRadioButtonId();
         if (selectedId>0)
             bSexFlag=true;

        // find the radiobutton by returned id
        rSexButton = (RadioButton) findViewById(selectedId);

        //empty fields
        if(email.isEmpty() || location.contentEquals("Location")  || sBirth.isEmpty() || !bSexFlag)
        {
            Toast.makeText(EditAccountActivity.this, "One or More of the fields are empty... ", Toast.LENGTH_LONG).show();
            return;
        }

        //facebook cant be added
        if (user.has("profile") )
        {
            Toast.makeText(EditAccountActivity.this, "Facebook users cant update their profiles ", Toast.LENGTH_LONG).show();
            return;
        }


        if(!user.has("account")) {
            try {

                userAccount.put("email", emailText.getText().toString());
                userAccount.put("location",locationInput);


                userAccount.put("gender",rSexButton.getText());

                userAccount.put("birthday",sBirth);

                // Save the user account info in a user property
                user.put("account", userAccount);
                user.saveInBackground();

                Toast.makeText(EditAccountActivity.this, "Saving Changes...", Toast.LENGTH_LONG).show();
                startIntent();

            } catch (JSONException e) {
                Toast.makeText(EditAccountActivity.this, "Error parsing returned user data. " + e, Toast.LENGTH_LONG).show();
            }
        }
        else
        Toast.makeText(EditAccountActivity.this, "already have an account..later", Toast.LENGTH_LONG).show();


    }

    private void startIntent()
    {
        // Start and intent for the Profile activity

        Intent intent = new Intent(EditAccountActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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
                    Snackbar.make(getCurrentFocus(), "LOCATION PERMISSION IS DENIED", Snackbar.LENGTH_LONG).show();
                }

            }

            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        /*
        // Remove the listener you previously added
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "LOCATION PERMISSION IS DENIED", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        */
    }


    @Override
    public void onStop() {
        super.onStop();
        /*
        // Remove the listener you previously added
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          //  Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.removeUpdates(locationListener);
        */
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
        // Remove the listener you previously added
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          //  Toast.makeText(getApplicationContext(), "LOCATION PERMISSION IS DENIED", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.removeUpdates(locationListener);
        */
    }

}
