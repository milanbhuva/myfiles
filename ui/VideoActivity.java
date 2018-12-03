package com.fourarc.videostatus.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.config.Config;
import com.fourarc.videostatus.manager.PrefManager;
import com.fourarc.videostatus.ui.fragement.VideoFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;


public class VideoActivity extends AppCompatActivity {

    private int status_id;
    String id_status;
    private int user_id;
    private String user_name;
    private String user_profile_picture;
    private String created;
    private int status_view;
    private String comments ;
    private String status_review ;
    private int copied = 0;
    private String from;
    private String title;
    private String status_image;
    private String type;
    private String status_data;
    String isfavourite;
    private String image;
    private String extension;
    private int downloads;
    private String tags;
    private boolean review;

    private int status_like;
    private int status_love;
    private int status_angry;
    private int status_haha;
    private int status_wow;
    private int status_sad;

    private String status_like1;
    private String status_love1;
    private String status_angry1;
    private String status_haha1;
    private String status_wow1;
    private String status_sad1;


    private ViewPager main_view_pager;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private ViewPagerAdapter adapter;
    private PrefManager prefManager;
    private String language;
    private String local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Bundle bundle = getIntent().getExtras();
        this.from =  bundle.getString("from");
        this.id_status = bundle.getString("id");
        this.title = bundle.getString("title");
        this.status_image = bundle.getString("thumbnail");
        this.user_id = bundle.getInt("userid");
        this.user_name = bundle.getString("user");
        this.user_profile_picture = bundle.getString("userimage");
        this.type = bundle.getString("type");
        this.status_data = bundle.getString("video");
        this.image = bundle.getString("image");
        this.isfavourite=bundle.getString("isfavourite");
        this.extension = bundle.getString("extension");
        this.status_review=bundle.getString("statusreview");
        //this.comment = bundle.getBoolean("comment");
        this.downloads = bundle.getInt("downloads");
        this.tags = bundle.getString("tags");
        this.review = bundle.getBoolean("review");
        this.comments = bundle.getString("comments");
        this.created = bundle.getString("created");
        this.local = bundle.getString("local");

        this.status_wow = bundle.getInt("woow");
        this.status_wow1 = bundle.getString("woow1");
        this.status_like = bundle.getInt("like");
        this.status_like1 = bundle.getString("like1");
        this.status_love = bundle.getInt("love");
        this.status_love1 = bundle.getString("love1");
        this.status_angry = bundle.getInt("angry");
        this.status_angry1 = bundle.getString("angry1");
        this.status_sad = bundle.getInt("sad");
        this.status_sad1 = bundle.getString("sad1");

        this.status_haha = bundle.getInt("haha");
        this.status_haha1 = bundle.getString("haha1");



        this.prefManager= new PrefManager(getApplicationContext());
        this.language=prefManager.getString("LANGUAGE_DEFAULT");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        if (!Config.REALTED_VIDEO_BOTTOM){
            //loadMore();
        }
        showAdsBanner();
    }

/*
    private void loadMore() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<VideoPojo>> call = service.getvideo(Constant_store.c_Key, Constant_store.c_Secret);
        call.enqueue(new Callback<List<VideoPojo>>() {
            @Override
            public void onResponse(Call<List<VideoPojo>> call, Response<List<VideoPojo>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i < response.body().size(); i++) {
                        if (status_id!=response.body().get(i).getStatusId()) {
                            VideoFragment videoFragment = new VideoFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", response.body().get(i).getStatusId());
                            bundle.putString("title", response.body().get(i).getStatusTitle());
                            bundle.putString("thumbnail", response.body().get(i).getStatusImage());
                            bundle.putInt("userid", response.body().get(i).getUserId());
                            bundle.putString("user", response.body().get(i).getUserName());
                            bundle.putString("userimage", response.body().get(i).getUserProfilePicture());
                           // bundle.putString("type", response.body().get(i).getType());
                            bundle.putString("video", response.body().get(i).getStatusData());
                           // bundle.putString("image", response.body().get(i).g());
                           // bundle.putString("extension", response.body().get(i).getExtension());
                            //bundle.putBoolean("comment", response.body().get(i).getComment());
                           // bundle.putInt("downloads", response.body().get(i).getDownloads());
                            //bundle.putString("tags", response.body().get(i).getTags());
                            bundle.putBoolean("review", response.body().get(i).getStatusReview());
                            bundle.putInt("comments", response.body().get(i).getStatusComments());
                            //bundle.putString("created", response.body().get(i).getCreated());

                            bundle.putInt("woow", response.body().get(i).getStatusWow());
                            bundle.putInt("like", response.body().get(i).getStatusLike());
                            bundle.putInt("love", response.body().get(i).getStatusLove());
                            bundle.putInt("angry", response.body().get(i).getStatusAngry());
                            bundle.putInt("sad", response.body().get(i).getStatusSad());
                            bundle.putInt("haha", response.body().get(i).getStatusHaha());
                            bundle.putString("from", "sub" );

                            videoFragment.setArguments(bundle);
                            adapter.addFragment(videoFragment);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<VideoPojo>> call, Throwable t) {

            }


        });
    }
*/

    private void initView() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        this.main_view_pager = (ViewPager) findViewById(R.id.main_view_pager);
        main_view_pager.setOffscreenPageLimit(0);
        main_view_pager.setAdapter(adapter);


      /*  VideoFragment imageFragment =  new VideoFragment();
        Bundle bundle = new Bundle();


        Toast.makeText(this, "from initView"+id, Toast.LENGTH_LONG).show();
        imageFragment.setArguments(bundle);

        adapter.addFragment(imageFragment);*/
        Bundle bundle = new Bundle();
        bundle.putString("id", id_status);
        bundle.putString("title",title);
        bundle.putString("thumbnail", status_image);
        bundle.putInt("userid", user_id);
        bundle.putString("user", user_name);
        bundle.putString("userimage", user_profile_picture);
        bundle.putString("type",type);
        bundle.putString("video", status_data);
        bundle.putString("image", image);
        bundle.putString("extension", extension);
      //  bundle.putBoolean("comment",comment);
        bundle.putInt("downloads",downloads);
        bundle.putString("isfavourite",isfavourite);
        bundle.putString("tags", tags);
        bundle.putBoolean("review",review);
        bundle.putString("comments",comments);
        bundle.putString("created",created);
        bundle.putString("local",local);
        bundle.putString("statusreview",status_review);

        bundle.putString("woow1", status_wow1);
        bundle.putString("like1", status_like1);
        bundle.putString("love1", status_love1);
        bundle.putString("angry1",status_angry1);
        bundle.putString("sad1", status_sad1);
        bundle.putString("haha1", status_haha1);


        VideoFragment fragobj = new VideoFragment();
        fragobj.setArguments(bundle);
        adapter.addFragment(fragobj);

        adapter.notifyDataSetChanged();
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);

        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);

        }

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
}
