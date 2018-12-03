package com.fourarc.videostatus.ui.fragement;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.adapter.VideoAdapter;
import com.fourarc.videostatus.config.VideoPojo;
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


public class FavoritesFragment extends Fragment {

    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;

    private RelativeLayout activity_favorites;
    private RecyclerView recycle_view_home_favorite;
    private ImageView imageView_empty_favorite;
    private SwipeRefreshLayout swipe_refreshl_home_favorite;
    private List<VideoPojo> VideoList =new ArrayList<VideoPojo>();
    private VideoAdapter videoAdapter;
    int last;
    private boolean loading = true;
    int page_no = 1;
    int last_page;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    ArrayList<VideoPojo> arrayList_local = new ArrayList<>();
    private boolean userScrolled = true, first_call = true;
    private View view;
    private GridLayoutManager gridLayoutManager;
    private PrefManager prf;
    private PeekAndPop peekAndPop;
    private Integer item = 0 ;
    private Integer lines_beetween_ads = 8 ;
    private Boolean native_ads_enabled = false ;
    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            item = 0;
            //getStatus();

            new GetFavouritevideos().execute();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_favorites, container, false);
        this.prf= new PrefManager(getActivity().getApplicationContext());

        iniView(view);
        initAction();
       // getStatus();
        new GetFavouritevideos().execute();
        nw = new NetworkConnection(getActivity());
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setCancelable(false);


        recycle_view_home_favorite.setLayoutManager(gridLayoutManager);

        recycle_view_home_favorite.addOnScrollListener(new RecyclerView.OnScrollListener()
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

                    visibleItemCount    = gridLayoutManager.getChildCount();
                    totalItemCount      = gridLayoutManager.getItemCount();
                    pastVisiblesItems   = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
/*
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
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
*/

                       /* if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == arrayList_local.size() - 1) {
                            userScrolled = false;
                            loading = false;

                            page_no = page_no + 1;
                            linear_layout_load_popular_fragment.setVisibility(View.VISIBLE);

                            if (first_call) {
                                first_call = false;

                                new GetPopularvideos().execute();
                                swipe_refreshl_popular_fragment.setRefreshing(false);

                            }
                            if (page_no <= last_page) {
                                new GetPopularvideos().execute();
                                //linear_layout_load_popular_fragment.setVisibility(View.VISIBLE);

                                swipe_refreshl_popular_fragment.setRefreshing(false);
                            }
                        }*/

                        if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == Constant_store.arrayList_local.size() - 1) {
                            userScrolled = false;
                            first_call = false;
                            page_no = page_no + 1;

                            new GetFavouritevideos().execute();
                            if (page_no <= last_page) {
                            }
                        }

                    }   if (dy < 0) {

                }
                }
            }
        });



        return view;
    }





    public void iniView(View  view){

        if (getResources().getString(R.string.FACEBOOK_ADS_ENABLED_NATIVE).equals("true")){
            native_ads_enabled=true;
            lines_beetween_ads=Integer.parseInt(getResources().getString(R.string.FACEBOOK_ADS_ITEM_BETWWEN_ADS));
        }
        PrefManager prefManager= new PrefManager(getActivity().getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled=false;
        }
        this.activity_favorites=(RelativeLayout) view.findViewById(R.id.activity_favorites);

        this.recycle_view_home_favorite=(RecyclerView) view.findViewById(R.id.recycle_view_home_favorite);
        this.swipe_refreshl_home_favorite=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_home_favorite);
        this.imageView_empty_favorite=(ImageView) view.findViewById(R.id.imageView_empty_favorite);




        this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),1,GridLayoutManager.VERTICAL,false);


        this.recycle_view_home_favorite=(RecyclerView) this.view.findViewById(R.id.recycle_view_home_favorite);
        this.swipe_refreshl_home_favorite=(SwipeRefreshLayout)  this.view.findViewById(R.id.swipe_refreshl_home_favorite);

        this.peekAndPop = new PeekAndPop.Builder(getActivity())
                .parentViewGroupToDisallowTouchEvents(recycle_view_home_favorite)
                .peekLayout(R.layout.dialog_view)
                .build();
        videoAdapter =new VideoAdapter(VideoList,null,getActivity(),peekAndPop,true);
        recycle_view_home_favorite.setHasFixedSize(true);
        recycle_view_home_favorite.setAdapter(videoAdapter);
       // recycle_view_home_favorite.setLayoutManager(gridLayoutManager);
    }

    public void initAction(){
        this.swipe_refreshl_home_favorite.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item = 0;
               // getStatus();

                page_no = 1;
                new GetFavouritevideos().execute();
                swipe_refreshl_home_favorite.setRefreshing(false);

            }
        });
    }
    private class GetFavouritevideos extends AsyncTask<String, Void, Void> {

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
                    json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));
                    json_main.put("page", page_no);
                    /*json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));*/
                    json_main.put("type", "favourite");

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

               // linear_layout_page_error.setVisibility(View.VISIBLE);
                //relative_layout_load_more.setVisibility(View.GONE);
            } else {


                if (status.equalsIgnoreCase("true")) {


                    recycle_view_home_favorite.setVisibility(View.VISIBLE);
                   // linear_layout_load_popular_fragment.setVisibility(View.GONE);
                   // linear_layout_page_error.setVisibility(View.GONE);
                    videoAdapter = new VideoAdapter(arrayList_local, null, getActivity(), peekAndPop, true);
                    recycle_view_home_favorite.setHasFixedSize(true);
                    recycle_view_home_favorite.setAdapter(videoAdapter);
                    recycle_view_home_favorite.setLayoutManager(gridLayoutManager);
                    videoAdapter.notifyDataSetChanged();
                } else {
                   // relative_layout_load_more.setVisibility(View.GONE);
                   // linear_layout_page_error.setVisibility(View.VISIBLE);
                    imageView_empty_favorite.setVisibility(View.VISIBLE);
                    recycle_view_home_favorite.setVisibility(View.GONE);
                }

            }
            super.onPostExecute(result);
        }
    }

/*
    private void getStatus() {

        swipe_refreshl_home_favorite.setRefreshing(true);
        final FavoritesStorage storageFavorites= new FavoritesStorage(getActivity().getApplicationContext());
        List<VideoPojo> statuses = storageFavorites.loadImagesFavorites();

        if (statuses==null){
            statuses= new ArrayList<>();
        }
        if (statuses.size()!=0){
            VideoList.clear();
            VideoList.add(new VideoPojo().setViewType(0));

            for (int i=0;i<statuses.size();i++){
                VideoPojo a= new VideoPojo();
                a = statuses.get(i) ;
                VideoList.add(a);
                if (native_ads_enabled){
                    item++;
                    if (item == lines_beetween_ads ){
                        item= 0;
                        VideoList.add(new VideoPojo().setViewType(3));
                    }
                }

            }
            videoAdapter.notifyDataSetChanged();
            imageView_empty_favorite.setVisibility(View.GONE);
            recycle_view_home_favorite.setVisibility(View.VISIBLE);
        }else{
            imageView_empty_favorite.setVisibility(View.VISIBLE);
            recycle_view_home_favorite.setVisibility(View.GONE);
        }

        swipe_refreshl_home_favorite.setRefreshing(false);

    }
*/
}