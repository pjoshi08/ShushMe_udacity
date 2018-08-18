package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 100;

    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState The Bundle that contains the data supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // COMPLETED (4) Create a GoogleApiClient with the LocationServices API and GEO_DATA_API
        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
    }

    // COMPLETED (5) Override onConnected, onConnectionSuspended and onConnectionFailed for GoogleApiClient
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connection Successful");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed");
    }

    // COMPLETED (7) Override onResume and inside it initialize the location permissions checkbox
    @Override
    protected void onResume() {
        super.onResume();

        CheckBox locationPermission = findViewById(R.id.location_permission_checkbox);
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationPermission.setChecked(false);
        } else {
            locationPermission.setChecked(true);
            locationPermission.setEnabled(false);
        }
    }

    // COMPLETED (8) Implement onLocationPermissionClicked to handle the CheckBox click event
    public void onLocationPermissionClicked(View view){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_FINE_LOCATION);
    }

    // COMPLETED (9) Implement the Add Place Button click event to show  a toast message with the permission status
    public void addButtonClick(View view){
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, R.string.no_location_permission, Toast.LENGTH_LONG).show();
        } else Toast.makeText(this, R.string.location_permission_granted,
                Toast.LENGTH_LONG).show();
    }

}
