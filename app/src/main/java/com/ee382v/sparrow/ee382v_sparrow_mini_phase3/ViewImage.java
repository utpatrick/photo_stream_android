package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Intent intent = getIntent();
        String[] content = intent.getStringArrayExtra(ViewOneStream.SELECTED_IMAGE);
        TextView textView = (TextView) findViewById(R.id.view_image_title);
        ImageView imageView = (ImageView) findViewById(R.id.view_image_image);
        textView.setText(content[1]);
        Picasso.with(this).load(content[0]).into(imageView);
    }
}
