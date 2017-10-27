package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;

public class ViewNearbyStream extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private int start = 0;

    static String SELECTED_STREAM = "com.ee382v.sparrow.viewnearbystream.SELECTED_STREAM";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nearby_stream);
        Intent intent = new Intent();

        // code for location service
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            makeRequest();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void viewAllStreams(View view) {
        Intent intent = new Intent(this, ViewAllStream.class);
        startActivity(intent);
    }

    public void more(View view) {
        start += 16;
        makeRequest();
    }

    private void makeRequest() {
        double latitude = 0, longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        String url = MainActivity.getEndpoint() + "/android/view_nearby_images?latitude=" + latitude +
                "&longitude=" + longitude + "&start=" + start;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<GridItem> items = new ArrayList<>();
                try {
                    start = RequestHandler.handleImageResponse(ViewNearbyStream.this, R.layout.grid_item, R.id.viewNearbyCanvas,
                            ViewOneStream.class, SELECTED_STREAM, response);
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(jsonRequest);
    }

}
