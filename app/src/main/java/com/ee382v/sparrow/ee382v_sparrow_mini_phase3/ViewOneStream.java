package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.ArrayList;

public class ViewOneStream extends AppCompatActivity {

    static String SELECTED_IMAGE = "com.ee382v.sparrow.viewonestream.SELECTED_IMAGE";
    private int start = 0;
    private String message = "";
    private boolean subscribeMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_one_stream);

        Intent intent = getIntent();
        message = intent.getStringExtra(ViewAllStream.SUB_STREAM);
        String url;
        if (message != null) {
            url = MainActivity.getEndpoint() + "/android/view_sub_images?user_email=" +
                    MainActivity.getUserEmail() + "&start=" + start;
            subscribeMode = true;
        } else {
            message = intent.getStringExtra(ViewAllStream.SELECTED_STREAM);

            if (message == null) {
                String[] extras = intent.getStringArrayExtra(ViewNearbyStream.SELECTED_STREAM);
                message = extras[2];
            }
            url = MainActivity.getEndpoint() + "/android/view_all_images?stream_name=" + message +
                    "&start=" + start;
        }
        TextView streamNameText = (TextView)findViewById(R.id.viewOneStream);
        streamNameText.append(message == null ? "" : message);

        makeRequest(url);
    }

    public void morePictures(View view) {
        start += 16;
        String url;
        if (subscribeMode) {
            url = MainActivity.getEndpoint() + "/android/view_sub_images?user_email=" +
                    MainActivity.getUserEmail() + "&start=" + start;
        } else {
            url = MainActivity.getEndpoint() + "/android/view_all_images?stream_name=" + message +
                    "&start=" + start;
        }
        makeRequest(url);
    }

    private void makeRequest(String url) {
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
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    public void goToUpload(View view) {
        Intent intent = new Intent(this, Upload.class);
        startActivity(intent);
    }

    public void goToAllStreams(View view) {
        Intent intent = new Intent(this, ViewAllStream.class);
        startActivity(intent);
    }
}
