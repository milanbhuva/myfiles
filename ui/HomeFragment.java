package com.fourarc.videostatus.ui.fragement;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.fourarc.videostatus.R;
import com.fourarc.videostatus.adapter.CategoryVideoAdapter;
import com.fourarc.videostatus.api.apiClient;
import com.fourarc.videostatus.api.apiRest;
import com.fourarc.videostatus.config.CategoryPojo;
import com.fourarc.videostatus.config.VideoPojo;
import com.fourarc.videostatus.manager.DownloadStorage;
import com.fourarc.videostatus.manager.FavoritesStorage;
import com.fourarc.videostatus.manager.PrefManager;
import com.fourarc.videostatus.network.Constant_store;
import com.fourarc.videostatus.network.NetworkConnection;
import com.fourarc.videostatus.network.ServiceHandler;
import com.fourarc.videostatus.network.Utility;
import com.fourarc.videostatus.ui.VideoActivity;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;
import com.peekandpop.shalskar.peekandpop.PeekAndPop;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public static ArrayList<VideoPojo> arrayList_lates = new ArrayList<>();
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;
    int page_no = 1;
    int last_page;
    String Videotitle;
    int last;
    YouTubePlayerView youTubePlayerView;
    ArrayList<VideoPojo> arrayList_local = new ArrayList<>();
    LinearLayoutManager mLayoutManager_see_all;
    String status_Id;
    private Integer page = 0;
    private View view;
    private PrefManager prefManager;
    private String language = "0";
    private boolean loaded = false;
    private RelativeLayout relative_layout_load_more;
    private Button button_try_again;
    private SwipeRefreshLayout swipe_refreshl_status_fragment;
    private LinearLayout linear_layout_page_error;
    private LinearLayout linear_layout_load_status_fragment;
    private RecyclerView recycler_view_status_fragment;
    private LinearLayoutManager linearLayoutManager;
    private boolean userScrolled = true, first_call = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private List<VideoPojo> VideoList = new ArrayList<>();
    private List<CategoryPojo> categoryList = new ArrayList<>();
    private VideoAdapter videoAdapter;
    private PeekAndPop peekAndPop;
    private Integer item = 0;
    private Integer lines_beetween_ads = 8;
    private Boolean native_ads_enabled = false;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        nw = new NetworkConnection(getActivity());
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setCancelable(false);

        this.view = inflater.inflate(R.layout.fragment_home, container, false);
        this.prefManager = new PrefManager(getActivity().getApplicationContext());
        new Getcategory().execute();
        new GetStatusOperation2().execute();
        this.language = prefManager.getString("LANGUAGE_DEFAULT");
        this.recycler_view_status_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_status_fragment);

        mLayoutManager_see_all = new LinearLayoutManager(getActivity());
        recycler_view_status_fragment.setLayoutManager(mLayoutManager_see_all);
        recycler_view_status_fragment.setItemAnimator(new DefaultItemAnimator());




        recycler_view_status_fragment.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                userScrolled = true;
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {

                    visibleItemCount = mLayoutManager_see_all.getChildCount();
                    totalItemCount = mLayoutManager_see_all.getItemCount();
                    pastVisiblesItems = mLayoutManager_see_all.findLastVisibleItemPosition();

                        if (mLayoutManager_see_all.findLastCompletelyVisibleItemPosition() == Constant_store.arrayList_local.size() - 1) {
                            userScrolled = false;
                            first_call = false;
                            page_no = page_no + 1;

                            new GetStatusOperation3().execute();
                            if (page_no <= last_page) {
                            }
                        }

                }   if (dy < 0) {
                }
            }
        });

        initView();
        initAction();


        /*loadData();*/
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


    }

    private void initAction() {
        this.swipe_refreshl_status_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                /*VideoList.clear();*/

              /*  item = 0;
                page = 0;
                loading = true;
                new GetStatusOperation2().execute();*/

                page_no = 1;
                new  GetStatusOperation2().execute();
                swipe_refreshl_status_fragment.setRefreshing(false);
                /*loadData();*/
            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryList.clear();
                /*VideoList.clear();*/
                videoAdapter.notifyDataSetChanged();
                item = 0;
                page = 0;
                loading = true;
                /*loadData();*/
                new GetStatusOperation2().execute();
            }
        });
    }

    private void initView() {
        if (getResources().getString(R.string.FACEBOOK_ADS_ENABLED_NATIVE).equals("true")) {
            native_ads_enabled = true;
            lines_beetween_ads = Integer.parseInt(getResources().getString(R.string.FACEBOOK_ADS_ITEM_BETWWEN_ADS));
        }
        PrefManager prefManager = new PrefManager(getActivity().getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled = false;
        }
        this.relative_layout_load_more = (RelativeLayout) view.findViewById(R.id.relative_layout_load_more);
        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);
        this.swipe_refreshl_status_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_status_fragment);
        this.linear_layout_page_error = (LinearLayout) view.findViewById(R.id.linear_layout_page_error);
        this.linear_layout_load_status_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_status_fragment);

        /*this.linearLayoutManager=  new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);*/
        this.swipe_refreshl_status_fragment.setProgressViewOffset(false, 0, 300);
        this.peekAndPop = new PeekAndPop.Builder(getActivity()).
                parentViewGroupToDisallowTouchEvents(recycler_view_status_fragment).peekLayout(R.layout.dialog_view).build();
        youTubePlayerView = (YouTubePlayerView) peekAndPop.getPeekView().findViewById(R.id.video_view_dialog_image);

        videoAdapter = new VideoAdapter(arrayList_lates, categoryList, getActivity(), peekAndPop);
        recycler_view_status_fragment.setHasFixedSize(true);
        recycler_view_status_fragment.setAdapter(videoAdapter);
        //   trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

