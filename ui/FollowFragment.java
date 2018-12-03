package com.fourarc.videostatus.ui.fragement;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.adapter.VideoAdapter;
import com.fourarc.videostatus.config.VideoPojo;
import com.fourarc.videostatus.manager.PrefManager;
import com.fourarc.videostatus.network.Constant_store;
import com.fourarc.videostatus.network.NetworkConnection;
import com.fourarc.videostatus.network.ServiceHandler;
import com.fourarc.videostatus.ui.LoginActivity;
import com.fourarc.videostatus.ui.MainActivity;
import com.peekandpop.shalskar.peekandpop.PeekAndPop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowFragment extends Fragment {

    private Integer page = 0;
    private Boolean loaded=false;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;

    private View view;
    private RelativeLayout relative_layout_follow_fragment;
    private SwipeRefreshLayout swipe_refreshl_follow_fragment;
    private RecyclerView recycle_view_follow_fragment;
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_page_error;
    private Button button_try_again;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private VideoAdapter videoAdapter;
    private List<VideoPojo> VideoList =new ArrayList<>();
    private Button button_login_nav_follow_fragment;
    private LinearLayout linear_layout_follow_fragment_me;
    private Integer id_user;
    int page_no = 1;
    int last_page;
    int last;
    public static ArrayList<VideoPojo> arrayList_lates = new ArrayList<>();
    ArrayList<VideoPojo> arrayList_local = new ArrayList<>();
    private boolean userScrolled = true, first_call = true;

    private AlertDialog.Builder builderFollowing;

    private AlertDialog.Builder builderFollowers;
    private ProgressDialog loading_progress;
    private LinearLayoutManager linearLayoutManager;
    private PeekAndPop peekAndPop;
    private PrefManager prefManager;
    private String language;
    private ImageView imageView_empty_follow;
    private Integer item = 0 ;
    private Integer lines_beetween_ads = 8 ;
    private Boolean native_ads_enabled = false ;

    public FollowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=  inflater.inflate(R.layout.fragment_follow, container, false);
        this.prefManager= new PrefManager(getActivity().getApplicationContext());

        this.language=prefManager.getString("LANGUAGE_DEFAULT");
        initView();
        initAction();
        new Getfollowersvideos().execute();
        nw = new NetworkConnection(getActivity());
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setCancelable(false);

        recycle_view_follow_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
        {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                userScrolled = true;
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {


                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {

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

                    }
                    if (dy < 0) {
                    }
                }
            }
        });

        return  view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

      /*  if (isVisibleToUser){
            if (!loaded) {
                page = 0;
                loading = true;
                VideoList.clear();
               // loadStatus();
                new Getfollowersvideos().execute();
            }
        }
        else{

        }*/
    }

    private void initAction() {
/*
        recycle_view_follow_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
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
                            //loadNextStatus();
                            new Getfollowersvideos().execute();
                        }
                    }
                }else{

                }
            }
        });
*/
        this.swipe_refreshl_follow_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item = 0;
                page = 0;
                loading = true;
                VideoList.clear();
                //loadStatus();

                page_no = 1;
                new Getfollowersvideos().execute();
                swipe_refreshl_follow_fragment.setRefreshing(false);
            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 0;
                item = 0;
                loading = true;
                VideoList.clear();
                page_no=1;
                new Getfollowersvideos().execute();
                //loadStatus();
            }
        });
        this.button_login_nav_follow_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).setFromLogin();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });

    }
    public void initView(){
        if (getResources().getString(R.string.FACEBOOK_ADS_ENABLED_NATIVE).equals("true")){
            native_ads_enabled=true;
            lines_beetween_ads=Integer.parseInt(getResources().getString(R.string.FACEBOOK_ADS_ITEM_BETWWEN_ADS));
        }
        PrefManager prefManager= new PrefManager(getActivity().getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled=false;
        }
        this.imageView_empty_follow=(ImageView) view.findViewById(R.id.imageView_empty_follow);
        this.relative_layout_follow_fragment=(RelativeLayout) view.findViewById(R.id.relative_layout_follow_fragment);
        this.swipe_refreshl_follow_fragment=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_follow_fragment);
        this.recycle_view_follow_fragment=(RecyclerView) view.findViewById(R.id.recycle_view_follow_fragment);
        this.relative_layout_load_more=(RelativeLayout) view.findViewById(R.id.relative_layout_load_more);
        this.linear_layout_follow_fragment_me=(LinearLayout) view.findViewById(R.id.linear_layout_follow_fragment_me);
        this.linear_layout_page_error=(LinearLayout) view.findViewById(R.id.linear_layout_page_error);
        this.button_try_again=(Button) view.findViewById(R.id.button_try_again);
        this.button_login_nav_follow_fragment=(Button) view.findViewById(R.id.button_login_nav_follow_fragment);
        this.linearLayoutManager=  new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        videoAdapter = new VideoAdapter(arrayList_lates, null, getActivity(), peekAndPop);
        this.peekAndPop = new PeekAndPop.Builder(getActivity())
                .parentViewGroupToDisallowTouchEvents(swipe_refreshl_follow_fragment)
                .peekLayout(R.layout.dialog_view)
                .build();
        videoAdapter =new VideoAdapter(arrayList_lates,null,getActivity(),peekAndPop);
        recycle_view_follow_fragment.setHasFixedSize(true);
        recycle_view_follow_fragment.setAdapter(videoAdapter);
        recycle_view_follow_fragment.setLayoutManager(linearLayoutManager);


        PrefManager prf= new PrefManager(getActivity().getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            this.id_user = Integer.parseInt(prf.getString("ID_USER"));
            linear_layout_follow_fragment_me.setVisibility(View.GONE);
            relative_layout_follow_fragment.setVisibility(View.VISIBLE);
        }else{
            linear_layout_follow_fragment_me.setVisibility(View.VISIBLE);
            relative_layout_follow_fragment.setVisibility(View.GONE);
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
                    //json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));
                    json_main.put("user_id", "27");
                    json_main.put("type", "follow");

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


                    recycle_view_follow_fragment.setVisibility(View.VISIBLE);
                    //linear_layout_load_popular_fragment.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);
                   // videoAdapter = new VideoAdapter(arrayList_lates, null, getActivity(), peekAndPop, true);
                    recycle_view_follow_fragment.setHasFixedSize(true);

                    recycle_view_follow_fragment.setAdapter(videoAdapter);
                    recycle_view_follow_fragment.setLayoutManager(linearLayoutManager);

                } else {

                }

            }
            super.onPostExecute(result);
        }
    }

    private class Getfollowersvideos extends AsyncTask<String, Void, Void> {

        String response = null;
        String status, responseMsg = "Something went wrong..!!";

        ArrayList<VideoPojo> arrayList_local = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            VideoList = new ArrayList<>();
            arrayList_lates = new ArrayList<>();
            arrayList_lates.clear();
           /* if (first_call) {
                arrayList_local.clear();
            }*/
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
                    json_main.put("user_id", "27");
                    json_main.put("page", page_no);
                    json_main.put("type", "follow");

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
                            arrayList_local.addAll(arrayList_lates);

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


                    recycle_view_follow_fragment.setVisibility(View.VISIBLE);
                    //linear_layout_load_popular_fragment.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.GONE);
                    swipe_refreshl_follow_fragment.setRefreshing(false);
                    videoAdapter = new VideoAdapter(arrayList_lates, null, getActivity(), peekAndPop, true);
                    recycle_view_follow_fragment.setHasFixedSize(true);
                    recycle_view_follow_fragment.setAdapter(videoAdapter);
                    recycle_view_follow_fragment.setLayoutManager(linearLayoutManager);
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
    private void loadStatus() {

        linear_layout_page_error.setVisibility(View.GONE);
        swipe_refreshl_follow_fragment.setRefreshing(true);

        VideoList.add(new VideoPojo().setViewType(0));
        videoAdapter.notifyDataSetChanged();

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
                                    VideoList.add(new VideoPojo().setViewType(3));
                                }
                            }
                        }

                        videoAdapter.notifyDataSetChanged();
                        page++;
                        loaded=true;
                        recycle_view_follow_fragment.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                        imageView_empty_follow.setVisibility(View.GONE);
                    }else{
                        recycle_view_follow_fragment.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                        imageView_empty_follow.setVisibility(View.VISIBLE);
                    }

                }else{
                    recycle_view_follow_fragment.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                    imageView_empty_follow.setVisibility(View.GONE);

                }
                swipe_refreshl_follow_fragment.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<VideoPojo>> call, Throwable t) {
                recycle_view_follow_fragment.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                imageView_empty_follow.setVisibility(View.GONE);
                swipe_refreshl_follow_fragment.setRefreshing(false);

            }


        });
    }
