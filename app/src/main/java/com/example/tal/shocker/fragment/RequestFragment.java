package com.example.tal.shocker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tal.shocker.R;


public class RequestFragment extends Fragment {

    private Button sendRequestButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request, container, false);

        sendRequestButton = (Button) v.findViewById(R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  sendRequestDialog();
            }
        });

     return v;
    }

    /*
    private void sendRequestDialog() {
        Bundle params = new Bundle();
        params.putString("message", "Shocker, Shock your friends and Family");

        WebDialog requestsDialog = (
                new WebDialog.RequestsDialogBuilder(getActivity(),
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values,
                                           FacebookException error) {
                        if (error != null) {
                            if (error instanceof FacebookOperationCanceledException) {
                                Toast.makeText(getActivity(), "Request cancelled", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(),
                                        "Network Error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String requestId = values.getString("request");
                            if (requestId != null) {
                                Toast.makeText(getActivity(),
                                        "Request sent",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(),
                                        "Request cancelled",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })
                .build();
        requestsDialog.show();
    }
*/

}
