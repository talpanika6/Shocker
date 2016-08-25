package com.example.tal.shocker.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;import com.example.tal.shocker.R;

import com.example.tal.shocker.view.UserPhotoActivity;

public class SheetDialogFragment extends BottomSheetDialogFragment {

    final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 4;
    final int MY_PERMISSIONS_REQUEST_CAMERA = 7;

   public static SheetDialogFragment newInstance() {
        return new SheetDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_main, container, false);

        RelativeLayout gallery=(RelativeLayout)v.findViewById(R.id.gallery);
        RelativeLayout camera=(RelativeLayout)v.findViewById(R.id.photo);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                    return;
                }

                chooseFromGallery();

            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);

                    return;
                }
                takePhoto();
            }
        });






        return v;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    chooseFromGallery();

                } else {
                   // showSnackbar("STORAGE PERMISSION IS DENIED");
                }

            }

            break;

            case MY_PERMISSIONS_REQUEST_CAMERA:

            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    takePhoto();

                } else {

                    //showSnackbar("CAMERA PERMISSION IS DENIED");
                }

            }
            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }




    /**
     * Sliding up Panel Actions
     */
    private void chooseFromGallery()
    {
        String objId="1";

        // Start and intent for the UserPhoto activity - obj1
        Intent intent = new Intent(getActivity(), UserPhotoActivity.class);
        intent.putExtra("FragmentId", objId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //takePhoto
    private void takePhoto()
    {
        String objId="2";


        // Start and intent for the UserPhoto activity-- obj2
        Intent intent = new Intent(getActivity(), UserPhotoActivity.class);
        intent.putExtra("FragmentId", objId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