*/
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
                json_main.put("user_id", "23");
                json_main.put("status_id", "59537");

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
            Snackbar snackbar = Snackbar
                    .make(button_try_again, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundResource(R.color.colorPrimary);
            snackbar.show();

        } else {

            if ("true".equalsIgnoreCase(status)) {
                Snackbar snackbar = Snackbar
                        .make(button_try_again, msg, Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();

            } else {
                Snackbar snackbar = Snackbar
                        .make(button_try_again, msg, Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();
            }
        }
        super.onPostExecute(result);
    }
}
    public void Resume() {
        try {
            PrefManager prf= new PrefManager(getActivity().getApplicationContext());

            if (prf.getString("LOGGED").toString().equals("TRUE")){
                relative_layout_follow_fragment.setVisibility(View.VISIBLE);
                linear_layout_follow_fragment_me.setVisibility(View.GONE);


                this.id_user = Integer.parseInt(prf.getString("ID_USER"));

                item = 0;
                page = 0;
                loading = true;
                VideoList.clear();

               // loadStatus();

            }else{
                relative_layout_follow_fragment.setVisibility(View.GONE);
                linear_layout_follow_fragment_me.setVisibility(View.VISIBLE);
            }
        }catch (java.lang.NullPointerException e){
            startActivity(new Intent(getContext(),MainActivity.class));
            getActivity().finish();
        }
    }


    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }
    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

}
