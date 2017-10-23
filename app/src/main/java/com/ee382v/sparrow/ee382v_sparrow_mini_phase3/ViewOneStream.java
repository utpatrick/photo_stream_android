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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewOneStream extends AppCompatActivity {

    static String SELECTED_IMAGE = "com.ee382v.sparrow.viewonestream.SELECTED_IMAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_one_stream);

        Intent intent = getIntent();
        String streamName = intent.getStringExtra(ViewAllStream.SELECTED_STREAM);
        if (streamName == null) {
            streamName = intent.getStringExtra(ViewNearbyStream.SELECTED_STREAM);
        }
        TextView streamNameText = (TextView)findViewById(R.id.viewOneStream);
        streamNameText.append(streamName == null ? "" : streamName);
        String url = MainActivity.getEndpoint() + "/android/view_all_images?stream_name=" + streamName;

        Log.w("current", url);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<GridItem> items = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        String title = obj.getString("title");
                        String link = MainActivity.getEndpoint() + "/view_image?img_id="
                                + obj.getString("key");
                        items.add(new GridItem(title, link));
                    }
                    GridViewAdapter adapter = new GridViewAdapter(ViewOneStream.this, R.layout.grid_item,items);
                    GridView gv = (GridView) findViewById(R.id.viewOneCanvas);
                    gv.setAdapter(adapter);

                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ViewOneStream.this, ViewImage.class);
                            ImageView imageView = view.findViewById(R.id.grid_item_image);
                            TextView textView = view.findViewById(R.id.grid_item_title);
                            intent.putExtra(SELECTED_IMAGE, new String[] {(String) imageView.getTag(),
                                    textView.getText().toString()});
                            startActivity(intent);
                        }
                    });
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

        ArrayList<GridItem> items = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            items.add(new GridItem("default " + i, ""));
        }
        GridViewAdapter adapter = new GridViewAdapter(this, R.layout.grid_item,items);
        GridView gv = (GridView) findViewById(R.id.viewOneCanvas);
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewOneStream.this, ViewImage.class);
                startActivity(intent);
            }
        });
    }
}
