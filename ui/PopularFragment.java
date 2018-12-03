package com.fourarc.videostatus.ui.fragement;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.fourarc.videostatus.network.Utility;
import com.peekandpop.shalskar.peekandpop.PeekAndPop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopularFragment extends Fragment {

    private Integer page = 0;


    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;
    private boolean userScrolled = true, first_call = true;
    ArrayList<VideoPojo> arrayList_local = new ArrayList<>();

    private View view;
    private PrefManager prefManager;
    private String language = "0";
    private boolean loaded = false;
    private RelativeLayout relative_layout_load_more;
    private Button button_try_again;
    private SwipeRefreshLayout swipe_refreshl_popular_fragment;
    private LinearLayout linear_layout_page_error;
    private LinearLayout linear_layout_load_popular_fragment;
    private RecyclerView recycler_view_popular_fragment;
    private LinearLayoutManager linearLayoutManager;
    int page_no = 1;
    int last_page;
    public static ArrayList<VideoPojo> arrayList_lates = new ArrayList<>();

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    int last;
    private List<VideoPojo> VideoList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private VideoAdapter videoAdapter;
    private PeekAndPop peekAndPop;
    private Integer item = 0;
    private Integer lines_beetween_ads = 8;
    private Boolean native_ads_enabled = false;


    public PopularFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            if (!loaded) {
                // loadStatus();
                new GetPopularvideos().execute();

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.view = inflater.inflate(R.layout.fragment_popular, container, false);
        this.prefManager = new PrefManager(getActivity().getApplicationContext());

        this.language = prefManager.getString("LANGUAGE_DEFAULT");

        initView();
        initAction();
        nw = new NetworkConnection(getActivity());
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setCancelable(false);
        //  loadStatus();
        new GetPopularvideos().execute();
        recycler_view_popular_fragment.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findLastVisibleItemPosition();

                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == Constant_store.arrayList_local.size() - 1) {
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


    private void initAction() {
        this.swipe_refreshl_popular_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                videoAdapter.notifyDataSetChanged();

              /*  page = 0;
                loading = true;
                item = 0;
                new GetPopularvideos().execute();*/
                page_no = 1;
                new GetPopularvideos().execute();
                swipe_refreshl_popular_fragment.setRefreshing(false);
                // loadStatus();
            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoAdapter.notifyDataSetChanged();

                page = 0;
                item = 0;
                loading = true;
                page_no = 1;
              new GetPopularvideos().execute();

                //loadStatus();
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
        this.swipe_refreshl_popular_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_popular_fragment);
        this.linear_layout_page_error = (LinearLayout) view.findViewById(R.id.linear_layout_page_error);
        this.linear_layout_load_popular_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_popular_fragment);
        this.recycler_view_popular_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_popular_fragment);
        this.linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        this.swipe_refreshl_popular_fragment.setProgressViewOffset(false, 0, 300);

        this.peekAndPop = new PeekAndPop.Builder(getActivity()).parentViewGroupToDisallowTouchEvents(recycler_view_popular_fragment).peekLayout(R.layout.dialog_view).build();
        videoAdapter = new VideoAdapter(arrayList_lates, null, getActivity(), peekAndPop, true);
        recycler_view_popular_fragment.setHasFixedSize(true);
        recycler_view_popular_fragment.setAdapter(videoAdapter);
        recycler_view_popular_fragment.setLayoutManager(linearLayoutManager);
    }


/*
    public void loadStatus(){
        recycler_view_popular_fragment.setVisibility(View.GONE);
        linear_layout_load_popular_fragment.setVisibility(View.VISIBLE);
        linear_layout_page_error.setVisibility(View.GONE);
        swipe_refreshl_popular_fragment.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<VideoPojo>> call = service.getvideo(Constant_store.c_Key,Constant_store.c_Secret);
        call.enqueue(new Callback<List<VideoPojo>>() {
            @Override
            public void onResponse(Call<List<VideoPojo>> call, Response<List<VideoPojo>> response) {
               // apiClient.FormatData(getActivity(),response);
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        VideoList.clear();

                        for (int i=0;i<response.body().size();i++){
                            VideoList.add(response.body().get(i));
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;

                                }
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
                        page++;
                        loaded=true;
                    }else {

                    }
                    recycler_view_popular_fragment.setVisibility(View.VISIBLE);
                    linear_layout_load_popular_fragment.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);

                }else{
                    recycler_view_popular_fragment.setVisibility(View.GONE);
                    linear_layout_load_popular_fragment.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
                swipe_refreshl_popular_fragment.setRefreshing(false);

            }
            @Override
            public void onFailure(Call<List<VideoPojo>> call, Throwable t) {
                recycler_view_popular_fragment.setVisibility(View.GONE);
                linear_layout_load_popular_fragment.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_popular_fragment.setRefreshing(false);
            }
        });
    }
*/
/*
    public void loadNextStatus(){
        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<VideoPojo>> call = service.getvideo(Constant_store.c_Key,Constant_store.c_Secret);
        call.enqueue(new Callback<List<VideoPojo>>() {
            @Override
            public void onResponse(Call<List<VideoPojo>> call, Response<List<VideoPojo>> response) {
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        for (int i=0;i<response.body().size();i++){
                            VideoList.add(response.body().get(i));
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;
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
                    //json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));
                    json_main.put("user_id", "27");
                    json_main.put("type", "popular");

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


                    recycler_view_popular_fragment.setVisibility(View.VISIBLE);
                    linear_layout_load_popular_fragment.setVisibility(View.GONE);
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

        private class GetPopularvideos extends AsyncTask<String, Void, Void> {

            String response = null;
            String status, responseMsg = "Something went wrong..!!";

            ArrayList<VideoPojo> arrayList_local = new ArrayList<>();
            @Override
            protected void onPreExecute() {

               /* if (first_call) {
                    arrayList_local.clear();
                }*/

               // VideoList = new ArrayList<>();
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
                        json_main.put("user_id", Utility.getStringSharedPreferences(getApplicationContext(), Constant_store.customer_id));
                        json_main.put("page", page_no);
                        /*json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));*/
                        json_main.put("type", "popular");

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


                        recycler_view_popular_fragment.setVisibility(View.VISIBLE);
                        linear_layout_load_popular_fragment.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                        videoAdapter = new VideoAdapter(arrayList_lates, null, getActivity(), peekAndPop, true);
                        recycler_view_popular_fragment.setHasFixedSize(true);
                        recycler_view_popular_fragment.setAdapter(videoAdapter);
                        recycler_view_popular_fragment.setLayoutManager(linearLayoutManager);
                        videoAdapter.notifyDataSetChanged();
                    } else {
                        relative_layout_load_more.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.VISIBLE);
                    }

                }
                super.onPostExecute(result);
            }
        }

    }

