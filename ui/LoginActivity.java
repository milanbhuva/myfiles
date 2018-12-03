package com.fourarc.videostatus.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fourarc.videostatus.R;
import com.fourarc.videostatus.api.apiClient;
import com.fourarc.videostatus.api.apiRest;
import com.fourarc.videostatus.entity.ApiResponse;
import com.fourarc.videostatus.manager.PrefManager;
import com.fourarc.videostatus.network.Constant_store;
import com.fourarc.videostatus.network.NetworkConnection;
import com.fourarc.videostatus.network.ServiceHandler;
import com.fourarc.videostatus.network.Utility;
import com.fourarc.videostatus.ui.view.Gmail_loginActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private Button sign_in_button_facebook;
    private Button sign_in_button_google;

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    String gEmail, gName, gId, gprofilepic;

    private ProgressDialog register_progress;
    private TextView text_view_skip_login;
    String refreshedToken;
    String userId, accessToken,fbimage;
    String fb_profileUrl, f_id, f_email, first_name, name, last_name, city;
    String namee,profileimage,c_id;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;
    private GoogleApiClient googleApiClient;
    String namee2, mobilee2, dob2, profile2, education2, cityy2, c_id2;
    String fuseremail,guseremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();


        sign_in_button_facebook =(Button) findViewById(R.id.sign_in_button_facebook);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("d", "Refreshed token: " + refreshedToken);

        sign_in_button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFacebook();
            }
        });
        PrefManager prf= new PrefManager(getApplicationContext());

        if (prf.getString("LOGGED").toString().equals("TRUE")){
            Intent intent= new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        /*sign_in_button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, 777);
            }
        });*/



        initView();
        initAction();
        customView();
        GoogleSignIn();

        /*GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();*/
        /*FaceookSignIn();*/
      //  GoogleSignIn();

        nw = new NetworkConnection(getApplicationContext());
        prgDialog = new ProgressDialog(LoginActivity.this);
        prgDialog.setCancelable(false);


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Log.d("Success", "Login");

                userId = loginResult.getAccessToken().getUserId();
                accessToken = loginResult.getAccessToken().getToken();


                //Log.e("loginResult","loginResult"+loginResult);
                System.out.println("accesstoken : " + loginResult.getAccessToken().getToken());
                System.out.println("userid : " + loginResult.getAccessToken().getUserId());
                String detailUrl = "https://graph.facebook.com/v2.0/" + userId + "?fields=id,email,first_name,last_name,name&access_token=" + accessToken;
                fbimage = "https://graph.facebook.com/" + userId + "/picture?type=large";
                //String detailUrl = "https://graph.facebook.com/v2.0/" + userId + "?fields=id,birthday,email,first_name,hometown,name&access_token=" + accessToken;
                System.out.println("Detail Url : " + detailUrl);
                new FacebookDetailOperation(detailUrl).execute();
            }

            @Override
            public void onCancel() {
                Snackbar snackbar = Snackbar
                        .make(text_view_skip_login, "Login Cancel", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();
            }

            @Override
            public void onError(FacebookException e) {
                Snackbar snackbar = Snackbar
                        .make(text_view_skip_login, e.getMessage(), Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();
                //Log.e("Key Hash","" + e.getMessage());
                e.printStackTrace();
            }
        });





    }



    private void OpenFacebook() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends", "email", "user_hometown", "user_friends"));
    }

    public class FacebookDetailOperation extends AsyncTask<String, Void, Void> {

        String status, message;


        public FacebookDetailOperation(String tempUrl) {
            fb_profileUrl = tempUrl;
        }

        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            prgDialog.setMessage("Loading...");
            prgDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            JSONObject json = new JSONObject();

            ServiceHandler sh = new ServiceHandler();
            String jsonStr = null;
            try {
                jsonStr = sh.callToServer(fb_profileUrl, ServiceHandler.GET, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Log.e("jsonObj","jsonnnnnnn"+jsonObj);
                    f_id = jsonObj.getString("id");
                    f_email = jsonObj.getString("email");
                    first_name = jsonObj.getString("first_name");
                    last_name = jsonObj.getString("last_name");
                    name = jsonObj.getString("name");



                    //Log.e("email", f_email);
                    //Log.e("last", first_name);
                    //Log.e("last", last_name);
                    //Log.e("name", name);
                    //Log.e("fb_id", f_id);


                } catch (JSONException e) {
                    e.printStackTrace();
                    noData = true;
                }
                netConnection = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (prgDialog != null && prgDialog.isShowing()) {
                prgDialog.dismiss();
            }
            if (netConnection == false) {
                Snackbar snackbar = Snackbar
                        .make(text_view_skip_login, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();

            } else {
                if (noData == true) {
                    Snackbar snackbar = Snackbar
                            .make(text_view_skip_login, "There is server error exist.", Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                } else {
                    new ChakFacebookApiOperation().execute();
                }
            }
            super.onPostExecute(aVoid);
        }
    }

    private class ChakFacebookApiOperation extends AsyncTask<String, Void, Void> {
        String status, message;

        @Override
        protected void onPreExecute() {
            prgDialog.setMessage("Loading...");
            prgDialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            if (nw.isOnline() == true) {
                try {

                    JSONObject json = new JSONObject();
                    json.put("c_key", Constant_store.c_Key);
                    json.put("c_secret", Constant_store.c_Secret);
                    json.put("fb_id", f_id);
                    json.put("username",name);
                    json.put("email",f_email);
                    json.put("device_id", refreshedToken);
                    json.put("profile_picture",fbimage);

                    ServiceHandler sh = new ServiceHandler();
                    String response = sh.callToServer(Constant_store.API_URL + "signin.php", ServiceHandler.POST, json);

                    //Log.e("response", response);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsMain = js.getJSONObject("response");
                    status = jsMain.getString("type");
                    message = jsMain.getString("responseMsg");

                    if (status.equals("false")) {
                    } else {
                        JSONObject jsFinal = jsMain.getJSONObject("responseData");
                        c_id = jsFinal.getString("user_id");
                        namee = jsFinal.getString("user_name");
                        profileimage = jsFinal.getString("user_profile_picture");
                        fuseremail=jsFinal.getString("user_email_address");
                        PrefManager prf= new PrefManager(getApplicationContext());
                        prf.setString("ID_USER",c_id);

                        prf.setString("NAME_USER",namee);
                        prf.setString("USERN_USER",namee);
                        prf.setString("useremail",fuseremail);

                        prf.setString("TYPE_USER","facebook");
                        prf.setString("IMAGE_USER",profileimage);
                        prf.setString("LOGGED","TRUE");


                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    noData = true;
                }
                netConnection = true;
            } else {
                netConnection = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (prgDialog != null && prgDialog.isShowing()) {
                prgDialog.dismiss();
            }

            if (netConnection == false) {

                Snackbar snackbar = Snackbar
                        .make(text_view_skip_login, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();
            } else {
                if ("true".equalsIgnoreCase(status)) {
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.is_login,"1");
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_id, c_id);
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_fullname, namee);
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_email, fuseremail);

                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.profilepicture, profileimage);
                    Intent nextActivity = new Intent(getApplicationContext(), MainActivity.class);
                    nextActivity.putExtra("f_id", f_id);
                    nextActivity.putExtra("first_name", first_name);
                    nextActivity.putExtra("last_name", last_name);
                    nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    nextActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    nextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(nextActivity);
                    finish();

                } else {


                    Snackbar snackbar = Snackbar
                            .make(text_view_skip_login, message, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                }
            }
            super.onPostExecute(result);
        }
    }

    public void initView(){
        this.sign_in_button_google   =      (Button)  findViewById(R.id.sign_in_button_google);
        this.sign_in_button_facebook =      (Button)   findViewById(R.id.sign_in_button_facebook);
        this.text_view_skip_login    = (TextView) findViewById(R.id.text_view_skip_login);

    }
    public void initAction(){
        this.sign_in_button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });

        this.text_view_skip_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        this.text_view_skip_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void customView(){
        sign_in_button_google = (Button) findViewById(R.id.sign_in_button_google);
       // sign_in_button_google.setSize(SignInButton.SIZE_STANDARD);
       // TextView textView = (TextView) sign_in_button_google.getChildAt(0);
       // textView.setText(getResources().getString(R.string.login_gg_text));
    }
    public void GoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            GoogleSignInAccount result1 = result.getSignInAccount();

            gId=result1.getId().toString().trim();
            gEmail=result1.getEmail().toString().trim();
            gName=result1.getDisplayName().toString().trim();
            gprofilepic=result1.getPhotoUrl().toString();


            getResultGoogle(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {


            goMainScreen();
        } else {
            Toast.makeText(getApplicationContext(), "not log in ", Toast.LENGTH_SHORT).show();
        }
    }
    private void goMainScreen() {
        Intent intent = new Intent(this, Gmail_loginActivity.class);
        startActivity(intent);
    }

  /*  public void GoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }*/
