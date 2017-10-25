package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    // 10.0.2.2 is the localhost on dev machine
    private static final String BACKEND_ENDPOINT = "http://10.0.2.2:8080";
    public static final String EXTRA_MESSAGE = "com.ees82v.sparrow.mainactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    public void goToView(View view) {
        Intent intent = new Intent(MainActivity.this, ViewAllStream.class);
        EditText nameField = (EditText) findViewById(R.id.mainName);
        intent.putExtra(EXTRA_MESSAGE, nameField.getText().toString());
        startActivity(intent);
    }

    public void goToUpload(View view) {
        Intent intent = new Intent(this, Upload.class);
        startActivity(intent);
    }

    public static String getEndpoint() {
        return BACKEND_ENDPOINT;
    }
}
