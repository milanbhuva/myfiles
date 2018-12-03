package com.fourarc.videostatus.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.leo.simplearcloader.SimpleArcLoader;
import com.fourarc.videostatus.R;
import com.fourarc.videostatus.adapter.LanguageAdapter;
import com.fourarc.videostatus.adapter.SelectableViewHolder;
import com.fourarc.videostatus.api.apiClient;
import com.fourarc.videostatus.api.apiRest;
import com.fourarc.videostatus.entity.Language;
import com.fourarc.videostatus.manager.LanguageStorage;
import com.fourarc.videostatus.manager.PrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LanguageActivity extends AppCompatActivity implements SelectableViewHolder.OnItemSelectedListener {

    private LanguageAdapter languageAdapter;
    private final List<Language> languageList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private SimpleArcLoader simple_arc_loader_lang;
    private LinearLayout linear_layout_page_error;
    private Toolbar toolbar;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager= new PrefManager(getApplicationContext());

        setContentView(R.layout.activity_language);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.select_language));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        loadLang();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.language_save:


                String s = "";
                for (int i = 0; i < languageAdapter.getSelectedItems().size(); i++) {
                    if (i == 0) {
                        s += +languageAdapter.getSelectedItems().get(i).getId();

                    } else {
                        s += "," + languageAdapter.getSelectedItems().get(i).getId();

                    }
                    for (int j = 0; j < languageList.size(); j++) {
                        if (languageList.get(j).getId().equals(languageAdapter.getSelectedItems().get(i).getId())) {
                            languageList.get(j).setSelected(true);
                        }
                    }
                }
                languageAdapter.notifyDataSetChanged();
                prefManager.setString("LANGUAGE_DEFAULT", s);

                final LanguageStorage storageFavorites = new LanguageStorage(getApplicationContext());
                ArrayList<Language> storage = new ArrayList<Language>();
                for (int j = 0; j < languageList.size(); j++) {
                    storage.add(languageList.get(j));
                }
                storageFavorites.StoreLang(storage);

                Intent intent_save = new Intent(LanguageActivity.this, MainActivity.class);
                startActivity(intent_save);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        this.linear_layout_page_error=(LinearLayout) findViewById(R.id.linear_layout_page_error);
        this.simple_arc_loader_lang=(SimpleArcLoader) findViewById(R.id.simple_arc_loader_lang);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.selection_list);

    }

    @Override
    public void onItemSelected(Language item) {

    }

    public void loadLang(){
        recyclerView.setVisibility(View.GONE);
        simple_arc_loader_lang.setVisibility(View.VISIBLE);
        linear_layout_page_error.setVisibility(View.GONE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Language>> call = service.languageAll();
        call.enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, final Response<List<Language>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i <response.body().size() ; i++) {
                        languageList.add(response.body().get(i));
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    simple_arc_loader_lang.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);



                    if (response.isSuccessful()) {
                        languageList.clear();
                        String s = prefManager.getString("LANGUAGE_DEFAULT");
                        String[] array = s.split(",");
                        for (int i = 0; i < response.body().size(); i++) {
                            languageList.add(response.body().get(i));
                            if (s.length() != 0) {
                                for (int j = 0; j < array.length; j++) {
                                    if (languageList.get(i).getId() == Integer.parseInt(array[j])) {
                                        languageList.get(i).setSelected(true);
                                    }
                                }
                            }
                        }
                    }

                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    languageAdapter = new LanguageAdapter(LanguageActivity.this, languageList, true, LanguageActivity.this);
                    recyclerView.setAdapter(languageAdapter);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    simple_arc_loader_lang.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                simple_arc_loader_lang.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
            }
        });
    }
}
