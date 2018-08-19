package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

// COMPLETED (1) Create a Geofencing class with a Context and GoogleApiClient constructor that
// initializes a private member ArrayList of Geofences called mGeofenceList
public class Geofencing implements ResultCallback {

    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;
    private GoogleApiClient apiClient;
    private Context context;

    private static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; // 24 Hours
    private static final float GEOFENCE_RADIUS = 50; // 50 meters
    private static final String TAG = Geofencing.class.getSimpleName();

    public Geofencing(Context context, GoogleApiClient apiClient){
        this.context = context;
        this.apiClient = apiClient;
        geofencePendingIntent = null;
        geofenceList = new ArrayList<>();
    }

    // COMPLETED (2) Inside Geofencing, implement a public method called updateGeofencesList that
    // given a PlaceBuffer will create a Geofence object for each Place using Geofence.Builder
    // and add that Geofence to mGeofenceList
    public void updateGeofencesList(PlaceBuffer places){
        if(places == null || places.getCount() == 0) return;

        for(Place place : places){

            // Read the place info from DB cursor
            String placeUID = place.getId();
            double placeLatitude = place.getLatLng().latitude;
            double placeLongitude = place.getLatLng().longitude;

            // Build Geofence Object
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeUID)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(placeLatitude, placeLongitude, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            // Add the geofence object to the list
            geofenceList.add(geofence);
        }
    }

    // COMPLETED (3) Inside Geofencing, implement a private helper method called getGeofencingRequest that
    // uses GeofencingRequest.Builder to return a GeofencingRequest object from the Geofence list
    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);

        return builder.build();
    }

    // COMPLETED (5) Inside Geofencing, implement a private helper method called getGeofencePendingIntent that
    // returns a PendingIntent for the GeofenceBroadcastReceiver class
    private PendingIntent getGeofencePendingIntent(){

        if(geofencePendingIntent != null){
            return geofencePendingIntent;
        }

        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return  geofencePendingIntent;
    }

    // COMPLETED (6) Inside Geofencing, implement a public method called registerAllGeofences that
    // registers the GeofencingRequest by calling LocationServices.GeofencingApi.addGeofences
    // using the helper functions getGeofencingRequest() and getGeofencePendingIntent()
    public void registerAllGeofences(){
        if(apiClient == null || !apiClient.isConnected() || geofenceList == null ||
                geofenceList.size() == 0) return;

        try {
            LocationServices.GeofencingApi.addGeofences(
                    apiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, String.format("Error adding/removing geofence: %s",
                result.getStatus().toString()));
    }

    // COMPLETED (7) Inside Geofencing, implement a public method called unRegisterAllGeofences that
    // unregisters all geofences by calling LocationServices.GeofencingApi.removeGeofences
    // using the helper function getGeofencePendingIntent()
    public void unRegisterAllGeofences(){
        if(apiClient == null || !apiClient.isConnected()) return;

        try {
            LocationServices.GeofencingApi.removeGeofences(
                    apiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
