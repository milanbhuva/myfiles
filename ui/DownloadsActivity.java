package com.fourarc.videostatus.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.adapter.VideoAdapter;
import com.fourarc.videostatus.config.VideoPojo;
import com.fourarc.videostatus.entity.Category;
import com.fourarc.videostatus.manager.DownloadStorage;
import com.fourarc.videostatus.manager.PrefManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.peekandpop.shalskar.peekandpop.PeekAndPop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadsActivity extends AppCompatActivity {

    private String query;

    private Integer page = 0;
    private String language = "0";
    private Boolean loaded=false;

    private SwipeRefreshLayout swipe_refreshl_image_downloads;
    private RecyclerView recycler_view_image_downloads;
    private List<VideoPojo> VideoList =new ArrayList<>();
    private List<Category> categoryList =new ArrayList<>();
    private VideoAdapter videoAdapter;
    private LinearLayoutManager linearLayoutManager;
    private PrefManager prefManager;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_page_error;
    private Button button_try_again;
    private ImageView imageView_empty_favorite;
    private PeekAndPop peekAndPop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras() ;
        this.query =  bundle.getString("query");
        this.prefManager= new PrefManager(getApplicationContext());
        this.language=prefManager.getString("LANGUAGE_DEFAULT");

        setContentView(R.layout.activity_downloads);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(query);
        initView();
        //loadImage();
        showAdsBanner();
        initAction();

    }
    private void showAdsBanner() {
        if (prefManager.getString("SUBSCRIBED").equals("FALSE")) {
            final AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
        }

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
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void initView(){
        this.imageView_empty_favorite=(ImageView) findViewById(R.id.imageView_empty_favorite);
        this.relative_layout_load_more=(RelativeLayout) findViewById(R.id.relative_layout_load_more);
        this.button_try_again =(Button) findViewById(R.id.button_try_again);
        this.swipe_refreshl_image_downloads=(SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_image_downloads);
        this.linear_layout_page_error=(LinearLayout) findViewById(R.id.linear_layout_page_error);
        this.recycler_view_image_downloads=(RecyclerView) findViewById(R.id.recycler_view_image_downloads);
        this.linearLayoutManager=  new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        this.peekAndPop = new PeekAndPop.Builder(this)
                .parentViewGroupToDisallowTouchEvents(recycler_view_image_downloads)
                .peekLayout(R.layout.dialog_view)
                .build();
        videoAdapter =new VideoAdapter(VideoList,null,this,peekAndPop,false,true);
        recycler_view_image_downloads.setHasFixedSize(true);
        recycler_view_image_downloads.setAdapter(videoAdapter);
        recycler_view_image_downloads.setLayoutManager(linearLayoutManager);


    }
    public void initAction(){
        swipe_refreshl_image_downloads.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VideoList.clear();
                page = 0;
                loading=true;
              loadImage();
            }
        });
    }
    public void loadImage(){

        swipe_refreshl_image_downloads.setRefreshing(true);
        final DownloadStorage downloadStorage= new DownloadStorage(getApplicationContext());
        List<VideoPojo> statuses = downloadStorage.loadImagesFavorites();

        if (statuses==null){
            statuses= new ArrayList<>();
        }
        VideoList.clear();
        for (int i=0;i<statuses.size();i++){
            File file = new File(statuses.get(i).getLocal());
            if (file.exists()) {
               VideoPojo a = new VideoPojo();
                a = statuses.get(i);
                VideoList.add(a);
            }

        }
        ArrayList<VideoPojo> new_downloads= new ArrayList<VideoPojo>();
        for (int i = 0; i < VideoList.size(); i++) {
            new_downloads.add(VideoList.get(i));
        }
        downloadStorage.storeImage(new_downloads);
        if (VideoList.size()!=0){

            videoAdapter.notifyDataSetChanged();
            imageView_empty_favorite.setVisibility(View.GONE);
            recycler_view_image_downloads.setVisibility(View.VISIBLE);
        }else{
            imageView_empty_favorite.setVisibility(View.VISIBLE);
            recycler_view_image_downloads.setVisibility(View.GONE);
        }

        swipe_refreshl_image_downloads.setRefreshing(false);

     }
}


