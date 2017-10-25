package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Patrick on 10/23/2017.
 */

public class Upload extends AppCompatActivity {

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int CAMERA_PHOTO_CODE = 2046;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
    }

    public void goToCamera(View view) {
        Intent intent = new Intent(this, CamActivity.class);
        startActivityForResult(intent, CAMERA_PHOTO_CODE);
    }

    // Trigger gallery selection for a photo
    public void selectFromGallery(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_CODE) {
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    Uri photoUri = data.getData();
                    // Do something with the photo based on Uri
                    Bitmap selectedImage = null;
                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Load the selected image into a preview
                    ImageView ivPreview = (ImageView) findViewById(R.id.photoPreview);
                    ivPreview.setImageBitmap(selectedImage);
                }
            }
        }else if (requestCode == CAMERA_PHOTO_CODE){
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    Uri photoUri = data.getData();
                    //Log.d("uri_trial", "this is the photo example " + photoUri.getPath());
                    Bitmap selectedImage = null;

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    selectedImage = BitmapFactory.decodeFile(photoUri.getPath(), bmOptions);

                    // Load the selected image into a preview
                    ImageView ivPreview = (ImageView) findViewById(R.id.photoPreview);
                    ivPreview.setImageBitmap(selectedImage);
                }
            }
        }
    }
}
