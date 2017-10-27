package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    // 10.0.2.2 is the localhost on dev machine
    //Now the End_Point is my deployed project for the convience of testing
    private static final String BACKEND_ENDPOINT = "https://minitrial-181200.appspot.com/";
    public static final String EXTRA_MESSAGE = "com.ees82v.sparrow.mainactivity";
    private Button SignOut;
    private SignInButton SignIn;
    private GoogleApiClient googleApiClient;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private VideoView mVideoView;
    private static final int REQ_CODE = 9001;
    static String name;
    static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String user_id = loginResult.getAccessToken().getUserId();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        updateUI(true);


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","first_name, last_name, email, id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        mVideoView = (VideoView)findViewById(R.id.bgvideoview);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ut);
        mVideoView.setVideoURI(uri);
        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);

            }
        });

        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String user_id = loginResult.getAccessToken().getUserId();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        updateUI(true);


                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","first_name, last_name, email, id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        SignOut = (Button)findViewById(R.id.bn_logout);
        SignIn = (SignInButton)findViewById(R.id.bn_login);
        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        //Name.setVisibility(View.GONE);
        SignOut.setVisibility(View.GONE);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope(Scopes.PLUS_LOGIN)).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }




    public static String getUserEmail(){
        return email;
    }

    public static String getUserName() {
        return name;
    }

    public void goToView(View view) {
        Intent intent = new Intent(MainActivity.this, ViewAllStream.class);
        EditText nameField = (EditText) findViewById(R.id.mainName);
        intent.putExtra(EXTRA_MESSAGE, nameField.getText().toString());
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
        startActivityForResult(intent, REQ_CODE);
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
            if (account != null) {
                this.name = account.getDisplayName();
                this.email = account.getEmail();
                updateUI(true);
            }
            Intent intent = new Intent(this, ViewAllStream.class);
            startActivity(intent);
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
            loginButton.setVisibility(View.GONE);
            mVideoView = (VideoView)findViewById(R.id.bgvideoview);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ut);
            mVideoView.setVideoURI(uri);
            mVideoView.start();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);

                }
            });
        }
        else{
            //Name.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
            SignOut.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            mVideoView = (VideoView)findViewById(R.id.bgvideoview);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ut);
            mVideoView.setVideoURI(uri);
            mVideoView.start();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);

                }
            });

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mVideoView = (VideoView)findViewById(R.id.bgvideoview);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ut);
        mVideoView.setVideoURI(uri);
        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }

    }
    public void goToViewAllStreams(View view) {
        Intent intent = new Intent(this, ViewAllStream.class);
        startActivity(intent);
    }
}
