package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewOneStream extends AppCompatActivity {

    static String SELECTED_IMAGE = "com.ee382v.sparrow.viewonestream.SELECTED_IMAGE";
    private int start = 0;
    private String streamName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_one_stream);

        Intent intent = getIntent();
        streamName = intent.getStringExtra(ViewAllStream.SELECTED_STREAM);
        if (streamName == null) {
            String[] extras = intent.getStringArrayExtra(ViewNearbyStream.SELECTED_STREAM);
            streamName = extras[2];
        }
        TextView streamNameText = (TextView)findViewById(R.id.viewOneStream);
        streamNameText.append(streamName == null ? "" : streamName);
        makeRequest();
    }

    public void morePictures(View view) {
        start += 16;
        makeRequest();
    }

    private void makeRequest() {
        String url = MainActivity.getEndpoint() + "/android/view_all_images?stream_name=" + streamName +
                "&start=" + start;

        Log.w("current", url);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<GridItem> items = new ArrayList<>();
                try {
                    start = RequestHandler.handleImageResponse(ViewOneStream.this, R.layout.grid_item,
                            R.id.viewOneCanvas, ViewImage.class, SELECTED_IMAGE, response);
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("from on error", "on error");
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }
}
