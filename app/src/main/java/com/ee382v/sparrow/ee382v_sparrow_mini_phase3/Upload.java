package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Patrick on 10/23/2017.
 */

public class Upload extends AppCompatActivity {

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int CAMERA_PHOTO_CODE = 2046;
    private static String user_email = MainActivity.getUserEmail();
    private Uri photoUri;
    private String upload_url;
    Bitmap selectedImage = null;
    Bitmap bitmap;
    String filename;
    ProgressDialog progressDialog;
    private String streamName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Button uploadButton = (Button)findViewById(R.id.uploadBtn);
        Intent intent = getIntent();
        streamName = intent.getStringExtra(ViewOneStream.THIS_STREAM);
        Log.d("stream", "stream name:  " + streamName);
        uploadButton.setEnabled(false);
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
                    photoUri = data.getData();
                    // Do something with the photo based on Uri
                    try {
                        Log.d("image", "image path:  " + photoUri.getPath());
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                        Log.d("image", "selectedImage1: " + selectedImage);
                        filename = ImageFilePath.getPath(Upload.this, data.getData()).substring(ImageFilePath.getPath(Upload.this, data.getData()).lastIndexOf("/")+1);
                        Log.d("camera", "gallery file name:  " + filename);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Load the selected image into a preview
                    ImageView ivPreview = (ImageView) findViewById(R.id.photoPreview);
                    ivPreview.setImageBitmap(selectedImage);
                    Button uploadButton = (Button)findViewById(R.id.uploadBtn);
                    uploadButton.setEnabled(true);
                }
            }
        }else if (requestCode == CAMERA_PHOTO_CODE){
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    photoUri = data.getData();
                    Log.d("camera", "camera path:  " + photoUri.getPath());
                    filename = photoUri.getPath().substring(photoUri.getPath().lastIndexOf("/")+1);
                    Log.d("camera", "file name:  " + filename);

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    selectedImage = BitmapFactory.decodeFile(photoUri.getPath(), bmOptions);
                    Log.d("image", "selectedImage1: " + selectedImage);

                    // Load the selected image into a preview
                    ImageView ivPreview = (ImageView) findViewById(R.id.photoPreview);
                    ivPreview.setImageBitmap(selectedImage);
                    Button uploadButton = (Button)findViewById(R.id.uploadBtn);
                    uploadButton.setEnabled(true);
                }
            }
        }
    }

    public void uploadImageBtn(View view){
        getUploadUrl(photoUri, "gg", "ggg");
    }

    private void getUploadUrl(final Uri imagePath, final String photoComment, final String location){
        final String request_url = MainActivity.getEndpoint() + "/android/upload_image_url";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, request_url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    upload_url = response.getString("uploadURL");
                    Log.w("upload_url", "upload_url: " + upload_url);
                    uploadImage(upload_url);
                } catch (JSONException e) {
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

    private void uploadImage(String request_url){

        progressDialog = new ProgressDialog(Upload.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();

        final ImageView ivPreview = (ImageView) findViewById(R.id.photoPreview);

        Log.d("image", "selectedImage2: " + selectedImage);
        //converting image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final byte[] encodedImage = Base64.encode(imageBytes, Base64.DEFAULT);
        Log.d("image", "encodedimage: " + encodedImage);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, request_url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", filename);
                params.put("stream", streamName);
                params.put("user_email", MainActivity.getUserEmail());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("image", new DataPart("trial", AppHelper.getFileDataFromDrawable(getBaseContext(), ivPreview.getDrawable()), "image/jpeg"));

                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);


    }
}
