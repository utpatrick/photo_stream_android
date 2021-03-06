package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ViewAllStream extends AppCompatActivity {

    static String SELECTED_STREAM = "com.ee382v.sparrow.viewallstream.SELECTED_STREAM";
    private Button MySubStreams;
    private boolean isOwner;
    String user_email = MainActivity.getUserEmail();
    public static final String SEARCH_STRING = "search";
    static String SUB_STREAM = "com.ee382v.sparrow.viewallstream.SUB_STREAM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_stream);

        MySubStreams = (Button)findViewById(R.id.viewAllSubStreams);
        Intent intent = getIntent();
        //this line is for testing only
        //String user = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String url = MainActivity.getEndpoint() + "/android/view_all_streams";
        Log.w("url: ", url);
        final String user_email = MainActivity.getUserEmail();

        JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<GridItem> items = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        items.add(new GridItem(obj.getString("stream_name"),
                                obj.getString("cover_image")));
                    }
                    GridViewAdapter adapter = new GridViewAdapter(ViewAllStream.this, R.layout.grid_item,items);
                    GridView gv = (GridView) findViewById(R.id.viewAllCanvas);
                    gv.setAdapter(adapter);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ViewAllStream.this, ViewOneStream.class);
                            TextView textView = view.findViewById(R.id.grid_item_title);
                            intent.putExtra(SELECTED_STREAM, textView.getText().toString());
                            startActivity(intent);
                        }
                    });
                    isOwner = (user_email != null);
                    updateUI(isOwner);
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

    private void updateUI(boolean isOwner){
        if(isOwner){
            MySubStreams.setVisibility(View.VISIBLE);
        }
        else{
            MySubStreams.setVisibility(View.GONE);
        }
    }

    public void goToNearbyStreams(View view) {
        Intent intent = new Intent(this, ViewNearbyStream.class);
        startActivity(intent);
    }

    public void goToSearchPage(View view) {

        String searchString  = "";
        //SearchView searchView = findViewById(R.id.viewAllSearchText);
        //String searchString = findViewById(R.id.viewAllSearchText).toString();
        EditText editText = (EditText) findViewById(R.id.viewAllSearchText);
        searchString = editText.getText().toString();
        Intent intent = new Intent(this,SearchStream.class);
        intent.putExtra(SEARCH_STRING,searchString);
        startActivity(intent);
    }

    public void toToSubstribed(View view) {
        if (MainActivity.getUserEmail() == null) return;
        Intent intent = new Intent(this, ViewOneStream.class);
        intent.putExtra(SUB_STREAM, "subscribed by " + MainActivity.getUserName());
        startActivity(intent);

    }
}