/*
        recycler_view_status_fragment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                userScrolled = true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager_see_all.getChildCount();
                    totalItemCount = mLayoutManager_see_all.getItemCount();
                    pastVisiblesItems = mLayoutManager_see_all.findLastVisibleItemPosition();

                    if (mLayoutManager_see_all.findLastCompletelyVisibleItemPosition() == arrayList_local.size() - 1) {
                        userScrolled = false;

                        page_no = page_no + 1;

                        if (first_call) {
                            first_call = false;
                            new GetStatusOperation2().execute();
                            swipe_refreshl_status_fragment.setRefreshing(false);

                        }
                        if (page_no <= last_page) {
                            new GetStatusOperation2().execute();
                            swipe_refreshl_status_fragment.setRefreshing(false);
                        }
                    }
                }
            }
        });
*/

    }

    /*
    public void loadData(){
        recycler_view_status_fragment.setVisibility(View.GONE);
        linear_layout_load_status_fragment.setVisibility(View.VISIBLE);
        linear_layout_page_error.setVisibility(View.GONE);
        swipe_refreshl_status_fragment.setRefreshing(true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Category>> call = service.categoriesImageAll();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                swipe_refreshl_status_fragment.setRefreshing(false);
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        categoryList.clear();
                        for (int i=0;i<response.body().size();i++){
                            if (i<10){
                                categoryList.add(response.body().get(i));
                            }else {
                                categoryList.add(null);
                                break;
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
                    }else {

                    }


                }else{
                    recycler_view_status_fragment.setVisibility(View.GONE);
                    linear_layout_load_status_fragment.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);

                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                recycler_view_status_fragment.setVisibility(View.GONE);
                linear_layout_load_status_fragment.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_status_fragment.setRefreshing(false);


            }
        });
    }
*/
    public void loadStatus() {
        swipe_refreshl_status_fragment.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<VideoPojo>> call = service.getvideo(Constant_store.c_Key, Constant_store.c_Secret);
        call.enqueue(new Callback<List<VideoPojo>>() {
            @Override
            public void onResponse(Call<List<VideoPojo>> call, Response<List<VideoPojo>> response) {
                //apiClient.FormatData(getActivity(),response);
                swipe_refreshl_status_fragment.setRefreshing(false);
                VideoList.clear();
               /* VideoList.add(new Video().setViewType(0));
                VideoList.add(new Video().setViewType(1));*/
                if (response.isSuccessful()) {
                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            VideoList.add(response.body().get(i));
                            if (native_ads_enabled) {
                                item++;
                                if (item == lines_beetween_ads) {
                                    item = 0;
                                    /*VideoList.add(new Video().setViewType(3));*/
                                }
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
                        page++;
                        loaded = true;
                    } else {

                    }
                    recycler_view_status_fragment.setVisibility(View.VISIBLE);
                    linear_layout_load_status_fragment.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);

                } else {
                    recycler_view_status_fragment.setVisibility(View.GONE);
                    linear_layout_load_status_fragment.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<VideoPojo>> call, Throwable t) {
                recycler_view_status_fragment.setVisibility(View.GONE);
                linear_layout_load_status_fragment.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_status_fragment.setRefreshing(false);
            }


        });
    }

    public void loadNextStatus() {
        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<VideoPojo>> call = service.getvideo(Constant_store.c_Key, Constant_store.c_Secret);
        call.enqueue(new Callback<List<VideoPojo>>() {
            @Override
            public void onResponse(Call<List<VideoPojo>> call, Response<List<VideoPojo>> response) {
                if (response.isSuccessful()) {

                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            VideoList.add(response.body().get(i));
                            if (native_ads_enabled) {
                                item++;
                                if (item == lines_beetween_ads) {
                                    item = 0;
                                    /*VideoList.add(new Video().setViewType(3));*/
                                }
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
                        page++;
                        loading = true;

                    } else {

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

    private class FavouriteOperation extends AsyncTask<String, Void, Void> {
        String response;
        String status, msg;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... urls) {

            if (nw.isOnline() == true) {
                JSONObject json_main = new JSONObject();

                try {
                    json_main.put("c_key", Constant_store.c_Key);
                    json_main.put("c_secret", Constant_store.c_Secret);
                    json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));
                    json_main.put("status_id", status_Id);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(Constant_store.API_URL + "video_favourite.php", ServiceHandler.POST, json_main);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsMain = js.getJSONObject("response");
                    status = jsMain.getString("type");
                    msg = jsMain.getString("responseMsg");

                    if (status.equals("false")) {
                    } else {
                        JSONObject jsFinal = jsMain.getJSONObject("responseData");
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
                Snackbar snackbar = Snackbar.make(button_try_again, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();

            } else {

                if ("true".equalsIgnoreCase(status)) {
                    Snackbar snackbar = Snackbar.make(button_try_again, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();

                } else {
                    Snackbar snackbar = Snackbar.make(button_try_again, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                }
            }
            super.onPostExecute(result);
        }
    }

    private class Getcategory extends AsyncTask<String, Void, Void> {

        String response = null;
        String status, responseMsg = "Something went wrong..!!";


        @Override
        protected void onPreExecute() {
            categoryList = new ArrayList<>();
        }

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

                        JSONArray categoryArray = jsmain.getJSONObject("responseData").getJSONArray("tagDetail");
                        JSONObject jsonObject = jsmain.getJSONObject("responseData");

                        if (categoryArray.length() == 0) {

                        } else {
                            for (int i = 0; i < categoryArray.length(); i++) {

                                JSONObject jFinal = categoryArray.getJSONObject(i);
                                CategoryPojo statusPojo = new CategoryPojo(jFinal.getString("tag_id"), jFinal.getString("tag_name"), jFinal.getString("tag_image"));
                                categoryList.add(statusPojo);
                            }

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
        protected void onPostExecute(Void result) {

            if (netConnection == false) {
                relative_layout_load_more.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);


            } else {
                if (noData) {
                    relative_layout_load_more.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                } else {

                    if (status.equalsIgnoreCase("true")) {

                        //loadStatus();
                        linear_layout_page_error.setVisibility(View.GONE);
                        relative_layout_load_more.setVisibility(View.GONE);
                        videoAdapter = new VideoAdapter(arrayList_local, categoryList, getActivity(), peekAndPop);
                        recycler_view_status_fragment.setHasFixedSize(true);
                        recycler_view_status_fragment.setAdapter(videoAdapter);

                    } else {
                        relative_layout_load_more.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.VISIBLE);
                    }
                }
            }
            super.onPostExecute(result);
        }
    }

    private class GetStatusOperation2 extends AsyncTask<String, Void, Void> {

        String response = null;
        String status, responseMsg = "Something went wrong..!!";

        ArrayList<VideoPojo> arrayList_local = new ArrayList<>();

        @Override
        protected void onPreExecute() {

            arrayList_lates = new ArrayList<>();
            arrayList_lates.clear();
            Constant_store.arrayList_local = new ArrayList<>();
            Constant_store.arrayList_local.clear();

        }

        @Override
        protected Void doInBackground(String... urls) {

            if (nw.isOnline() == true) {
                JSONObject json_main = new JSONObject();

                try {
                    json_main.put("c_key", Constant_store.c_Key);
                    json_main.put("c_secret", Constant_store.c_Secret);
                    json_main.put("page", page_no);
                    json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));
                    //json_main.put("user_id", "27");

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
                                arrayList_lates.add(status);

                            }
                            Constant_store.arrayList_local.addAll(arrayList_lates);

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


                    recycler_view_status_fragment.setVisibility(View.VISIBLE);
                    linear_layout_load_status_fragment.setVisibility(View.GONE);
                    relative_layout_load_more.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);
                    swipe_refreshl_status_fragment.setRefreshing(false);

                    videoAdapter = new VideoAdapter(arrayList_lates, categoryList, getActivity(), peekAndPop);
                    recycler_view_status_fragment.setHasFixedSize(true);
                    recycler_view_status_fragment.setAdapter(videoAdapter);
                    /*videoAdapter.notifyDataSetChanged();*/


                } else {

                }

            }
            super.onPostExecute(result);
        }
    }

    private class GetStatusOperation3 extends AsyncTask<String, Void, Void> {

        String response = null;
        String status, responseMsg = "Something went wrong..!!";

        ArrayList<VideoPojo> arrayList_local = new ArrayList<>();

        @Override
        protected void onPreExecute() {

            VideoList = new ArrayList<>();
            Constant_store.arrayList_local = new ArrayList<>();
            Constant_store.arrayList_local.clear();

        }

        @Override
        protected Void doInBackground(String... urls) {

            if (nw.isOnline() == true) {
                JSONObject json_main = new JSONObject();

                try {
                    json_main.put("c_key", Constant_store.c_Key);
                    json_main.put("c_secret", Constant_store.c_Secret);
                    json_main.put("page", page_no);
                    json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));
                   // json_main.put("user_id", "27");

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
                                arrayList_lates.add(status);

                            }
                            Constant_store.arrayList_local.addAll(arrayList_lates);

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


                    recycler_view_status_fragment.setVisibility(View.VISIBLE);
                    linear_layout_load_status_fragment.setVisibility(View.GONE);
                    relative_layout_load_more.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);


                    videoAdapter.notifyDataSetChanged();


                } else {

                }

            }
            super.onPostExecute(result);
        }
    }

    public class VideoAdapter extends RecyclerView.Adapter {
        private static final String WHATSAPP_ID = "com.whatsapp";
        private static final String FACEBOOK_ID = "com.facebook.katana";
        private static final String MESSENGER_ID = "com.facebook.orca";
        private static final String INSTAGRAM_ID = "com.instagram.android";
        private static final String SHARE_ID = "com.android.all";
        private final NavigableMap<Long, String> suffixes = new TreeMap<>();
        String video;
        String videotitle;
        String link;
      String downloadUrl;;
        YouTubePlayerView youTubePlayerView;
        private Boolean downloads = false;
        private Boolean favorites = false;
        private PeekAndPop peekAndPop;
        private List<VideoPojo> VideoList = new ArrayList<>();
        private List<CategoryPojo> categoryList = new ArrayList<>();
        private Activity activity;
        private InterstitialAd mInterstitialAd;
        private SimpleExoPlayerView simpleExoPlayerView;
        private SimpleExoPlayer player;
        private DefaultTrackSelector trackSelector;
        private boolean shouldAutoPlay;
        private BandwidthMeter bandwidthMeter;
        private DataSource.Factory mediaDataSourceFactory;
        private ImageView ivHideControllerButton;
        private Timeline.Window window;

        {
            suffixes.put(1_000L, "k");
            suffixes.put(1_000_000L, "M");
            suffixes.put(1_000_000_000L, "G");
            suffixes.put(1_000_000_000_000L, "T");
            suffixes.put(1_000_000_000_000_000L, "P");
            suffixes.put(1_000_000_000_000_000_000L, "E");
        }


        public VideoAdapter(final List<VideoPojo> VideoList, List<CategoryPojo> categoryList, final Activity activity, final PeekAndPop peekAndPop) {
            this.VideoList = VideoList;
            this.categoryList = categoryList;
            this.activity = activity;
            this.peekAndPop = peekAndPop;

            mInterstitialAd = new InterstitialAd(activity.getApplication());
            mInterstitialAd.setAdUnitId(activity.getString(R.string.ad_unit_id_interstitial));
            requestNewInterstitial();

            peekAndPop.addHoldAndReleaseView(R.id.like_button_fav_image_dialog);
            peekAndPop.addHoldAndReleaseView(R.id.like_button_messenger_image_dialog);
            peekAndPop.addHoldAndReleaseView(R.id.like_button_facebook_image_dialog);
            peekAndPop.addHoldAndReleaseView(R.id.like_button_instagram_image_dialog);
            peekAndPop.addHoldAndReleaseView(R.id.like_button_share_image_dialog);
            peekAndPop.addHoldAndReleaseView(R.id.like_button_whatsapp_image_dialog);

            peekAndPop.setOnHoldAndReleaseListener(new PeekAndPop.OnHoldAndReleaseListener() {


                @Override
                public void onHold(View view, int i) {
                    Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(40);
                }

                @Override
                public void onLeave(View view, int i) {

                }

                @Override
                public void onRelease(View view, final int position) {
                    final String videourl = VideoList.get(position).getStatusData();
                    final String ext = "mp4";
                    final DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL();
                    switch (view.getId()) {
                        case R.id.like_button_facebook_image_dialog:
                            if (mInterstitialAd.isLoaded()) {
                                if (check()) {
                                    mInterstitialAd.show();
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            requestNewInterstitial();
                                            if (!VideoList.get(position).isDownloading()) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, FACEBOOK_ID);
                                                else
                                                    downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, FACEBOOK_ID);
                                            }
                                        }
                                    });
                                } else {

                                    if (!VideoList.get(position).isDownloading()) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                            downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, FACEBOOK_ID);
                                        else
                                            downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, FACEBOOK_ID);
                                    }
                                }
                            } else {

                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, FACEBOOK_ID);
                                    else
                                        downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, FACEBOOK_ID);
                                }
                            }

                            break;
                        case R.id.like_button_messenger_image_dialog:


                            if (mInterstitialAd.isLoaded()) {
                                if (check()) {
                                    mInterstitialAd.show();
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            requestNewInterstitial();
                                            if (!VideoList.get(position).isDownloading()) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, MESSENGER_ID);
                                                else
                                                    downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, MESSENGER_ID);
                                            }
                                        }
                                    });
                                } else {
                                    if (!VideoList.get(position).isDownloading()) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                            downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, MESSENGER_ID);
                                        else
                                            downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, MESSENGER_ID);
                                    }
                                }
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, MESSENGER_ID);
                                    else
                                        downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, MESSENGER_ID);
                                }
                            }


                            break;
                        case R.id.like_button_whatsapp_image_dialog:


                            if (mInterstitialAd.isLoaded()) {
                                if (check()) {
                                    mInterstitialAd.show();
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            requestNewInterstitial();
                                            if (!VideoList.get(position).isDownloading()) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, WHATSAPP_ID);
                                                else
                                                    downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, WHATSAPP_ID);
                                            }
                                        }
                                    });
                                } else {
                                    if (!VideoList.get(position).isDownloading()) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                            downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, WHATSAPP_ID);
                                        else
                                            downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, WHATSAPP_ID);
                                    }
                                }
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, WHATSAPP_ID);
                                    else
                                        downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, WHATSAPP_ID);
                                }
                            }


                            break;
                        case R.id.like_button_instagram_image_dialog:


                            if (mInterstitialAd.isLoaded()) {
                                if (check()) {
                                    mInterstitialAd.show();
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            requestNewInterstitial();
                                            if (!VideoList.get(position).isDownloading()) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, INSTAGRAM_ID);
                                                else
                                                    downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, INSTAGRAM_ID);
                                            }
                                        }
                                    });
                                } else {
                                    if (!VideoList.get(position).isDownloading()) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                            downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, INSTAGRAM_ID);
                                        else
                                            downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, INSTAGRAM_ID);
                                    }
                                }
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, INSTAGRAM_ID);
                                    else
                                        downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, INSTAGRAM_ID);
                                }
                            }


                            break;
                        case R.id.like_button_share_image_dialog:


                            if (mInterstitialAd.isLoaded()) {
                                if (check()) {
                                    mInterstitialAd.show();
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            requestNewInterstitial();
                                            if (!VideoList.get(position).isDownloading()) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, SHARE_ID);
                                                else
                                                    downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, SHARE_ID);
                                            }
                                        }
                                    });
                                } else {
                                    if (!VideoList.get(position).isDownloading()) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                            downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, SHARE_ID);
                                        else
                                            downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, SHARE_ID);
                                    }
                                }
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, SHARE_ID);
                                    else
                                        downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext, position, SHARE_ID);
                                }
                            }
                            break;
                        case R.id.like_button_fav_image_dialog:
                            final FavoritesStorage storageFavorites = new FavoritesStorage(activity.getApplicationContext());

                            List<VideoPojo> favorites_list = storageFavorites.loadImagesFavorites();
                            Boolean exist = false;
                            if (favorites_list == null) {
                                favorites_list = new ArrayList<>();
                            }
                            for (int i = 0; i < favorites_list.size(); i++) {
                                if (favorites_list.get(i).getStatusId() == (VideoList.get(position).getStatusId())) {
                                    exist = true;
                                }
                            }
                            if (exist == false) {
                                ArrayList<VideoPojo> audios = new ArrayList<VideoPojo>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    audios.add(favorites_list.get(i));
                                }
                                audios.add(VideoList.get(position));
                                storageFavorites.storeImage(audios);
                            } else {
                                ArrayList<VideoPojo> new_favorites = new ArrayList<VideoPojo>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    if (favorites_list.get(i).getStatusId() != (VideoList.get(position).getStatusId())) {
                                        new_favorites.add(favorites_list.get(i));

                                    }
                                }
                                if (favorites == true) {
                                    VideoList.remove(position);
                                    notifyDataSetChanged();
                                    //holder.ripple_view_wallpaper_item.setVisibility(View.GONE);
                                }
                                storageFavorites.storeImage(new_favorites);

                            }
                            notifyDataSetChanged();
                            break;

                    }
                }


            });
            peekAndPop.setOnGeneralActionListener(new PeekAndPop.OnGeneralActionListener() {
                @Override
                public void onPeek(View view, final int position) {

                    LikeButton like_button_fav_image_dialog = (LikeButton) peekAndPop.getPeekView().findViewById(R.id.like_button_fav_image_dialog);

                    final FavoritesStorage storageFavorites = new FavoritesStorage(activity.getApplicationContext());
                    List<VideoPojo> Videos = storageFavorites.loadImagesFavorites();
                    Boolean exist = false;
                    if (Videos == null) {
                        Videos = new ArrayList<>();
                    }
                    for (int i = 0; i < Videos.size(); i++) {
                        if (Videos.get(i).getStatusId() == (VideoList.get(position).getStatusId())) {
                            exist = true;
                        }
                    }
                    if (exist == false) {
                        like_button_fav_image_dialog.setLiked(false);
                    } else {
                        like_button_fav_image_dialog.setLiked(true);
                    }


                    ImageView circle_image_view_dialog_image = (ImageView) peekAndPop.getPeekView().findViewById(R.id.circle_image_view_dialog_image);
                    TextView text_view_view_dialog_user = (TextView) peekAndPop.getPeekView().findViewById(R.id.text_view_view_dialog_user);
                    TextView text_view_view_dialog_title = (TextView) peekAndPop.getPeekView().findViewById(R.id.text_view_view_dialog_title);

                    Picasso.with(activity.getApplicationContext()).load(VideoList.get(position).getUserProfilePicture()).error(R.drawable.profile).placeholder(R.drawable.profile).into(circle_image_view_dialog_image);
                    text_view_view_dialog_user.setText(VideoList.get(position).getUserName());
                    text_view_view_dialog_title.setText(VideoList.get(position).getStatusTitle());






                  /*  youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                        @Override
                        public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                            initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                                @Override
                                public void onReady() {
                                    DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();


                                    initializedYouTubePlayer.loadVideo( VideoList.get(position).getStatusData(),0);
                                }
                            });
                        }
                    }, true);

                    youTubePlayerView.setVisibility(View.VISIBLE);
                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    youTubePlayerView.getPlayerUIController().showYouTubeButton(false);
                    //youTubeView.addFullScreenListener((YouTubePlayerFullScreenListener) getActivity());
                    youTubePlayerView.getPlayerUIController().setVideoTitle(VideoList.get(position).getStatusTitle());
                    trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
*/

                    shouldAutoPlay = true;
                    bandwidthMeter = new DefaultBandwidthMeter();
                    mediaDataSourceFactory = new DefaultDataSourceFactory(activity.getApplicationContext(), Util.getUserAgent(activity.getApplication(), "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
                    window = new Timeline.Window();
                    ivHideControllerButton = (ImageView) peekAndPop.getPeekView().findViewById(R.id.exo_controller);
                    // initializePlayer(position);
                    youTubePlayerView = (YouTubePlayerView) peekAndPop.getPeekView().findViewById(R.id.video_view_dialog_image);
                    video = VideoList.get(position).getStatusData();
                    youTubePlayerView.requestFocus();
                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
                    youTubePlayerView.initialize(new YouTubePlayerInitListener() {

                        @Override
                        public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {

                            initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                                @Override
                                public void onReady() {
                                    DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                                    initializedYouTubePlayer.loadVideo(video, 0);
                                    YouTubeUriExtractor ytEx = new YouTubeUriExtractor(getActivity()) {
                                        @Override
                                        public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                                            if (ytFiles != null) {
                                                int itag = ytFiles.keyAt(2);
                                                YtFile ytFile = ytFiles.get(itag);

                                                downloadUrl = ytFile.getUrl();

                                            }
                                        }
                                    };
                                            String vlink=VideoList.get(position).getStatusData();
                                    ytEx.execute("https://www.youtube.com/watch?v="+vlink);

                                }
                            });
                        }


                    }, true);


                    youTubePlayerView.setVisibility(View.VISIBLE);
                   // TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    youTubePlayerView.getPlayerUIController().showYouTubeButton(false);
                    //youTubeView.addFullScreenListener((YouTubePlayerFullScreenListener) getActivity());
                    youTubePlayerView.getPlayerUIController().setVideoTitle(VideoList.get(position).getStatusTitle());





                }

                @Override
                public void onPop(View view, int i) {
                    try {
                    releasePlayer();

                         bandwidthMeter=null;
                         mediaDataSourceFactory=null;
                         window=null;
                    } catch (Exception e) {

                    }

                }


            });

        }
        private void releasePlayer() {


            //  youTubePlayerView.release();

            if (youTubePlayerView != null) {

               // youTubePlayerView.release();
                //youTubePlayerView.release();
                 youTubePlayerView = null;
                trackSelector = null;
            }
        }


        public VideoAdapter(final List<VideoPojo> VideoList, List<CategoryPojo> categoryList, final Activity activity, final PeekAndPop peekAndPop, Boolean favorites_) {
            this(VideoList, categoryList, activity, peekAndPop);
            this.favorites = favorites_;
        }

        public VideoAdapter(final List<VideoPojo> VideoList, List<CategoryPojo> categoryList, final Activity activity, final PeekAndPop peekAndPop, Boolean favorites_, Boolean downloads_) {
            this(VideoList, categoryList, activity, peekAndPop);
            this.favorites = favorites_;
            this.downloads = downloads_;
        }

        /*private void initializePlayer(Integer position) {

            simpleExoPlayerView.requestFocus();

            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);

            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);

            simpleExoPlayerView.setPlayer(player);

            player.setPlayWhenReady(shouldAutoPlay);
        MediaSource mediaSource = new HlsMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),
                mediaDataSourceFactory, mainHandler, null);

            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(VideoList.get(position).getStatusData()),
                    mediaDataSourceFactory, extractorsFactory, null, null);
            if (downloads){
//            Log.v("this is path",VideoList.get(position).getPath());
                Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(VideoList.get(position).getLocal()));
                mediaSource = new ExtractorMediaSource(imageUri,
                        mediaDataSourceFactory, extractorsFactory, null, null);
            }


            player.prepare(mediaSource);
            simpleExoPlayerView.hideController();

            ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    simpleExoPlayerView.hideController();
                }
            });
        }*/
       /* private void releasePlayer() {
            //  youTubePlayerView.release();

            if (youTubePlayerView != null) {

            youTubePlayerView.release();
               // youTubePlayerView = null;
                trackSelector = null;
            }
        }*/

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {

                case 0: {
                    View v0 = inflater.inflate(R.layout.item_empty, parent, false);
                    viewHolder = new EmptyHolder(v0);
                    break;
                }
                case 1: {
                    View v1 = inflater.inflate(R.layout.item_categories, parent, false);
                    viewHolder = new CategoriesHolder(v1);
                    break;
                }
                case 2: {
                    View v2 = inflater.inflate(R.layout.item_video, parent, false);
                    viewHolder = new VideoAdapter.ImageHolder(v2);
                    break;
                }
                case 3: {
                    View v3 = inflater.inflate(R.layout.item_facebook_ads, parent, false);
                    viewHolder = new FacebookNativeHolder(v3);
                    break;
                }
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder_parent, final int position) {
            switch (getItemViewType(position)) {
                case 0: {

                    break;
                }
                case 1: {
                    final CategoriesHolder holder = (VideoAdapter.CategoriesHolder) holder_parent;
                    break;
                }
                case 2: {

                    final VideoAdapter.ImageHolder holder = (VideoAdapter.ImageHolder) holder_parent;
                    final DownloadFileFromURL downloadFileFromURL = new VideoAdapter.DownloadFileFromURL();
                    peekAndPop.addLongClickView(holder.image_view_image_item_image, position);
/*                if (VideoList.get(position).getStatusReview()){
                    holder.relative_layout_item_image_review.setVisibility(View.VISIBLE);
                }else{
                    holder.relative_layout_item_image_review.setVisibility(View.GONE);
                }*/
                    if (downloads) {
                        holder.like_button_delete_image_item.setVisibility(View.VISIBLE);
                        holder.like_button_fav_image_item.setVisibility(View.GONE);

                    } else {
                        holder.like_button_delete_image_item.setVisibility(View.GONE);
                        holder.like_button_fav_image_item.setVisibility(View.VISIBLE);
                    }
                    Picasso.with(activity.getApplicationContext()).load(VideoList.get(position).getStatusImage()).error(R.drawable.bg_transparant).placeholder(R.drawable.bg_transparant).into(holder.image_view_image_item_image);
                    Picasso.with(activity.getApplicationContext()).load(VideoList.get(position).getUserProfilePicture()).error(R.drawable.profile).placeholder(R.drawable.profile).into(holder.circle_image_view_image_item_user);
                    if (!VideoList.get(position).isDownloading()) {
                        holder.relative_layout_progress_image_item.setVisibility(View.GONE);
                    } else {
                        holder.relative_layout_progress_image_item.setVisibility(View.VISIBLE);
                        holder.progress_bar_item_image.setProgress(VideoList.get(position).getProgress());
                        holder.text_view_progress_image_item.setText("Loading : " + VideoList.get(position).getProgress() + " %");
                        if (VideoList.get(position).getProgress() == 100) {
                            holder.image_view_cancel_image_item.setClickable(false);
                        } else {
                            holder.image_view_cancel_image_item.setClickable(true);

                        }
                    }
                    //holder.text_view_downloads.setText(format(VideoList.get(position).getStatusView()));
                    holder.text_view_image_item_name_user.setText(VideoList.get(position).getUserName());
                    holder.text_view_image_item_title.setText(VideoList.get(position).getStatusTitle());
                    holder.image_view_cancel_image_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            downloadFileFromURL.cancel(true);
                        }
                    });
                    holder.like_button_whatsapp_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(LikeButton likeButton) {



                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), "mp4", position, WHATSAPP_ID);
                            else
                                downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), "mp4", position, WHATSAPP_ID);

                            //downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), "mp4", position, MESSENGER_ID);

                          /*  File file = new File(getApplicationContext().getFilesDir()
                                    .getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/"
                                    + VideoList.get(position).getStatusTitle() + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                String download = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                String path = VideoList.get(position).getPath();
                                if (VideoList.get(position).getLocal() != null) {
                                    if (new File(VideoList.get(position).getLocal()).exists()) {
                                        path = VideoList.get(position).getLocal();
                                    }
                                }
                                Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext()+"com.fourarc.videostatus", new File(download));
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.setPackage(WHATSAPP_ID);


                                final String final_text = activity.getResources().getString(R.string.download_more_from_link);

                                shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                try {
                                    activity.startActivity(shareIntent);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT, true).show();
                                }
                            } else {
                               *//* String download = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                holder.relative_layout_progress_image_item.setVisibility(View.VISIBLE);
                                new DownloadFileFromURL1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, download);*//*
                                String download = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                String path = VideoList.get(position).getPath();
                                if (VideoList.get(position).getLocal() != null) {
                                    if (new File(VideoList.get(position).getLocal()).exists()) {
                                        path = VideoList.get(position).getLocal();
                                    }
                                }
                                Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext()+"com.fourarc.videostatus", new File(download));
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.setPackage(WHATSAPP_ID);


                                final String final_text = activity.getResources().getString(R.string.download_more_from_link);

                                shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                try {
                                    activity.startActivity(shareIntent);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT, true).show();
                                }

                            }
*/
                        }
                    });
                    holder.like_button_messenger_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(LikeButton likeButton) {
                            holder.like_button_messenger_image_item.setLiked(false);

                            if (mInterstitialAd.isLoaded()) {
                                if (check()) {
                                    mInterstitialAd.show();
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            requestNewInterstitial();
                                            if (!VideoList.get(position).isDownloading()) {
                                                String ext3 = "mp4";
                                                String url3 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext3, position, MESSENGER_ID);
                                                else
                                                    downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext3, position, MESSENGER_ID);
                                            }
                                        }
                                    });
                                } else {
                                    if (!VideoList.get(position).isDownloading()) {
                                        String ext4 = "mp4";
                                        String url4 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                            downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext4, position, MESSENGER_ID);
                                        else
                                            downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), VideoList.get(position).getStatusData(), ext4, position, MESSENGER_ID);
                                    }
                                }
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    String ext5 = "mp4";
                                    String url5 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext5, position, MESSENGER_ID);
                                    else
                                        downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), position, MESSENGER_ID);
                                }
                            }


                        }
                    });

                    holder.like_button_instagram_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(LikeButton likeButton) {
                            holder.like_button_instagram_image_item.setLiked(false);


                            if (mInterstitialAd.isLoaded()) {
                                if (check()) {
                                    mInterstitialAd.show();
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            requestNewInterstitial();
                                            if (!VideoList.get(position).isDownloading()) {
                                                String ext6 = "mp4";
                                                String url6 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext6, position, INSTAGRAM_ID);
                                                else
                                                    downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext6, position, INSTAGRAM_ID);
                                            }
                                        }
                                    });
                                } else {
                                    if (!VideoList.get(position).isDownloading()) {
                                        String ext7 = "mp4";
                                        String url7 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                            downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext7, position, INSTAGRAM_ID);
                                        else
                                            downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext7, position, INSTAGRAM_ID);
                                    }
                                }
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    String ext8 = "mp4";
                                    String url8 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext8, position, INSTAGRAM_ID);
                                    else
                                        downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), position, ext8, INSTAGRAM_ID);
                                }
                            }


                        }
                    });
                    holder.like_button_facebook_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(LikeButton likeButton) {
                            holder.like_button_facebook_image_item.setLiked(false);


                            if (mInterstitialAd.isLoaded()) {
                                if (check()) {
                                    mInterstitialAd.show();
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            requestNewInterstitial();
                                            if (!VideoList.get(position).isDownloading()) {
                                                String ext9 = "mp4";
                                                String url9 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext9, position, FACEBOOK_ID);
                                                else
                                                    downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext9, position, FACEBOOK_ID);
                                            }
                                        }
                                    });
                                } else {
                                    if (!VideoList.get(position).isDownloading()) {
                                        String ext10 = "mp4";
                                        String url10 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)

                                            downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext10, position, FACEBOOK_ID);
                                        else
                                            downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext10, position, FACEBOOK_ID);
                                    }
                                }
                            } else {
                                if (!VideoList.get(position).isDownloading()) {
                                    String ext11 = "mp4";
                                    String url11 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext11, position, FACEBOOK_ID);
                                    else
                                        downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext11, position, FACEBOOK_ID);
                                }
                            }


                        }
                    });
                    holder.like_button_share_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(LikeButton likeButton) {
                            holder.like_button_facebook_image_item.setLiked(false);


                            if (mInterstitialAd.isLoaded()) {
                                if (check()) {
                                    mInterstitialAd.show();
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            super.onAdClosed();
                                            requestNewInterstitial();
                                            if (!VideoList.get(position).isDownloading()) {
                                                String ext12 = "mp4";
                                                String url12 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                                    downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext12, position, SHARE_ID);
                                                else
                                                    downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext12, position, SHARE_ID);
                                            }
                                        }
                                    });
                                } else {

                                    if (!VideoList.get(position).isDownloading()) {
                                        String ext13 = "mp4";
                                        String url13 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                            downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext13, position, SHARE_ID);
                                        else
                                            downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext13, position, SHARE_ID);
                                    }
                                }
                            } else {

                                if (!VideoList.get(position).isDownloading()) {
                                    String ext14 = "mp4";
                                    String url14 = "https://www.youtube.com/watch?v=" + VideoList.get(position).getStatusData();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                        downloadFileFromURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, VideoList.get(position).getStatusTitle(), ext14, position, SHARE_ID);
                                    else
                                        downloadFileFromURL.execute(downloadUrl, VideoList.get(position).getStatusTitle(), ext14, position, SHARE_ID);
                                }
                            }

                        }
                    });

                    holder.image_view_image_item_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(activity.getApplicationContext(), VideoActivity.class);
                            intent.putExtra("id", VideoList.get(position).getStatusId());
                            intent.putExtra("title", VideoList.get(position).getStatusTitle());
                            intent.putExtra("thumbnail", VideoList.get(position).getStatusImage());
                            intent.putExtra("userid", VideoList.get(position).getUserId());
                            intent.putExtra("user", VideoList.get(position).getUserName());
                            intent.putExtra("userimage", VideoList.get(position).getUserProfilePicture());
                            //intent.putExtra("type", VideoList.get(position).getType());
                            intent.putExtra("video", VideoList.get(position).getStatusData());
                            intent.putExtra("image", VideoList.get(position).getStatusImage());
                            intent.putExtra("extension", VideoList.get(position).getStatusData());
                            intent.putExtra("isfavourite",VideoList.get(position).getStatusIsfavourite());
                            //intent.putExtra("comment", VideoList.get(position).ge());
                            intent.putExtra("downloads", VideoList.get(position).getStatusView());
                            // intent.putExtra("tags", VideoList.get(position).getTags());
                            intent.putExtra("review", VideoList.get(position).getStatusReview());
                            intent.putExtra("comments", VideoList.get(position).getStatusComments());
                            // intent.putExtra("created", VideoList.get(position).getCreated());
                            intent.putExtra("local", VideoList.get(position).getLocal());

                            intent.putExtra("woow1", VideoList.get(position).getStatusWow());
                            intent.putExtra("like1", VideoList.get(position).getStatusLike());
                            intent.putExtra("love1", VideoList.get(position).getStatusLove());
                            intent.putExtra("angry1", VideoList.get(position).getStatusAngry());
                            intent.putExtra("sad1", VideoList.get(position).getStatusSad());
                            intent.putExtra("haha1", VideoList.get(position).getStatusHaha());
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    });
                    holder.relative_layout_item_imge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(activity.getApplicationContext(), VideoActivity.class);
                            intent.putExtra("id", VideoList.get(position).getStatusId());
                            intent.putExtra("title", VideoList.get(position).getStatusTitle());
                            intent.putExtra("thumbnail", VideoList.get(position).getStatusImage());
                            intent.putExtra("userid", VideoList.get(position).getUserId());
                            intent.putExtra("user", VideoList.get(position).getUserName());
                            intent.putExtra("userimage", VideoList.get(position).getUserProfilePicture());
                            // intent.putExtra("type", VideoList.get(position).getType());
                            intent.putExtra("video", VideoList.get(position).getStatusData());
                            intent.putExtra("image", VideoList.get(position).getStatusImage());
                            intent.putExtra("extension", VideoList.get(position).getStatusData());
                            //intent.putExtra("comment", VideoList.get(position).getComment());
                            intent.putExtra("downloads", VideoList.get(position).getStatusView());
                            // intent.putExtra("tags", VideoList.get(position).getTags());
                            intent.putExtra("review", VideoList.get(position).getStatusReview());
                            intent.putExtra("comments", VideoList.get(position).getStatusComments());
                            // intent.putExtra("created", VideoList.get(position).getCreated());
                            intent.putExtra("local", VideoList.get(position).getLocal());
                            intent.putExtra("isfavourite",VideoList.get(position).getStatusIsfavourite());

                            intent.putExtra("woow1", VideoList.get(position).getStatusWow());
                            intent.putExtra("like1", VideoList.get(position).getStatusLike());
                            intent.putExtra("love1", VideoList.get(position).getStatusLove());
                            intent.putExtra("angry1", VideoList.get(position).getStatusAngry());
                            intent.putExtra("sad1", VideoList.get(position).getStatusSad());
                            intent.putExtra("haha1", VideoList.get(position).getStatusHaha());
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    });



                    /*final FavoritesStorage storageFavorites = new FavoritesStorage(activity.getApplicationContext());
                    List<VideoPojo> Videos = storageFavorites.loadImagesFavorites();
                    Boolean exist = false;
                    if (Videos == null) {
                        Videos = new ArrayList<>();
                    }
                    for (int i = 0; i < Videos.size(); i++) {
                        if (Videos.get(i).getStatusId() == (VideoList.get(position).getStatusId())) {
                            exist = true;
                        }
                    }
                    if (exist == false) {
                        holder.like_button_fav_image_item.setLiked(false);
                    } else {
                        holder.like_button_fav_image_item.setLiked(true);
                    }*/

                    if (VideoList.get(position).getStatusIsfavourite().equals("1"))
                    {
                        holder.like_button_fav_image_item.setLiked(true);
                    }
                    else {
                        holder.like_button_fav_image_item.setLiked(false);
                    }
                    holder.like_button_fav_image_item.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            /*List<VideoPojo> favorites_list = storageFavorites.loadImagesFavorites();
                            Boolean exist = false;
                            if (favorites_list == null) {
                                favorites_list = new ArrayList<>();
                            }
                            for (int i = 0; i < favorites_list.size(); i++) {
                                if (favorites_list.get(i).getStatusId() == (VideoList.get(position).getStatusId())) {
                                    exist = true;
                                }
                            }
                            if (exist == false) {
                                ArrayList<VideoPojo> audios = new ArrayList<VideoPojo>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    audios.add(favorites_list.get(i));
                                }
                                audios.add(VideoList.get(position));
                                storageFavorites.storeImage(audios);
                                holder.like_button_fav_image_item.setLiked(true);
                            } else {
                                ArrayList<VideoPojo> new_favorites = new ArrayList<VideoPojo>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    if (favorites_list.get(i).getStatusId() != (VideoList.get(position).getStatusId())) {
                                        new_favorites.add(favorites_list.get(i));

                                    }
                                }
                                if (favorites == true) {
                                    VideoList.remove(position);
                                    notifyDataSetChanged();
                                    //holder.ripple_view_wallpaper_item.setVisibility(View.GONE);
                                }
                                storageFavorites.storeImage(new_favorites);
                                holder.like_button_fav_image_item.setLiked(false);

                            }*/
                            status_Id = VideoList.get(position).getStatusId();
                            new FavouriteOperation().execute();
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                           /* List<VideoPojo> favorites_list = storageFavorites.loadImagesFavorites();
                            Boolean exist = false;
                            if (favorites_list == null) {
                                favorites_list = new ArrayList<>();
                            }
                            for (int i = 0; i < favorites_list.size(); i++) {
                                if (favorites_list.get(i).getStatusId() == (VideoList.get(position).getStatusId())) {
                                    exist = true;
                                }
                            }
                            if (exist == false) {
                                ArrayList<VideoPojo> audios = new ArrayList<VideoPojo>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    audios.add(favorites_list.get(i));
                                }
                                audios.add(VideoList.get(position));
                                storageFavorites.storeImage(audios);
                                holder.like_button_fav_image_item.setLiked(true);
                            } else {
                                ArrayList<VideoPojo> new_favorites = new ArrayList<VideoPojo>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    if (favorites_list.get(i).getStatusId() != (VideoList.get(position).getStatusId())) {
                                        new_favorites.add(favorites_list.get(i));

                                    }
                                }
                                if (favorites == true) {
                                    VideoList.remove(position);
                                    notifyDataSetChanged();
                                    //holder.ripple_view_wallpaper_item.setVisibility(View.GONE);
                                }
                                storageFavorites.storeImage(new_favorites);
                                holder.like_button_fav_image_item.setLiked(false);

                            }*/

                            status_Id = VideoList.get(position).getStatusId();
                            new FavouriteOperation().execute();
                        }
                    });

                    holder.like_button_delete_image_item.setOnAnimationEndListener(new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(LikeButton likeButton) {
                            final DownloadStorage downloadStorage = new DownloadStorage(activity.getApplicationContext());

                            List<VideoPojo> downVideoList = downloadStorage.loadImagesFavorites();
                            Boolean exist = false;
                            if (downVideoList == null) {
                                downVideoList = new ArrayList<>();
                            }
                            for (int i = 0; i < downVideoList.size(); i++) {
                                if (downVideoList.get(i).getStatusId() == (VideoList.get(position).getStatusId())) {
                                    exist = true;
                                }
                            }
                            if (exist == true) {
                                String pathlocal = VideoList.get(position).getLocal();
                                ArrayList<VideoPojo> new_dwonloads = new ArrayList<VideoPojo>();
                                for (int i = 0; i < downVideoList.size(); i++) {
                                    if (downVideoList.get(i).getStatusId() != (VideoList.get(position).getStatusId())) {
                                        new_dwonloads.add(downVideoList.get(i));

                                    }
                                }
                                if (downloads == true) {
                                    VideoList.remove(position);
                                    notifyDataSetChanged();
                                }
                                downloadStorage.storeImage(new_dwonloads);
                                holder.like_button_delete_image_item.setLiked(false);
                                Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(pathlocal));
                                File file = new File(pathlocal);
                                if (file.exists()) {
                                    file.delete();
                                    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                }

                            }
                        }
                    });
                    break;
                }
                case 3: {
                    break;
                }

            }
        }

        @Override
        public int getItemCount() {
            return VideoList.size();
        }

        @Override
        public int getItemViewType(int position) {

            return VideoList.get(position).getViewType();

        }

        public void addDownload(final Integer position) {
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<Integer> call = service.imageAddDownload(Integer.valueOf(VideoList.get(position).getStatusId()));
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                    if (response.isSuccessful()) {
                        VideoList.get(position).setStatusView(VideoList.get(position).getStatusView() + 1);
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });
        }

        /*public static String format(String value) {
            //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
            if (value == Long.MIN_VALUE) return format(String.valueOf(Long.MIN_VALUE + 1));
            if (value < 0) return "-" + format(-value);
            if (value < 1000) return Long.toString(value); //deal with easy case

            Map.Entry<Long, String> e = suffixes.floorEntry(value);
            Long divideBy = e.getKey();
            String suffix = e.getValue();

            long truncated = value / (divideBy / 10); //the number part of the output times 10
            boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
            return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
        }*/
        private void requestNewInterstitial() {
            AdRequest adRequest = new AdRequest.Builder().build();

            mInterstitialAd.loadAd(adRequest);
        }

        public boolean check() {
            PrefManager prf = new PrefManager(activity.getApplicationContext());
            if (!prf.getString("SUBSCRIBED").equals("FALSE")) {
                return false;
            }
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdf.format(c.getTime());

            if (prf.getString("LAST_DATE_ADS").equals("")) {
                prf.setString("LAST_DATE_ADS", strDate);
            } else {
                String toyBornTime = prf.getString("LAST_DATE_ADS");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    Date oldDate = dateFormat.parse(toyBornTime);
                    System.out.println(oldDate);

                    Date currentDate = new Date();

                    long diff = currentDate.getTime() - oldDate.getTime();
                    long seconds = diff / 1000;

                    if (seconds > Integer.parseInt(activity.getResources().getString(R.string.AD_MOB_TIME))) {
                        prf.setString("LAST_DATE_ADS", strDate);
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        public class ImageHolder extends RecyclerView.ViewHolder {
            private final RelativeLayout relative_layout_progress_image_item;
            private final TextView text_view_progress_image_item;
            private final ImageView image_view_cancel_image_item;
            private final LikeButton like_button_whatsapp_image_item;
            private final LikeButton like_button_messenger_image_item;
            private final LikeButton like_button_share_image_item;
            private final LikeButton like_button_instagram_image_item;
            private final LikeButton like_button_facebook_image_item;
            private final LikeButton like_button_fav_image_item;
            private final RelativeLayout relative_layout_item_imge;
            private final TextView text_view_downloads;
            private final RelativeLayout relative_layout_item_image_review;
            private final LikeButton like_button_delete_image_item;
            private ProgressBar progress_bar_item_image;
            private TextView text_view_image_item_name_user;
            private TextView text_view_image_item_title;
            private ImageView image_view_image_item_image;
            private CircleImageView circle_image_view_image_item_user;

            public ImageHolder(View itemView) {
                super(itemView);
                this.relative_layout_item_image_review = (RelativeLayout) itemView.findViewById(R.id.relative_layout_item_image_review);
                this.relative_layout_item_imge = (RelativeLayout) itemView.findViewById(R.id.relative_layout_item_imge);
                this.like_button_delete_image_item = (LikeButton) itemView.findViewById(R.id.like_button_delete_image_item);
                this.like_button_fav_image_item = (LikeButton) itemView.findViewById(R.id.like_button_fav_image_item);
                this.like_button_messenger_image_item = (LikeButton) itemView.findViewById(R.id.like_button_messenger_image_item);
                this.like_button_facebook_image_item = (LikeButton) itemView.findViewById(R.id.like_button_facebook_image_item);
                this.like_button_instagram_image_item = (LikeButton) itemView.findViewById(R.id.like_button_instagram_image_item);
                this.like_button_share_image_item = (LikeButton) itemView.findViewById(R.id.like_button_share_image_item);
                this.like_button_whatsapp_image_item = (LikeButton) itemView.findViewById(R.id.like_button_whatsapp_image_item);
                this.image_view_cancel_image_item = (ImageView) itemView.findViewById(R.id.image_view_cancel_image_item);
                this.text_view_progress_image_item = (TextView) itemView.findViewById(R.id.text_view_progress_image_item);
                this.relative_layout_progress_image_item = (RelativeLayout) itemView.findViewById(R.id.relative_layout_progress_image_item);
                this.progress_bar_item_image = (ProgressBar) itemView.findViewById(R.id.progress_bar_item_image);
                this.circle_image_view_image_item_user = (CircleImageView) itemView.findViewById(R.id.circle_image_view_image_item_user);
                this.text_view_image_item_name_user = (TextView) itemView.findViewById(R.id.text_view_image_item_name_user);
                this.text_view_image_item_title = (TextView) itemView.findViewById(R.id.text_view_image_item_title);
                this.image_view_image_item_image = (ImageView) itemView.findViewById(R.id.image_view_image_item_image);
                this.text_view_downloads = (TextView) itemView.findViewById(R.id.text_view_downloads);
            }
        }

        public class CategoriesHolder extends RecyclerView.ViewHolder {
            private final LinearLayoutManager linearLayoutManager;
            private final CategoryVideoAdapter categoryVideoAdapter;
            public RecyclerView recycler_view_item_categories;

            public CategoriesHolder(View view) {
                super(view);
                this.recycler_view_item_categories = (RecyclerView) itemView.findViewById(R.id.recycler_view_item_categories);
                this.linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                this.categoryVideoAdapter = new CategoryVideoAdapter(categoryList, activity);
                recycler_view_item_categories.setHasFixedSize(true);
                recycler_view_item_categories.setAdapter(categoryVideoAdapter);
                recycler_view_item_categories.setLayoutManager(linearLayoutManager);
            }
        }

        public class EmptyHolder extends RecyclerView.ViewHolder {


            public EmptyHolder(View view) {
                super(view);

            }
        }

        /**
         * Background Async Task to download file
         */


        class DownloadFileFromURL1 extends AsyncTask<String, String, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... f_url) {
                int count;

                try {

                    URL url = new URL(f_url[0]);
                    URLConnection conection = url.openConnection();
                    conection.connect();

                    int lenghtOfFile = conection.getContentLength();

                    InputStream input = new BufferedInputStream(url.openStream(), 8192);


                    File file = new File(getApplicationContext().getFilesDir()
                            .getPath(), "video status");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String uriSting = (file.getAbsolutePath() + "/"
                            + "ndfnsfjsd" + ".mp4");

                    OutputStream output = new FileOutputStream(uriSting);
                    byte data[] = new byte[1024];
                    long total = 0;
                    int latestPercentDone;
                    int percentDone = -1;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                    /*latestPercentDone = (int) Math.round(total / lenghtOfFile * 100.0);
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    if (percentDone != latestPercentDone) {
                        percentDone = latestPercentDone;
                        publishProgress(""+percentDone);
                    }*/

                        latestPercentDone = (int) ((total / (float) lenghtOfFile) * 100);
                        if (percentDone != latestPercentDone) {
                            percentDone = latestPercentDone;
                            publishProgress("" + percentDone);
                        }
                        //publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        // writing data to file
                        output.write(data, 0, count);
                    }
                    // flushing output
                    output.flush();
                    // closing streams
                    output.close();
                    input.close();
                } catch (Exception e) {
                    //Log.e("Error: ", e.getMessage());
                }
                return null;
            }

            /**
             * Updating progress bar
             */
            protected void onProgressUpdate(String... progress) {
                // setting progress percentage
                /*holder.progress_bar_item_image.setProgress(Integer.parseInt(progress[0]));
                holder.text_view_progress_image_item.setText(Integer.parseInt(progress[0]) + "%");*/
                            /*mBuilder.setContentText("" + Integer.parseInt(progress[0]) + "%");
                            mBuilder.setProgress(100, Integer.parseInt(progress[0]), false);
                            notificationManager.notify(getUniqueInteger(link), mBuilder.build());*/
            }

            @Override
            protected void onPostExecute(String file_url) {
                            /*detail.setVisibility(View.VISIBLE);*/
               /* holder.relative_layout_progress_image_item.setVisibility(View.GONE);*/

                            /*mBuilder.setContentTitle("GPSC Direct");
                            mBuilder.setContentText("Download Complete");


                            notificationManager.notify(getUniqueInteger(link), mBuilder.build());*/
                //sendNotification(file_url);
            }

        }


        class DownloadFileFromURL extends AsyncTask<Object, String, String> {

            private int position;
            private String old = "-100";
            private boolean runing = true;
            private String share_app;

            /**
             * Before starting background thread
             * Show Progress Bar Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            public boolean dir_exists(String dir_path) {
                boolean ret = false;
                File dir = new File(dir_path);
                if (dir.exists() && dir.isDirectory()) ret = true;
                return ret;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                runing = false;
            }

            /**
             * Downloading file in background thread
             */
            @Override
            protected String doInBackground(Object... f_url) {
                int count;
                try {
                    URL url = new URL((String) f_url[0]);
                    String title = (String) f_url[1];
                    String extension = (String) f_url[2];
                    this.position = (int) f_url[3];
                    this.share_app = (String) f_url[4];
                    String id = String.valueOf(VideoList.get(position).getStatusId());


                    VideoList.get(position).setDownloading(true);
                    URLConnection conection = url.openConnection();
                    conection.setRequestProperty("Accept-Encoding", "identity");

                    conection.connect();
                    int lenghtOfFile = conection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);


                    String dir_path = Environment.getExternalStorageDirectory().toString() + "/StatusVideos/";

                    if (!dir_exists(dir_path)) {
                        File directory = new File(dir_path);
                        if (directory.mkdirs()) {
                            Log.v("dir", "is created 1");
                        } else {
                            Log.v("dir", "not created 1");

                        }
                        if (directory.mkdir()) {
                            Log.v("dir", "is created 2");
                        } else {
                            Log.v("dir", "not created 2");

                        }
                    } else {
                        Log.v("dir", "is exist");
                    }
                    File file = new File(dir_path + title.toString().replace("/", "_") + "_" + id + "." + extension);
                    if (!file.exists()) {
                        // Output stream
                        OutputStream output = new FileOutputStream(dir_path + title.toString().replace("/", "_") + "_" + id + "." + extension);

                        byte data[] = new byte[1024];

                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
                            // publishing the progress....
                            // After this onProgressUpdate will be called
                            publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                            // writing data to file
                            output.write(data, 0, count);
                            if (!runing) {
                                Log.v("v", "not rurning");
                            }
                        }

                        output.flush();


                        output.close();
                        input.close();
                        MediaScannerConnection.scanFile(activity.getApplicationContext(), new String[]{dir_path + title.toString().replace("/", "_") + "_" + id + "." + extension}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            final Uri contentUri = Uri.fromFile(new File(dir_path + title.toString().replace("/", "_") + "_" + id + "." + extension));
                            scanIntent.setData(contentUri);
                            activity.sendBroadcast(scanIntent);
                        } else {
                            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                            activity.sendBroadcast(intent);
                        }
                    }
                    VideoList.get(position).setPath(dir_path + title.toString().replace("/", "_") + "_" + id + "." + extension);
                } catch (Exception e) {
                    Log.v("ex", e.getMessage());
                }

                return null;
            }

            /**
             * Updating progress bar
             */
            protected void onProgressUpdate(String... progress) {
                // setting progress percentage
                try {
                    if (!progress[0].equals(old)) {
                        VideoList.get(position).setProgress(Integer.valueOf(progress[0]));
                        notifyDataSetChanged();
                        old = progress[0];
                        Log.v("download", progress[0] + "%");
                        VideoList.get(position).setDownloading(true);
                        VideoList.get(position).setProgress(Integer.parseInt(progress[0]));
                    }
                } catch (Exception e) {

                }

            }

            public void AddDownloadLocal(Integer position) {
                final DownloadStorage downloadStorage = new DownloadStorage(activity.getApplicationContext());
                List<VideoPojo> download_list = downloadStorage.loadImagesFavorites();
                Boolean exist = false;
                if (download_list == null) {
                    download_list = new ArrayList<>();
                }
                for (int i = 0; i < download_list.size(); i++) {
                    if (download_list.get(i).getStatusId() == (VideoList.get(position).getStatusId())) {
                        exist = true;
                    }
                }
                if (exist == false) {
                    ArrayList<VideoPojo> audios = new ArrayList<VideoPojo>();
                    for (int i = 0; i < download_list.size(); i++) {
                        audios.add(download_list.get(i));
                    }
                    VideoPojo videodownloaded = VideoList.get(position);

                    videodownloaded.setLocal(VideoList.get(position).getPath());

                    audios.add(videodownloaded);
                    downloadStorage.storeImage(audios);
                }
            }

            /**
             * After completing background task
             * Dismiss the progress dialog
             **/
            @Override
            protected void onPostExecute(String file_url) {

                VideoList.get(position).setDownloading(false);
                if (VideoList.get(position).getPath() == null) {
                    if (downloads) {
                        switch (share_app) {
                            case WHATSAPP_ID:
                                shareWhatsapp(position);
                                break;
                            case FACEBOOK_ID:
                                shareFacebook(position);
                                break;
                            case MESSENGER_ID:
                                shareMessenger(position);
                                break;
                            case INSTAGRAM_ID:
                                shareInstagram(position);
                                break;
                            case SHARE_ID:
                                share(position);
                                break;
                        }
                    } else {
                        Toasty.error(activity.getApplicationContext(), activity.getString(R.string.download_failed), Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    addDownload(position);
                    AddDownloadLocal(position);
                    switch (share_app) {
                        case WHATSAPP_ID:
                            shareWhatsapp(position);
                            break;
                        case FACEBOOK_ID:
                            shareFacebook(position);
                            break;
                        case MESSENGER_ID:
                            shareMessenger(position);
                            break;
                        case INSTAGRAM_ID:
                            shareInstagram(position);
                            break;
                        case SHARE_ID:
                            share(position);
                            break;
                    }
                }
                notifyDataSetChanged();
            }

            public void shareWhatsapp(Integer position) {
                String path = VideoList.get(position).getPath();
                if (VideoList.get(position).getLocal() != null) {
                    if (new File(VideoList.get(position).getLocal()).exists()) {
                        path = VideoList.get(position).getLocal();
                    }
                }
                Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage(WHATSAPP_ID);


                final String final_text = activity.getResources().getString(R.string.download_more_from_link);

                shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);


                // shareIntent.setType(VideoList.get(position).getType());
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    activity.startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT, true).show();
                }
            }

            public void shareFacebook(Integer position) {
                String path = VideoList.get(position).getPath();

                Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage(FACEBOOK_ID);


                final String final_text = activity.getResources().getString(R.string.download_more_from_link);

                shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

                //shareIntent.setType(VideoList.get(position).getType());
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    activity.startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.facebook_not_installed), Toast.LENGTH_SHORT, true).show();
                }
            }

            public void shareMessenger(Integer position) {
                String path = VideoList.get(position).getPath();

                Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage(MESSENGER_ID);

                final String final_text = activity.getResources().getString(R.string.download_more_from_link);

                shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

                //.setType(VideoList.get(position).getType());
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    activity.startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.messenger_not_installed), Toast.LENGTH_SHORT, true).show();
                }
            }

            public void shareInstagram(Integer position) {
                String path = VideoList.get(position).getPath();

                Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage(INSTAGRAM_ID);


                final String final_text = activity.getResources().getString(R.string.download_more_from_link);

                shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

                //shareIntent.setType(VideoList.get(position).getType());
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    activity.startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.instagram_not_installed), Toast.LENGTH_SHORT, true).show();
                }
            }

            public void share(Integer position) {
                String path = VideoList.get(position).getPath();

                Uri imageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(path));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);


                final String final_text = activity.getResources().getString(R.string.download_more_from_link);
                shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

                //shareIntent.setType(VideoList.get(position).getType());
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    activity.startActivity(Intent.createChooser(shareIntent, "Shared via " + activity.getResources().getString(R.string.app_name)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toasty.error(activity.getApplicationContext(), activity.getResources().getString(R.string.app_not_installed), Toast.LENGTH_SHORT, true).show();
                }
            }

        }

        public class FacebookNativeHolder extends RecyclerView.ViewHolder {
            private final String TAG = "WALLPAPERADAPTER";
            private LinearLayout nativeAdContainer;
            private LinearLayout adView;
            private NativeAd nativeAd;

            public FacebookNativeHolder(View view) {
                super(view);
                loadNativeAd(view);
            }

            private void loadNativeAd(final View view) {
                // Instantiate a NativeAd object.
                // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
                // now, while you are testing and replace it later when you have signed up.
                // While you are using this temporary code you will only get test ads and if you release
                // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
                nativeAd = new NativeAd(activity, activity.getString(R.string.FACEBOOK_ADS_NATIVE_PLACEMENT_ID));
                nativeAd.setAdListener(new NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(Ad ad) {
                        // Native ad finished downloading all assets
                        Log.e(TAG, "Native ad finished downloading all assets.");
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        // Native ad failed to load
                        Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        // Native ad is loaded and ready to be displayed
                        Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                        // Race condition, load() called again before last ad was displayed
                        if (nativeAd == null || nativeAd != ad) {
                            return;
                        }
                   /* NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                            .setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark))
                            .setTitleTextColor(Color.WHITE)
                            .setDescriptionTextColor(Color.WHITE)
                            .setButtonColor(Color.WHITE);

                    View adView = NativeAdView.render(activity, nativeAd, NativeAdView.Type.HEIGHT_300, viewAttributes);

                    LinearLayout nativeAdContainer = (LinearLayout) view.findViewById(R.id.native_ad_container);
                    nativeAdContainer.addView(adView);*/
                        // Inflate Native Ad into Container
                        inflateAd(nativeAd, view);
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        // Native ad clicked
                        Log.d(TAG, "Native ad clicked!");
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        // Native ad impression
                        Log.d(TAG, "Native ad impression logged!");
                    }
                });

                // Request an ad
                nativeAd.loadAd();
            }

            private void inflateAd(NativeAd nativeAd, View view) {

                nativeAd.unregisterView();

                // Add the Ad view into the ad container.
                nativeAdContainer = view.findViewById(R.id.native_ad_container);
                LayoutInflater inflater = LayoutInflater.from(activity);
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout_1, nativeAdContainer, false);
                nativeAdContainer.addView(adView);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = view.findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(activity, nativeAd, true);
                adChoicesContainer.addView(adChoicesView, 0);

                // Create native UI using the ad metadata.
                AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdvertiserName());
                nativeAdBody.setText(nativeAd.getAdBodyText());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                // Create a list of clickable views
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);

                // Register the Title and CTA button to listen for clicks.
                nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
            }

        }
    }


}