/*
    public void FaceookSignIn(){
        sign_in_button_facebook.setReadPermissions(Arrays.asList("public_profile"));
        // Other app specific specialization
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        sign_in_button_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        getResultFacebook(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();

            }

            @Override
            public void onError(FacebookException exception) {
                Toasty.error(getApplicationContext(), "Operation has been errored ! ", Toast.LENGTH_SHORT, true).show();
            }
        });
    }
*/
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

    }
    private void getResultGoogle(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
          profile2 = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg" ;
            if (acct.getPhotoUrl()!=null){
                profile2 =  acct.getPhotoUrl().toString();
            }
            new ChakGmailApiOperation().execute();
            //signUp(acct.getId().toString(),acct.getId(), acct.getDisplayName().toString(),"google",photo);

            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        } else {

        }
    }
    public class GoogleDetailOperation extends AsyncTask<String, Void, Void> {

        String status, message;


        public GoogleDetailOperation(String tempUrl) {
            profile2 = tempUrl;
        }



        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            prgDialog.setMessage("Loading...");
            prgDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            JSONObject json = new JSONObject();

            ServiceHandler sh = new ServiceHandler();
            String jsonStr = null;
            try {
                jsonStr = sh.callToServer(profile2, ServiceHandler.GET, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Log.e("jsonObj","jsonnnnnnn"+jsonObj);
                    gId = jsonObj.getString("id");
                    gEmail = jsonObj.getString("email");
                    gName = jsonObj.getString("first_name");
                  //  last_name = jsonObj.getString("last_name");
                   // name = jsonObj.getString("name");



                    //Log.e("email", f_email);
                    //Log.e("last", first_name);
                    //Log.e("last", last_name);
                    //Log.e("name", name);
                    //Log.e("fb_id", f_id);


                } catch (JSONException e) {
                    e.printStackTrace();
                    noData = true;
                }
                netConnection = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (prgDialog != null && prgDialog.isShowing()) {
                prgDialog.dismiss();
            }
            if (netConnection == false) {
                Snackbar snackbar = Snackbar
                        .make(text_view_skip_login, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();

            } else {
                if (noData == true) {
                    Snackbar snackbar = Snackbar
                            .make(text_view_skip_login, "There is server error exist.", Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                } else {
                    new ChakGmailApiOperation().execute();
                }
            }
            super.onPostExecute(aVoid);
        }
    }

    private class ChakGmailApiOperation extends AsyncTask<String, Void, Void> {
        String status, message;

        @Override
        protected void onPreExecute() {
            prgDialog.setMessage("Loading...");
            prgDialog.show();

            /*Dialog_Loading();*/
        }

        @Override
        protected Void doInBackground(String... urls) {
            if (nw.isOnline() == true) {
                try {

                    JSONObject json = new JSONObject();
                    json.put("c_key", Constant_store.c_Key);
                    json.put("c_secret", Constant_store.c_Secret);
                    json.put("username", gName);
                    json.put("email", gEmail);
                    json.put("gplus_id", gId);
                    json.put("device_id", refreshedToken);
                    json.put("profile_picture", gprofilepic);


                    ServiceHandler sh = new ServiceHandler();
                    String response = sh.callToServer(Constant_store.API_URL + "signin.php", ServiceHandler.POST, json);

                    //Log.e("response", response);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsMain = js.getJSONObject("response");
                    status = jsMain.getString("type");
                    message = jsMain.getString("responseMsg");

                    if (status.equals("false")) {
                    } else {
                        JSONObject jsFinal = jsMain.getJSONObject("responseData");

                        c_id2 = jsFinal.getString("user_id");
                        namee2 = jsFinal.getString("user_name");
                        guseremail=jsFinal.getString("user_email_address");

                        // mobilee2 = jsFinal.getString("customer_mobile");
                        gprofilepic = jsFinal.getString("user_profile_picture");


                        PrefManager prf = new PrefManager(getApplicationContext());
                        prf.setString("ID_USER", c_id2);
                        // prf.setString("SALT_USER",salt_user);
                        //prf.setString("TOKEN_USER",token_user);
                        prf.setString("NAME_USER", namee2);
                        prf.setString("useremail",guseremail);

                        prf.setString("TYPE_USER","google");
                        prf.setString("USERN_USER", namee2);
                        prf.setString("IMAGE_USER", gprofilepic);
                        prf.setString("LOGGED", "TRUE");

                        //  dob2 = jsFinal.getString("customer_dob");
                        // education2 = jsFinal.getString("customer_education");
                        // cityy2 = jsFinal.getString("customer_city");

                      /*  Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_verified, jsFinal.getString("customer_verified"));
                        Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_mobile, jsFinal.getString("customer_mobile"));
                        Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_mobile, jsFinal.getString("mobile"));
*/
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    noData = true;
                }
                netConnection = true;
            } else {
                netConnection = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (prgDialog != null && prgDialog.isShowing()) {
                prgDialog.dismiss();
            }

            if (netConnection == false) {

                Snackbar snackbar = Snackbar.make(text_view_skip_login, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();
            } else {
                if ("true".equalsIgnoreCase(status)) {

                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_id, c_id2);
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_fullname, namee2);
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.profilepicture, gprofilepic);
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_email, guseremail);

                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.is_login, "1");
                    Intent nextActivity = new Intent(getApplicationContext(), MainActivity.class);
                    nextActivity.putExtra("gId", gId);
                    nextActivity.putExtra("first_name", gName);
                    nextActivity.putExtra("last_name", gEmail);
                    nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    nextActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    nextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    nextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(nextActivity);
                    finish();

                } else {
                    Snackbar snackbar = Snackbar.make(text_view_skip_login, "you are not logged in. Retry!", Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                }
            }
            super.onPostExecute(result);
        }
    }

    private void getResultFacebook(JSONObject object){
        Log.d(TAG, object.toString());
        try {
            signUp(object.getString("id").toString(),object.getString("id").toString(),object.getString("name").toString(),"facebook",object.getJSONObject("picture").getJSONObject("data").getString("url"));
            LoginManager.getInstance().logOut();        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void signUp(String username,String password,String name,String type,String image){
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.register(name,username,password,type,image);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    if (response.body().getCode()==200){

                        String id_user="0";
                        String name_user="x";
                        String username_user="x";
                        String salt_user="0";
                        String token_user="0";
                        String type_user="x";
                        String image_user="x";
                        String enabled="x";
                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("salt")){
                                salt_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")){
                                name_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")){
                                type_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")){
                                username_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")){
                                image_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")){
                                enabled=response.body().getValues().get(i).getValue();
                            }
                        }if (enabled.equals("true")){
                            PrefManager prf= new PrefManager(getApplicationContext());
                            prf.setString("ID_USER",id_user);
                            prf.setString("SALT_USER",salt_user);
                            prf.setString("TOKEN_USER",token_user);
                            prf.setString("NAME_USER",name_user);
                            prf.setString("TYPE_USER",type_user);
                            prf.setString("USERN_USER",username_user);
                            prf.setString("IMAGE_USER",image_user);
                            prf.setString("LOGGED","TRUE");
                            String  token = FirebaseInstanceId.getInstance().getToken();

                            updateToken(Integer.parseInt(id_user),token_user,token);


                        }else{
                            Toasty.error(getApplicationContext(),getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                    if (response.body().getCode()==500){
                        Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                    }
                }else{
                    Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                }
                register_progress.dismiss();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
            }
        });
    }
    public void updateToken(Integer id,String key,String token){
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.editToken(id,key,token);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){

                    Toasty.success(getApplicationContext(),response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                    register_progress.dismiss();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
            }
        });
    }
}

