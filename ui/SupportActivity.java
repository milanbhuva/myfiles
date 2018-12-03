package com.fourarc.videostatus.ui;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SupportActivity extends AppCompatActivity {


    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;

    private EditText support_input_email;
    private EditText support_input_message;
    private EditText support_input_name;
    private TextInputLayout support_input_layout_email;
    private TextInputLayout support_input_layout_message;
    private TextInputLayout support_input_layout_name;
    private Button support_button;
    private ProgressDialog register_progress;
    private Boolean FromLogin = false;
String name,email,message1,subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(getResources().getString(R.string.action_infos));
        initView();
        initAction();
        Bundle bundle = getIntent().getExtras() ;
        PrefManager prf= new PrefManager(getApplicationContext());


        this.name =  bundle.getString("NAME_USER");
        this.email =  bundle.getString("useremail");


        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            support_input_email.setText(Utility.getStringSharedPreferences(getApplicationContext(), Constant_store.customer_email));
            support_input_name.setText(Utility.getStringSharedPreferences(getApplicationContext(), Constant_store.customer_fullname));
        }

        nw = new NetworkConnection(SupportActivity.this);
        prgDialog = new ProgressDialog(SupportActivity.this);
        prgDialog.setCancelable(false);


    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
        return;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    

    public void initView(){
        this.support_input_email=(EditText) findViewById(R.id.support_input_email);
        this.support_input_message=(EditText) findViewById(R.id.support_input_message);
        this.support_input_name=(EditText) findViewById(R.id.support_input_name);
        this.support_input_layout_email=(TextInputLayout) findViewById(R.id.support_input_layout_email);
        this.support_input_layout_message=(TextInputLayout) findViewById(R.id.support_input_layout_message);
        this.support_input_layout_name=(TextInputLayout) findViewById(R.id.support_input_layout_name);
        this.support_button=(Button) findViewById(R.id.support_button);
    }
    public void initAction(){
        this.support_input_email.addTextChangedListener(new SupportTextWatcher(this.support_input_email));
        this.support_input_name.addTextChangedListener(new SupportTextWatcher(this.support_input_name));
        this.support_input_message.addTextChangedListener(new SupportTextWatcher(this.support_input_message));
        this.support_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager prf = new PrefManager(getApplicationContext());

                if (prf.getString("LOGGED").toString().equals("TRUE")) {
                    if (!validateEmail()) {
                        return;
                    }
                    if (!validatName()) {
                        return;
                    }
                    if (!validatMessage()) {
                        return;
                    }

                    message1=support_input_message.getText().toString();

                    new inquiry().execute();
                    //submit();


                } else {
                    Intent intent = new Intent(SupportActivity.this, LoginActivity.class);
                    startActivity(intent);
                    FromLogin = true;

                }

                //submit();
            }
        });
    }
    public void submit(){
        if (!validateEmail()) {
            return;
        }
        if (!validatName()) {
            return;
        }
        if (!validatMessage()) {
            return;
        }
        register_progress= ProgressDialog.show(this,null,getString(R.string.progress_login));


        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.addSupport(support_input_email.getText().toString(),support_input_name.getText().toString(),support_input_message.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()){
                    Toasty.success(getApplicationContext(), getResources().getString(R.string.message_sended), Toast.LENGTH_SHORT).show();
                    support_input_email.setText("");
                    support_input_message.setText("");
                    support_input_name.setText("");
                    finish();
                }else{
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


    private class inquiry extends AsyncTask<String, Void, Void> {
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
                    json.put("subject", "subject");
                    json.put("message", message1);

                    ServiceHandler sh = new ServiceHandler();
                    String response = sh.callToServer(Constant_store.API_URL + "inquery.php", ServiceHandler.POST, json);

                    //Log.e("response", response);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsMain = js.getJSONObject("response");
                    status = jsMain.getString("type");
                    message = jsMain.getString("responseMsg");

                    if (status.equals("false")) {
                    } else {

                        JSONObject jsFinal = jsMain.getJSONObject("responseData");
                       // prf.setString("NAME_USER",edit_input_name.getText().toString());






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

                    Toasty.success(getApplicationContext(), "Thank you for contact us", Toast.LENGTH_SHORT).show();
                    finish();


                    support_input_message.setText("");
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





    private boolean validatName() {
        if (support_input_name.getText().toString().trim().isEmpty() || support_input_name.getText().length()  < 3 ) {
            support_input_layout_name.setError(getString(R.string.error_short_value));
            requestFocus(support_input_name);
            return false;
        } else {
            support_input_layout_name.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatMessage() {
        if (support_input_message.getText().toString().trim().isEmpty() || support_input_message.getText().length()  < 3 ) {
            support_input_layout_message.setError(getString(R.string.error_short_value));
            requestFocus(support_input_message);
            return false;
        } else {
            support_input_layout_message.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateEmail() {
        String email = support_input_email.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            support_input_layout_email.setError(getString(R.string.error_mail_valide));
            requestFocus(support_input_email);
            return false;
        } else {
            support_input_layout_email.setErrorEnabled(false);
        }
        return true;
    }
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
                case R.id.support_input_email:
                    validateEmail();
                    break;
                case R.id.support_input_name:
                    validatName();
                    break;
                case R.id.support_input_message :
                    validatMessage();
                    break;
            }
        }
    }
}
