package com.fourarc.videostatus.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.api.apiClient;
import com.fourarc.videostatus.api.apiRest;
import com.fourarc.videostatus.entity.ApiResponse;
import com.fourarc.videostatus.manager.PrefManager;
import com.fourarc.videostatus.network.Constant_store;
import com.fourarc.videostatus.network.NetworkConnection;
import com.fourarc.videostatus.network.ServiceHandler;
import com.fourarc.videostatus.network.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditActivity extends AppCompatActivity {

    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;
    String name, email, facebookurl, twitterurl, instaurl;
    private EditText edit_input_email;
    private EditText edit_input_name;
    private EditText edit_input_facebook;
    private EditText edit_input_twitter;
    private EditText edit_input_instragram;
    private TextInputLayout edit_input_layout_instragram;
    private TextInputLayout edit_input_layout_twitter;
    private TextInputLayout edit_input_layout_facebook;
    private TextInputLayout edit_input_layout_name;
    private TextInputLayout edit_input_layout_email;
    private Button edit_button;
    private ProgressDialog register_progress;
    private int id;
    //private String name;
    private String image;
    private String facebook;
    // private String email;
    private String instagram;
    String fname,email1,fburl,twiturl,instagramurl;
    private String twitter;
    private ImageView image_view_user_background;
    private ImageView image_view_user_profile;
    private TextView text_view_name_user;
    private ProgressDialog register_progress_load;

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        this.id = bundle.getInt("id");
        this.fname = bundle.getString("name");
        this.image = bundle.getString("image");
        this.email1 = bundle.getString("email");
        this.fburl = bundle.getString("fburl");
        this.twiturl = bundle.getString("twiterurl");
        this.instagramurl = bundle.getString("instaurl");

        /*intent.putExtra("id",id);
        intent.putExtra("name",fname);
        intent.putExtra("email",email1);
        intent.putExtra("fburl",fburl);
        intent.putExtra("twiterurl",twiturl);
        intent.putExtra("instaurl",instagramurl);
*/
        setContentView(R.layout.activity_edit);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  SelectWallpaper();
        getSupportActionBar().setTitle(getResources().getString(R.string.edit_my_profile));
        initView();
        setUser();

        initAction();
        nw = new NetworkConnection(EditActivity.this);
        prgDialog = new ProgressDialog(EditActivity.this);
        prgDialog.setCancelable(false);

    }

    public void initView() {
        this.text_view_name_user = (TextView) findViewById(R.id.text_view_name_user);
        this.edit_input_email = (EditText) findViewById(R.id.edit_input_email);
        this.edit_input_name = (EditText) findViewById(R.id.edit_input_name);
        this.edit_input_facebook = (EditText) findViewById(R.id.edit_input_facebook);
        this.edit_input_twitter = (EditText) findViewById(R.id.edit_input_twitter);
        this.edit_input_instragram = (EditText) findViewById(R.id.edit_input_instragram);
        this.edit_input_layout_email = (TextInputLayout) findViewById(R.id.edit_input_layout_email);
        this.edit_input_layout_name = (TextInputLayout) findViewById(R.id.edit_input_layout_name);
        this.edit_input_layout_facebook = (TextInputLayout) findViewById(R.id.edit_input_layout_facebook);
        this.edit_input_layout_twitter = (TextInputLayout) findViewById(R.id.edit_input_layout_twitter);
        this.edit_input_layout_instragram = (TextInputLayout) findViewById(R.id.edit_input_layout_instragram);
        this.image_view_user_background = (ImageView) findViewById(R.id.image_view_user_background);
        this.image_view_user_profile = (ImageView) findViewById(R.id.image_view_user_profile);
        this.edit_button = (Button) findViewById(R.id.edit_button);


        edit_input_email.setText(email1);
        edit_input_name.setText(fname);
        edit_input_facebook.setText(fburl);
        edit_input_instragram.setText(instagramurl);
        edit_input_twitter.setText(twiturl);

        this.text_view_name_user.setText(fname);

        //Picasso.with(getApplicationContext()).load(Utility.getStringSharedPreferences(getApplicationContext(), Constant_store.customer_profile_picture)).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(image_view_user_profile);
       // Picasso.with(getApplicationContext()).load(Utility.getStringSharedPreferences(getApplicationContext(), Constant_store.customer_profile_picture)).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(image_view_user_background);

    }

    public void initAction() {
        this.edit_input_email.addTextChangedListener(new SupportTextWatcher(this.edit_input_email));
        this.edit_input_name.addTextChangedListener(new SupportTextWatcher(this.edit_input_name));
        this.edit_input_facebook.addTextChangedListener(new SupportTextWatcher(this.edit_input_facebook));
        this.edit_input_twitter.addTextChangedListener(new SupportTextWatcher(this.edit_input_twitter));
        this.edit_input_instragram.addTextChangedListener(new SupportTextWatcher(this.edit_input_instragram));

        this.edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             submit();

            }
        });
    }

    public void submit() {

        if (!validatName()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validateFacebook()) {
            return;
        }
        if (!validateTwitter()) {
            return;
        }
        if (!validateInstagram()) {
            return;
        }
        name = edit_input_name.getText().toString();
        email = edit_input_email.getText().toString();
        facebookurl = edit_input_facebook.getText().toString();
        twitterurl = edit_input_twitter.getText().toString();
        instaurl = edit_input_instragram.getText().toString();

        new edituser().execute();
        //   editUser();

    }

