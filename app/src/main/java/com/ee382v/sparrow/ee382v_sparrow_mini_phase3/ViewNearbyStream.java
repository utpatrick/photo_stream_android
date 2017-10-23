package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewNearbyStream extends AppCompatActivity {
    static String SELECTED_STREAM = "com.ee382v.sparrow.viewnearbystream.SELECTED_STREAM";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nearby_stream);
    }
}
