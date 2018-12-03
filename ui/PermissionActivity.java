package com.fourarc.videostatus.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.manager.PrefManager;

public class PermissionActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int REQUEST_WRITE_PERMISSION = 786;
    private CardView card_view_allow_permission;
    private PrefManager prefManager;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION : {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (!prefManager.getString("first_lang_set").equals("true")){
                        prefManager.setString("first_lang_set","true");
                        Intent intent_status  =  new Intent(getApplicationContext(), LanguageActivity.class);
                        startActivity(intent_status);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }else {
                        Intent intent_status  =  new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent_status);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }

                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        prefManager= new PrefManager(getApplicationContext());

        this.card_view_allow_permission=(CardView) findViewById(R.id.card_view_allow_permission);
        this.card_view_allow_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(PermissionActivity.this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_WRITE_PERMISSION);
        }
    }



}