/*
    private void editUser() {
        final PrefManager prf= new PrefManager(getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            String user = prf.getString("ID_USER");
            String key = prf.getString("TOKEN_USER");

            register_progress = ProgressDialog.show(this, null, getString(R.string.progress_login));
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.editUser(Integer.parseInt(user), key, edit_input_name.getText().toString(), edit_input_email.getText().toString(), edit_input_facebook.getText().toString(), edit_input_twitter.getText().toString(), edit_input_instragram.getText().toString());
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        prf.setString("NAME_USER",edit_input_name.getText().toString());

                        Toasty.success(getApplicationContext(), getResources().getString(R.string.message_sended), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                    }
                    register_progress.dismiss();
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    register_progress.dismiss();
                    Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
*/

    private boolean validatName() {
        if (edit_input_name.getText().toString().trim().isEmpty() || edit_input_name.getText().length() < 3) {
            edit_input_layout_name.setError(getString(R.string.error_short_value));
            requestFocus(edit_input_name);
            return false;
        } else {
            edit_input_layout_name.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        if (edit_input_email.getText().toString().trim().length() == 0) {
            return true;
        }
        String email = edit_input_email.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            edit_input_layout_email.setError(getString(R.string.error_mail_valide));
            requestFocus(edit_input_email);
            return false;
        } else {
            edit_input_layout_email.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateInstagram() {
        if (edit_input_instragram.getText().toString().trim().length() == 0) {
            return true;
        }
        if (!URLUtil.isValidUrl(edit_input_instragram.getText().toString())) {
            edit_input_layout_instragram.setError(getString(R.string.invalide_url));
            requestFocus(edit_input_instragram);

            return false;
        } else {
            edit_input_layout_instragram.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateTwitter() {
        if (edit_input_twitter.getText().toString().trim().length() == 0) {
            return true;
        }
        if (!URLUtil.isValidUrl(edit_input_twitter.getText().toString())) {
            edit_input_layout_twitter.setError(getString(R.string.invalide_url));
            requestFocus(edit_input_twitter);

            return false;
        } else {
            edit_input_layout_twitter.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateFacebook() {
        if (edit_input_facebook.getText().toString().trim().length() == 0) {
            return true;
        }
        if (!URLUtil.isValidUrl(edit_input_facebook.getText().toString())) {
            edit_input_layout_facebook.setError(getString(R.string.invalide_url));
            requestFocus(edit_input_facebook);

            return false;
        } else {
            edit_input_layout_facebook.setErrorEnabled(false);
            return true;
        }
    }

    private void getUser() {
        register_progress_load = ProgressDialog.show(this, null, getString(R.string.loading_user_data));
        edit_input_name.setText(name);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.getUser(id, id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {

                    for (int i = 0; i < response.body().getValues().size(); i++) {
                        if (response.body().getValues().get(i).getName().equals("facebook")) {
                            facebook = response.body().getValues().get(i).getValue();
                            if (facebook != null) {
                                if (!facebook.isEmpty()) {
                                    if (facebook.startsWith("http://") || facebook.startsWith("https://")) {
                                        edit_input_facebook.setText(facebook);
                                    }
                                }
                            }
                        }
                        if (response.body().getValues().get(i).getName().equals("twitter")) {
                            twitter = response.body().getValues().get(i).getValue();
                            if (twitter != null) {

                                if (!twitter.isEmpty()) {
                                    if (twitter.startsWith("http://") || twitter.startsWith("https://")) {
                                        edit_input_twitter.setText(twitter);

                                    }
                                }
                            }
                        }
                        if (response.body().getValues().get(i).getName().equals("instagram")) {

                            instagram = response.body().getValues().get(i).getValue();
                            if (instagram != null) {

                                if (!instagram.isEmpty()) {
                                    if (instagram.startsWith("http://") || instagram.startsWith("https://")) {

                                        edit_input_instragram.setText(instagram);
                                    }
                                }
                            }
                        }
                        if (response.body().getValues().get(i).getName().equals("email")) {
                            email = response.body().getValues().get(i).getValue();
                            if (email != null) {
                                if (!email.isEmpty()) {
                                    edit_input_email.setText(email);
                                }
                            }
                        }

                    }

                }
                register_progress_load.hide();
                register_progress_load.dismiss();

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

                register_progress_load.hide();
                register_progress_load.dismiss();


            }
        });
    }

    public void setUser() {


        if (!image.isEmpty()){
            Picasso.with(getApplicationContext()).load(image).error(R.drawable.logo_w).placeholder(R.drawable.profile).into(this.image_view_user_background);
            Picasso.with(getApplicationContext()).load(image).error(R.drawable.logo_w).placeholder(R.drawable.profile).into(this.image_view_user_profile);

        }else{
            Picasso.with(getApplicationContext()).load(R.drawable.logo_w).error(R.drawable.logo_w).placeholder(R.drawable.profile).into(this.image_view_user_background);
            Picasso.with(getApplicationContext()).load(image).error(R.drawable.logo_w).placeholder(R.drawable.profile).into(this.image_view_user_profile);

        }
   /*     this.text_view_name_user.setText(name);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                image_view_user_background.setImageBitmap(bitmap);
                image_view_user_profile.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                image_view_user_background.setImageResource(R.mipmap.ic_launcher);
                image_view_user_profile.setImageResource(R.mipmap.ic_launcher);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        image_view_user_background.setTag(target);
        Picasso.with(this).load(image).error(R.drawable.profile).placeholder(R.drawable.profile).centerCrop().resize(100, 80).into(target);
        getUser();*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class edituser extends AsyncTask<String, Void, Void> {
        String status, message;

        @Override
        protected void onPreExecute() {
            prgDialog.setMessage("Loading...");
            prgDialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            final PrefManager prf= new PrefManager(getApplicationContext());

            if (nw.isOnline() == true) {
                try {

                    JSONObject json = new JSONObject();
                    json.put("c_key", Constant_store.c_Key);
                    json.put("c_secret", Constant_store.c_Secret);
                    json.put("user_id", Utility.getStringSharedPreferences(getApplicationContext(), Constant_store.customer_id));
                    json.put("fullname", name);
                    json.put("email", email);
                    json.put("fb_url", facebookurl);
                    json.put("twitter_url", twitterurl);
                    json.put("insta_url", instaurl);

                    ServiceHandler sh = new ServiceHandler();
                    String response = sh.callToServer(Constant_store.API_URL + "update_profile.php", ServiceHandler.POST, json);

                    //Log.e("response", response);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsMain = js.getJSONObject("response");
                    status = jsMain.getString("type");
                    message = jsMain.getString("responseMsg");

                    if (status.equals("false")) {
                    } else {

                        JSONObject jsFinal = jsMain.getJSONObject("responseData");
                        prf.setString("NAME_USER",edit_input_name.getText().toString());






                        /*c_id = jsFinal.getString("user_id");
                        namee = jsFinal.getString("user_name");
                        profileimage = jsFinal.getString("user_profile_picture");

                        PrefManager prf= new PrefManager(getApplicationContext());
                        prf.setString("ID_USER",c_id);

                        prf.setString("NAME_USER",namee);
                        prf.setString("USERN_USER",namee);
                        prf.setString("IMAGE_USER",profileimage);
                        prf.setString("LOGGED","TRUE");*/


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

               /* Snackbar snackbar = Snackbar
                        .make(text_view_skip_login, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();*/
            } else {
                if ("true".equalsIgnoreCase(status)) {

                    Toasty.success(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    /*Utility.setStringSharedPreference(getApplicationContext(), Constant_store.is_login,"1");
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_id, c_id);
                    Utility.setStringSharedPreference(getApplicationContext(), Constant_store.customer_fullname, namee);
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
                    finish();*/

                } else {


                  /*  Snackbar snackbar = Snackbar
                            .make(text_view_skip_login, message, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();*/
                }
            }
            super.onPostExecute(result);
        }
    }


/*
    public class getuserprofile extends AsyncTask<String, Void, Void> {

        String status, message;

        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            prgDialog.setMessage("Loading...");
            prgDialog.show();
        }

       */
/* @Override
        protected Void doInBackground(String... params) {
            JSONObject json = new JSONObject();


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Log.e("jsonObj","jsonnnnnnn"+jsonObj);
                    fname = jsonObj.getString("user_name");
                    email1 = jsonObj.getString("user_email_address");
                    fburl = jsonObj.getString("user_fb_url");
                    twiturl = jsonObj.getString("user_twitter_url");
                    instagramurl = jsonObj.getString("user_insta_url");


                    edit_input_name.setText(fname);
                    edit_input_email.setText(email1);
                    edit_input_facebook.setText(fburl);
                    edit_input_twitter.setText(twiturl);
                    edit_input_instragram.setText(instagramurl);

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
        }*//*


        @Override
        protected Void doInBackground(String... urls) {

            if (nw.isOnline() == true) {
                JSONObject json_main = new JSONObject();

                try {
                    json_main.put("c_key", Constant_store.c_Key);
                    json_main.put("c_secret", Constant_store.c_Secret);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(Constant_store.API_URL + "get_category.php", ServiceHandler.POST, json_main);
                    //Log.d("response", response);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsmain = js.getJSONObject("response");
                    status = jsmain.getString("type");

                    if (status.contains("true")) {


                        if (categoryArray.length() == 0) {

                        } else {

                        }

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
        protected void onPostExecute(Void aVoid) {
            if (prgDialog != null && prgDialog.isShowing()) {
                prgDialog.dismiss();
            }
            if (netConnection == false) {
               */
/* Snackbar snackbar = Snackbar
                        .make(text_view_skip_login, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();*//*


            } else {
                if (noData == true) {
                    */
/*Snackbar snackbar = Snackbar
                            .make(text_view_skip_login, "There is server error exist.", Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();*//*

                } else {
                  //  new LoginActivity.ChakFacebookApiOperation().execute();
                }
            }
            super.onPostExecute(aVoid);
        }
    }
*/


    private class SupportTextWatcher implements TextWatcher {
        private View view;

        private SupportTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_input_email:
                    validateEmail();
                    break;
                case R.id.edit_input_name:
                    validatName();
                    break;
                case R.id.edit_input_facebook:
                    validateFacebook();
                    break;
                case R.id.edit_input_twitter:
                    validateTwitter();
                    break;
                case R.id.edit_input_instragram:
                   validateInstagram();
                    break;

            }
        }
    }
}
