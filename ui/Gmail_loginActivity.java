package com.fourarc.videostatus.ui.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.manager.PrefManager;
import com.fourarc.videostatus.network.Constant_store;
import com.fourarc.videostatus.network.NetworkConnection;
import com.fourarc.videostatus.network.ServiceHandler;
import com.fourarc.videostatus.network.Utility;
import com.fourarc.videostatus.ui.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Gmail_loginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static int SPLASH_TIME_OUT = 200;
    String gEmail, gName, gId, gprofilepic;
    TextView txt_name, txt_email, txt_id;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;
    String refreshedToken;
    Dialog loading;
    String namee2, mobilee2, dob2, profile2, education2, cityy2, c_id2;
    private GoogleApiClient googleApiClient;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_login);


        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("d", "Refreshed token: " + refreshedToken);

        nw = new NetworkConnection(getApplicationContext());
        prgDialog = new ProgressDialog(Gmail_loginActivity.this);
        prgDialog.setCancelable(false);

        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_email = (TextView) findViewById(R.id.txt_email);
        txt_id = (TextView) findViewById(R.id.txt_id);

        txt_name.setVisibility(View.GONE);
        txt_email.setVisibility(View.GONE);
        txt_id.setVisibility(View.GONE);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                new ChakGmailApiOperation().execute();

            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount result1 = result.getSignInAccount();
            txt_name.setText(result1.getDisplayName());
            txt_email.setText(result1.getEmail());
            txt_id.setText(result1.getId());

            gName = txt_name.getText().toString().trim();
            gEmail = txt_email.getText().toString().trim();
            gId = txt_id.getText().toString().trim();

            // new GmailApiOperation().execute();

        } else {
            Toast.makeText(getApplicationContext(), "not log in ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

                        c_id2 = jsFinal.getString("customer_id");
                        namee2 = jsFinal.getString("customer_fullname");
                        // mobilee2 = jsFinal.getString("customer_mobile");
                        profile2 = jsFinal.getString("customer_profile_picture");


                        PrefManager prf = new PrefManager(getApplicationContext());
                        prf.setString("ID_USER", c_id2);
                        // prf.setString("SALT_USER",salt_user);
                        //prf.setString("TOKEN_USER",token_user);
                        prf.setString("NAME_USER", namee2);
                        //prf.setString("TYPE_USER",type_user);
                        prf.setString("USERN_USER", namee2);
                        prf.setString("IMAGE_USER", profile2);
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

                Snackbar snackbar = Snackbar.make(txt_id, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();
            } else {
                if ("true".equalsIgnoreCase(status)) {

                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_id, c_id2);
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_fullname, namee2);
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.profilepicture, profile2);
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
                    Snackbar snackbar = Snackbar.make(txt_id, "you are not logged in. Retry!", Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                }
            }
            super.onPostExecute(result);
        }
    }
}
