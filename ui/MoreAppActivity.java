package com.fourarc.videostatus.ui.view;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.adapter.MoreAppAdapter;
import com.fourarc.videostatus.entity.MoreApp;
import com.fourarc.videostatus.network.Constant_store;
import com.fourarc.videostatus.network.NetworkConnection;
import com.fourarc.videostatus.network.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MoreAppActivity extends AppCompatActivity {
    ArrayList<MoreApp> arrayList_jobs;
    ListView more_ap;

    NetworkConnection nw;
    ProgressDialog prgDialog;
    Boolean netConnection = false;
    Boolean noData = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_app);
        nw = new NetworkConnection(MoreAppActivity.this);
        prgDialog = new ProgressDialog(MoreAppActivity.this);
        prgDialog.setCancelable(false);
        more_ap = (ListView)findViewById(R.id.more_ap);
        more_ap.setDivider(null);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("More Apps");

        new GetStatusOperation1().execute();
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


    private class GetStatusOperation1 extends AsyncTask<String, Void, Void> {

        String response = null;
        String status, responseMsg = "Something went wrong..!!";

        @Override
        protected void onPreExecute() {

            prgDialog.setMessage("Please wait...");
            prgDialog.show();

            arrayList_jobs = new ArrayList();

        }

        @Override
        protected Void doInBackground(String... urls) {

            if (nw.isOnline() == true) {
                JSONObject json_main = new JSONObject();

                try {
                    json_main.put("c_key", Constant_store.c_Key1);
                    json_main.put("c_secret", Constant_store.c_Secret1);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(Constant_store.API_URL1 + "getmoreapp.php", ServiceHandler.POST, json_main);
                    Log.d("response", response);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsmain = js.getJSONObject("response");
                    status = jsmain.getString("type");

                    if (status.contains("true")) {

                        JSONObject jsonObject = jsmain.getJSONObject("responseData");

                        JSONArray jsonArray = jsonObject.optJSONArray("ads");

                        if (jsonArray.length() == 0) {
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jFinal = jsonArray.getJSONObject(i);

                                MoreApp adModel=new MoreApp(jFinal.getString("ad_title"),jFinal.getString("ad_image"),jFinal.getString("ad_url"),jFinal.getString("ad_desc"),jFinal.getString("ad_size"));
                                arrayList_jobs.add(adModel);
                            }
                        }

                    } else {
                        responseMsg = jsmain.getString("responseMsg");
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
            if(prgDialog!=null&&prgDialog.isShowing()) {
                prgDialog.dismiss();
            }

            if (netConnection == false) {

                Toast.makeText(getApplicationContext(),"Internet is not available. Please turn on and try again.",Toast.LENGTH_SHORT).show();
            } else {
                if (noData) {

                    Toast.makeText(getApplicationContext(),responseMsg,Toast.LENGTH_SHORT).show();

                } else {
                    if (status.equalsIgnoreCase("true")) {

                        MoreAppAdapter jobAdapter = new MoreAppAdapter(MoreAppActivity.this,arrayList_jobs);
                        more_ap.setAdapter(jobAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(),responseMsg,Toast.LENGTH_SHORT).show();
                    }
                }
            }
            super.onPostExecute(result);
        }
    }

}
