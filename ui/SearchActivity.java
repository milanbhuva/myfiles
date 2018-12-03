package com.fourarc.videostatus.ui;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.fourarc.videostatus.manager.PrefManager;
import com.fourarc.videostatus.network.Constant_store;
import com.fourarc.videostatus.network.NetworkConnection;
import com.fourarc.videostatus.network.ServiceHandler;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.peekandpop.shalskar.peekandpop.PeekAndPop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    private String query;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;
    private Integer page = 0;
    private String language = "0";
    private Boolean loaded=false;

    private SwipeRefreshLayout swipe_refreshl_image_search;
    private RecyclerView recycler_view_image_search;
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
    int last;
    ArrayList<VideoPojo> arrayList_local = new ArrayList<>();
    private boolean userScrolled = true, first_call = true;
    private Integer item = 0 ;
    private Integer lines_beetween_ads = 8 ;
    private Boolean native_ads_enabled = false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras() ;
        this.query =  bundle.getString("query");
        this.prefManager= new PrefManager(getApplicationContext());
        this.language=prefManager.getString("LANGUAGE_DEFAULT");

        setContentView(R.layout.activity_search);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(query);
        initView();
       // loadVideo();
        new Getsearchedvideos().execute();
        nw = new NetworkConnection(getApplicationContext());
        prgDialog = new ProgressDialog(getApplicationContext());
        prgDialog.setCancelable(false);
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
        if (getResources().getString(R.string.FACEBOOK_ADS_ENABLED_NATIVE).equals("true")){
            native_ads_enabled=true;
            lines_beetween_ads=Integer.parseInt(getResources().getString(R.string.FACEBOOK_ADS_ITEM_BETWWEN_ADS));
        }
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled=false;
        }
        this.imageView_empty_favorite=(ImageView) findViewById(R.id.imageView_empty_favorite);
        this.relative_layout_load_more=(RelativeLayout) findViewById(R.id.relative_layout_load_more);
        this.button_try_again =(Button) findViewById(R.id.button_try_again);
        this.swipe_refreshl_image_search=(SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_image_search);
        this.linear_layout_page_error=(LinearLayout) findViewById(R.id.linear_layout_page_error);
        this.recycler_view_image_search=(RecyclerView) findViewById(R.id.recycler_view_image_search);
        this.linearLayoutManager=  new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        this.peekAndPop = new PeekAndPop.Builder(this)
                .parentViewGroupToDisallowTouchEvents(recycler_view_image_search)
                .peekLayout(R.layout.dialog_view)
                .build();
        videoAdapter =new VideoAdapter(VideoList,null,this,peekAndPop,true);
        recycler_view_image_search.setHasFixedSize(true);
        recycler_view_image_search.setAdapter(videoAdapter);
        recycler_view_image_search.setLayoutManager(linearLayoutManager);
        recycler_view_image_search.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount    = linearLayoutManager.getChildCount();
                    totalItemCount      = linearLayoutManager.getItemCount();
                    pastVisiblesItems   = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            //loadNextVideo();
                            new Getsearchedvideos().execute();
                        }
                    }
                }else{

                }
            }
        });

    }
    public void initAction(){
        swipe_refreshl_image_search.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VideoList.clear();
                page = 0;
                item = 0;
                loading=true;
                new Getsearchedvideos().execute();
               // loadVideo();
            }
        });
    }
    private class Getsearchedvideos extends AsyncTask<String, Void, Void> {

        String response = null;
        String status, responseMsg = "Something went wrong..!!";


        @Override
        protected void onPreExecute() {
            VideoList = new ArrayList<>();

            if (first_call) {
                arrayList_local.clear();
            }
        }

        @Override
        protected Void doInBackground(String... urls) {

            if (nw.isOnline() == true) {
                JSONObject json_main = new JSONObject();

                try {
                    json_main.put("c_key", Constant_store.c_Key);
                    json_main.put("c_secret", Constant_store.c_Secret);
                    json_main.put("search_keyword", query);
                    json_main.put("user_id", "27");
                    /*json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));*/


                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(Constant_store.API_URL + "get_video.php", ServiceHandler.POST, json_main);
                    Log.e("response", response);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsmain = js.getJSONObject("response");
                    status = jsmain.getString("type");

                    if (status.contains("true")) {

                        JSONArray videoarray = jsmain.getJSONObject("responseData").getJSONArray("Video");
                        JSONObject jsonObject = jsmain.getJSONObject("responseData");
                        last = jsonObject.getInt("last");
                        if (videoarray.length() == 0) {

                        } else {
                            for (int i = 0; i < videoarray.length(); i++) {

                                JSONObject jFinal = videoarray.getJSONObject(i);
                                VideoPojo status = new VideoPojo(jFinal.getString("status_id"), jFinal.getString("status_title"), jFinal.getString("user_id"), jFinal.getString("user_name"), jFinal.getString("user_profile_picture"), jFinal.getString("status_view"), jFinal.getString("status_review"), jFinal.getString("status_image"), jFinal.getString("status_data"), jFinal.getString("status_isfavourite"), jFinal.getString("status_comments"), jFinal.getString("status_love"), jFinal.getString("status_angry"), jFinal.getString("status_sad"), jFinal.getString("status_haha"), jFinal.getString("status_wow"), jFinal.getString("status_like"));
                                VideoList.add(status);

                            }
                            arrayList_local.addAll(VideoList);

                        }

                    } else {
                        responseMsg = jsmain.getString("asdfdsdfsd");
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

            if (netConnection == false) {

                linear_layout_page_error.setVisibility(View.VISIBLE);
                relative_layout_load_more.setVisibility(View.GONE);
            } else {


                if (status.equalsIgnoreCase("true")) {


                    recycler_view_image_search.setVisibility(View.VISIBLE);
                    //linear_layout_load_popular_fragment.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);
                   // videoAdapter = new VideoAdapter(arrayList_local, null,this, peekAndPop, true);
                    videoAdapter =new VideoAdapter(arrayList_local,null,SearchActivity.this,peekAndPop,true);

                    recycler_view_image_search.setHasFixedSize(true);
                    recycler_view_image_search.setAdapter(videoAdapter);
                    recycler_view_image_search.setLayoutManager(linearLayoutManager);
                    videoAdapter.notifyDataSetChanged();
                } else {
                    relative_layout_load_more.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }

            }
            super.onPostExecute(result);
        }
    }

/*
    public void loadNextVideo(){
        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<VideoPojo>> call = service.getvideo(Constant_store.c_Key,Constant_store.c_Secret);
        call.enqueue(new Callback<List<VideoPojo>>() {
            @Override
            public void onResponse(Call<List<VideoPojo>> call, Response<List<VideoPojo>> response) {
                //apiClient.FormatData(SearchActivity.this,response);

                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        for (int i=0;i<response.body().size();i++){
                            VideoList.add(response.body().get(i));
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;
                                    VideoList.add(new VideoPojo().setViewType(3));
                                }
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
                        page++;
                        loading=true;

                    }else {

                    }
                    relative_layout_load_more.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<VideoPojo>> call, Throwable t) {
                relative_layout_load_more.setVisibility(View.GONE);
            }
        });
    }
*/
/*
    public void loadVideo(){
        imageView_empty_favorite.setVisibility(View.GONE);
        swipe_refreshl_image_search.setRefreshing(true);
        linear_layout_page_error.setVisibility(View.GONE);
        recycler_view_image_search.setVisibility(View.GONE);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<VideoPojo>> call = service.getvideo(Constant_store.c_Key,Constant_store.c_Secret);
        call.enqueue(new Callback<List<VideoPojo>>() {
            @Override
            public void onResponse(Call<List<VideoPojo>> call, Response<List<VideoPojo>> response) {
                swipe_refreshl_image_search.setRefreshing(false);
                //apiClient.FormatData(SearchActivity.this,response);
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        VideoList.clear();
                        for (int i=0;i<response.body().size();i++){
                            VideoList.add(response.body().get(i));
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;
                                    VideoList.add(new VideoPojo().setViewType(3));
                                }
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
                        page++;
                        loaded=true;
                        imageView_empty_favorite.setVisibility(View.GONE);
                        recycler_view_image_search.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }else {
                        imageView_empty_favorite.setVisibility(View.VISIBLE);
                        recycler_view_image_search.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }


                }else{
                    imageView_empty_favorite.setVisibility(View.GONE);
                    recycler_view_image_search.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<VideoPojo>> call, Throwable t) {
                swipe_refreshl_image_search.setRefreshing(false);
                recycler_view_image_search.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                imageView_empty_favorite.setVisibility(View.GONE);

            }
        });
    }
*/
}
