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
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{


    // 10.0.2.2 is the localhost on dev machine
    private static final String BACKEND_ENDPOINT = "http://10.0.2.2:8080";
    public static final String EXTRA_MESSAGE = "com.ees82v.sparrow.mainactivity";
    private Button SignOut;
    private SignInButton SignIn;
    //private TextView Name;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignOut = (Button)findViewById(R.id.bn_logout);
        SignIn = (SignInButton)findViewById(R.id.bn_login);
        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        //Name.setVisibility(View.GONE);
        SignOut.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bn_login:
                signIn();
                break;

            case R.id.bn_logout:
                signOut();
                break;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }


    private void handleResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            updateUI(true);
        }
        else{
            updateUI(false);
        }

    }


    private void updateUI(boolean isLogin){
        if(isLogin){
            //Name.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.GONE);
            SignOut.setVisibility(View.VISIBLE);
        }
        else{
            //Name.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
            SignOut.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }

    }
}
