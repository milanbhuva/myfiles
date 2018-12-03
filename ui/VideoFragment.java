package com.fourarc.videostatus.ui.fragement;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.fourarc.videostatus.App;
import com.fourarc.videostatus.R;
import com.fourarc.videostatus.adapter.CommentAdapter;
import com.fourarc.videostatus.adapter.VideoAdapter;
import com.fourarc.videostatus.api.apiClient;
import com.fourarc.videostatus.api.apiRest;
import com.fourarc.videostatus.config.CategoryPojo;
import com.fourarc.videostatus.config.Config;
import com.fourarc.videostatus.config.GetCommPojo;
import com.fourarc.videostatus.config.VideoPojo;
import com.fourarc.videostatus.entity.ApiResponse;
import com.fourarc.videostatus.entity.Comment;
import com.fourarc.videostatus.entity.Language;
import com.fourarc.videostatus.manager.DownloadStorage;
import com.fourarc.videostatus.manager.FavoritesStorage;
import com.fourarc.videostatus.manager.PrefManager;
import com.fourarc.videostatus.network.Constant_store;
import com.fourarc.videostatus.network.NetworkConnection;
import com.fourarc.videostatus.network.ServiceHandler;
import com.fourarc.videostatus.network.Utility;
import com.fourarc.videostatus.ui.LoginActivity;
import com.fourarc.videostatus.ui.PermissionActivity;
import com.fourarc.videostatus.ui.UserActivity;
import com.fourarc.videostatus.ui.VideoActivity;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.leo.simplearcloader.SimpleArcLoader;
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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
public class VideoFragment extends Fragment {
    private int status_id;
    String id_status;
    private String status_title;
    private String extension;
    private int user_id;
    private String user_name;
    private String user_profile_picture;
    private String status_view,status_review;
     String isfavourite;
    private String status_data;
    private String status_image;
    private int status_comments;
    private Boolean downloading =false;
    private int downloads;
    String downloadUrl;
    public static ArrayList<VideoPojo> arrayList_lates = new ArrayList<>();

    private boolean review;
    private List<VideoPojo> VideoList =new ArrayList<>();
    ArrayList<VideoPojo> arrayList_local = new ArrayList<>();
    public static final String YOUTUBE_VIDEO_CODE = "_oEA18Y8gM0";
    int like=0, totallike;
    String total;
    private String status_love;
    private String status_angry;
    private String status_sad;
    private String status_haha;
    private String status_wow;
    private String status_like;
    Integer liketext;

    private int status_love1;
    private int status_angry1;
    private int status_sad1;
    private int status_haha1;
    private int status_wow1;
    private int status_like1;

    private int love1;
    private int angry1;
    private int sad1;
    private int haha1;
    private int wow1;
    private int like1;

    int page_no = 1;
    int last;

    private String type;
    private String local;
    private String path;
    String email;

    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean noData = false;
    ProgressDialog prgDialog;

    private CircleImageView circle_image_view_fragement_video_user;
    private TextView text_view_fragement_video_title;
    private TextView text_view_fragement_video_name_user;
    private String created;
    private LikeButton like_button_whatsapp_fragement_video;
    private LikeButton like_button_messenger_fragement_video;
    private LikeButton like_button_twitter_fragement_video;
    private LikeButton like_button_snapshat_fragement_video;
    private LikeButton like_button_hike_fragement_video;
    private LikeButton like_button_fav_fragement_video;
    private LikeButton like_button_copy_fragement_video;
    private LikeButton like_botton_share_fragement_video;

    String likereaction;
    private static final String COPY_ID="com.android.copy";

    private static final String WHATSAPP_ID="com.whatsapp";
    private static final String FACEBOOK_ID="com.facebook.katana";
    private static final String MESSENGER_ID="com.facebook.orca";
    private static final String INSTAGRAM_ID="com.instagram.android";
    private static final String SHARE_ID="com.android.all";
    private static final String DOWNLOAD_ID="com.android.download";
    private static final String TWITTER_ID="com.twitter.android";
    private static final String SNAPSHAT_ID="com.snapchat.android";
    private static final String HIKE_ID="com.bsb.hike";


    private PrefManager prefManager;
    private String language = "0";
    private Object from;
    private InterstitialAd mInterstitialAdDownload;
    private int open_action;


    String likk;
    private TextView text_view_wallpaper_comments_count;
    private RelativeLayout relative_layout_comment_section;
    private EditText edit_text_comment_add;
    private ProgressBar progress_bar_comment_add;
    private ProgressBar progress_bar_comment_list;
    private ImageView image_button_comment_add;
    private RecyclerView recycle_view_comment;
    private ImageView imageView_empty_comment;
    private String comments;
    private LikeButton like_button_comments_wallpaper;
    private CardView card_view_wallpaper_comments;
    private boolean comment;


    private ArrayList<GetCommPojo> commentList= new ArrayList<>();
    private CommentAdapter commentAdapter;
    private LinearLayoutManager linearLayoutManagerCOmment;
    private RelativeLayout relative_layout_dialog_top,emojis,social;
    private Button button_follow_user_activity;
    private RelativeLayout relative_layout_main,more;
    private ScrollView scroll_view_main;
    private CardView card_view_fragement_video;
    private CardView card_view_fragement_video_share;
    private View view;
    private TextView text_view_comment_box_count;
    private ImageView image_view_comment_box_close;
    private TextView text_view_sad_fragement_video;
    private TextView text_view_angry_fragement_video;
    private TextView text_view_haha_fragement_video;
    private TextView text_view_love_fragement_video;
    private TextView text_view_like_fragement_video;
    private TextView text_view_woow_fragement_video;
    private LikeButton like_button_sad_fragement_video;
    private LikeButton like_button_angry_fragement_video;
    private LikeButton like_button_woow_fragement_video;
    private LikeButton like_button_like_fragement_video;
    private LikeButton like_button_haha_fragement_video;
    private LikeButton like_button_love_fragement_video;

    private RelativeLayout relative_layout_progress_fragement_video;
    private TextView text_view_progress_fragement_video;
    private ProgressBar progress_bar_fragement_video;
    private LikeButton like_button_instagram_fragement_video;
    private LikeButton like_button_download_fragement_video;
    private LikeButton like_button_facebook_fragement_video;
    private LinearLayout linear_layout_reactions_loading,linear_layout_control_image_fragment;
    private int strtext;
    String commenttt;
    private RelativeLayout morevideos;
    private YouTubePlayerView youTubeView;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;
    private DataSource.Factory mediaDataSourceFactory;
    private ImageView ivHideControllerButton;
    private Timeline.Window window;
    private ImageView image_view_load_video_item;
    private RelativeLayout relative_layout_fragement_video_thum;
    private ImageView image_view_video_fragement_video;
    private SimpleArcLoader simple_arc_loader_lang_player;


    private List<VideoPojo> videoList =new ArrayList<>();
    private List<CategoryPojo> categoryList =new ArrayList<>();
    private VideoAdapter videoAdapter;
    private PeekAndPop peekAndPop;
//String  link = getActivity().getIntent().getStringExtra("status_video_id");

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recycler_view_status_load_more;

    public VideoFragment() {
        // Required empty public constructor
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
        {
            if (!isVisibleToUser)   // If we are becoming invisible, then...
            {
                if (Util.SDK_INT <= 23) {
                    releasePlayer();
                }
            }

            if (isVisibleToUser)
            {
                if (Util.SDK_INT <= 23) {
                    releasePlayer();
                }
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        checkPermission();
        loadLang();

        this.view =  inflater.inflate(R.layout.fragment_video, container, false);
        Bundle bundle =  this.getArguments();

        nw = new NetworkConnection(getActivity());
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setCancelable(false);


        this.from =  bundle.getString("from");


        this.status_id = bundle.getInt("id");
        id_status = bundle.getString("id");
        commenttt = bundle.getString("comments");
        likk = bundle.getString("like");
        this.local = bundle.getString("local");
        this.status_title = bundle.getString("title");
        Utility.setStringSharedPreference(getApplicationContext(), Constant_store.statustitle, status_title);

        this.status_image = bundle.getString("thumbnail");
        this.user_id = bundle.getInt("userid");
        this.user_name = bundle.getString("user");
        this.user_profile_picture = bundle.getString("userimage");
        this.status_review=bundle.getString("statusreview");
        this.type = bundle.getString("type");
        this.status_data = bundle.getString("video");
        Utility.setStringSharedPreference(getApplicationContext(), Constant_store.statusdata, status_data);
        //his.image = bundle.getString("image");
        //this.extension = bundle.getString("extension");
        //this.comment = bundle.getBoolean("comment");
        this.downloads = bundle.getInt("downloads");
        //this.tags = bundle.getString("tags");
        this.review = bundle.getBoolean("review");
        this.status_comments = bundle.getInt("comments");
        this.created = bundle.getString("created");

        this.status_wow = bundle.getString("woow1");
        this.status_like = bundle.getString("like1");
        this.status_love = bundle.getString("love1");
            isfavourite=bundle.getString("isfavourite");
        this.status_angry = bundle.getString("angry1");
        this.status_sad = bundle.getString("sad1");
        this.status_haha = bundle.getString("haha1");

        status_wow1= Integer.parseInt(status_wow);
        status_like1= Integer.parseInt(status_like);
        status_love1= Integer.parseInt(status_love);
        status_angry1= Integer.parseInt(status_angry);
        status_sad1= Integer.parseInt(status_sad);
        status_haha1= Integer.parseInt(status_haha);

        wow1=status_wow1;
        like1=status_like1;
        love1=status_love1;
        angry1=status_angry1;
        sad1=status_sad1;
        haha1=status_haha1;










        this.prefManager= new PrefManager(getActivity().getApplicationContext());
        this.language=prefManager.getString("LANGUAGE_DEFAULT");




        initView();
        initAction();
        initInterstitialAdPrepare();
        initStatus();
        getUser();
        setReaction(prefManager.getString("reaction_"+status_id));
        if (Config.REALTED_VIDEO_BOTTOM){
            //loadMore();

        new GetStatusOperation2().execute();
        }
        initAds();
        youTubeView = (YouTubePlayerView) view.findViewById(R.id.video_view);

        youTubeView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();


                        initializedYouTubePlayer.loadVideo(status_data,0);

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

                        ytEx.execute("https://www.youtube.com/watch?v="+status_data);

                    }
                });
            }


        }, true);


            return view;


    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override //reconfigure display properties on screen rotation
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // handle change here
            relative_layout_dialog_top.setVisibility(View.VISIBLE);
            linear_layout_control_image_fragment.setVisibility(View.VISIBLE);
            card_view_fragement_video.setVisibility(View.VISIBLE);
            morevideos.setVisibility(View.VISIBLE);
            emojis.setVisibility(View.VISIBLE);
            social.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams playerParams =
                    (RelativeLayout.LayoutParams) youTubeView.getLayoutParams();
            // or here
            playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;

//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            relative_layout_dialog_top.setVisibility(View.GONE);
            card_view_fragement_video.setVisibility(View.GONE);
            linear_layout_control_image_fragment.setVisibility(View.GONE);
            morevideos.setVisibility(View.GONE);
            emojis.setVisibility(View.GONE);
            social.setVisibility(View.GONE);
            more.setVisibility(View.GONE);

//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            RelativeLayout.LayoutParams playerParams =
                    (RelativeLayout.LayoutParams) youTubeView.getLayoutParams();
            // or here
            playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;

        }
    }
    private void initializePlayer() {


        //simpleExoPlayerView.requestFocus();
        youTubeView.setVisibility(View.VISIBLE);
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        youTubeView.getPlayerUIController().showYouTubeButton(false);
        //youTubeView.addFullScreenListener((YouTubePlayerFullScreenListener) getActivity());
        youTubeView.getPlayerUIController().setVideoTitle(status_title);




        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

       //simpleExoPlayerView.setPlayer(player);

        player.setPlayWhenReady(shouldAutoPlay);
/*        MediaSource mediaSource = new HlsMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),
                mediaDataSourceFactory, mainHandler, null);*/

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(status_data),
                mediaDataSourceFactory, extractorsFactory, null, null);

        if (local!=null){
            Log.v("this is path",local);
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(local));
            mediaSource = new ExtractorMediaSource(imageUri,
                    mediaDataSourceFactory, extractorsFactory, null, null);
        }
        player.prepare(mediaSource);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == ExoPlayer.STATE_READY){
                        simple_arc_loader_lang_player.setVisibility(View.GONE);
                    }
                    if (playbackState == ExoPlayer.STATE_BUFFERING){
                        simple_arc_loader_lang_player.setVisibility(View.VISIBLE);
                    }
                }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
        /*ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(getActivity(),FullscreenActivity.class);
                intent.putExtra("duration", player.getContentPosition()+"");
                intent.putExtra("video",video);
                startActivity(intent);

            }
        });*/

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

              //  linear_layout_page_error.setVisibility(View.VISIBLE);
              //  relative_layout_load_more.setVisibility(View.GONE);
            } else {


                if (status.equalsIgnoreCase("true")) {


                    recycler_view_status_load_more.setVisibility(View.VISIBLE);
                   // linear_layout_load_status_fragment.setVisibility(View.GONE);
                    //relative_layout_load_more.setVisibility(View.GONE);
                    //linear_layout_page_error.setVisibility(View.GONE);
                   // linear_layout_page_error.setVisibility(View.GONE);
                  //  swipe_refreshl_status_fragment.setRefreshing(false);

                    videoAdapter = new VideoAdapter(arrayList_lates, null, getActivity(), peekAndPop);
                    recycler_view_status_load_more.setHasFixedSize(true);
                    recycler_view_status_load_more.setAdapter(videoAdapter);

                    videoAdapter.notifyDataSetChanged();
                    recycler_view_status_load_more.setNestedScrollingEnabled(false);
                    /*videoAdapter.notifyDataSetChanged();*/


                } else {

                }

            }
            super.onPostExecute(result);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
     /*   if (Util.SDK_INT > 23) {
            initializePlayer();
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
       /* if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
    private void initStatus() {


        Picasso.with(getActivity().getApplicationContext()).load(status_image).error(R.drawable.bg_transparant).placeholder(R.drawable.bg_transparant).into(this.image_view_video_fragement_video);
        Picasso.with(getActivity().getApplicationContext()).load(user_profile_picture).error(R.drawable.profile).placeholder(R.drawable.profile).into(this.circle_image_view_fragement_video_user);
        this.text_view_fragement_video_title.setText(status_title );
        this.text_view_fragement_video_name_user.setText(user_name);
        this.text_view_wallpaper_comments_count.setText(commenttt+"");


        if (this.comment){
            relative_layout_comment_section.setVisibility(View.VISIBLE);
        }else{
            relative_layout_comment_section.setVisibility(View.GONE);
        }


        this.text_view_wallpaper_comments_count.setText(commenttt+"");
        this.text_view_comment_box_count.setText(commenttt+" "+getActivity().getResources().getString(R.string.comments));
        if (this.comment){
            relative_layout_comment_section.setVisibility(View.VISIBLE);
        }else{
            relative_layout_comment_section.setVisibility(View.GONE);
        }

        this.text_view_like_fragement_video.setText(format(status_like1));
        this.text_view_love_fragement_video.setText(format(status_love1));
        this.text_view_angry_fragement_video.setText(format(status_angry1));
        this.text_view_haha_fragement_video.setText(format(status_haha1));
        this.text_view_woow_fragement_video.setText(format(status_wow1));
        this.text_view_sad_fragement_video.setText(format(status_sad1));



    }

    private void initView() {

        this.recycler_view_status_load_more=(RecyclerView) view.findViewById(R.id.recycler_view_status_load_more);
        this.peekAndPop = new PeekAndPop.Builder(getActivity())
                .parentViewGroupToDisallowTouchEvents(recycler_view_status_load_more)
                .peekLayout(R.layout.dialog_view)
                .build();
        this.linearLayoutManager=  new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        videoAdapter =new VideoAdapter(videoList,categoryList,getActivity(),peekAndPop);
        recycler_view_status_load_more.setHasFixedSize(true);
        recycler_view_status_load_more.setAdapter(videoAdapter);
        recycler_view_status_load_more.setLayoutManager(linearLayoutManager);


        this.simple_arc_loader_lang_player=(SimpleArcLoader) view.findViewById(R.id.simple_arc_loader_lang_player);
        this.image_view_video_fragement_video=(ImageView) view.findViewById(R.id.image_view_video_fragement_video);
        this.image_view_load_video_item=(ImageView) view.findViewById(R.id.image_view_load_video_item);
        this.relative_layout_fragement_video_thum=(RelativeLayout) view.findViewById(R.id.relative_layout_fragement_video_thum);
        ivHideControllerButton = (ImageView) view.findViewById(R.id.exo_controller);
        this.morevideos=view.findViewById(R.id.morevideos);
        this.linear_layout_reactions_loading=(LinearLayout) view.findViewById(R.id.linear_layout_reactions_loading);
        this.like_button_download_fragement_video=(LikeButton) view.findViewById(R.id.like_button_download_fragement_video);
        this.like_button_facebook_fragement_video=(LikeButton) view.findViewById(R.id.like_button_facebook_fragement_video);
        this.like_button_instagram_fragement_video=(LikeButton) view.findViewById(R.id.like_button_instagram_fragement_video);
        this.progress_bar_fragement_video=(ProgressBar) view.findViewById(R.id.progress_bar_fragement_video);
        this.text_view_progress_fragement_video=(TextView) view.findViewById(R.id.text_view_progress_fragement_video);
        this.relative_layout_progress_fragement_video=(RelativeLayout) view.findViewById(R.id.relative_layout_progress_fragement_video);
        this.like_button_angry_fragement_video=(LikeButton) view.findViewById(R.id.like_button_angry_fragement_video);
        this.like_button_like_fragement_video=(LikeButton) view.findViewById(R.id.like_button_like_fragement_video);
        this.like_button_love_fragement_video=(LikeButton) view.findViewById(R.id.like_button_love_fragement_video);
        this.like_button_sad_fragement_video=(LikeButton) view.findViewById(R.id.like_button_sad_fragement_video);
        this.like_button_woow_fragement_video=(LikeButton) view.findViewById(R.id.like_button_woow_fragement_video);
        this.like_button_haha_fragement_video=(LikeButton) view.findViewById(R.id.like_button_haha_fragement_video);

        this.text_view_sad_fragement_video=(TextView) view.findViewById(R.id.text_view_sad_fragement_video);
        this.text_view_angry_fragement_video=(TextView) view.findViewById(R.id.text_view_angry_fragement_video);
        this.text_view_haha_fragement_video=(TextView) view.findViewById(R.id.text_view_haha_fragement_video);
        this.text_view_love_fragement_video=(TextView) view.findViewById(R.id.text_view_love_fragement_video);
        this.text_view_like_fragement_video=(TextView) view.findViewById(R.id.text_view_like_fragement_video);
        this.text_view_woow_fragement_video=(TextView) view.findViewById(R.id.text_view_woow_fragement_video);
        this.image_view_comment_box_close=(ImageView) view.findViewById(R.id.image_view_comment_box_close);
        this.more=view.findViewById(R.id.more);
        this.text_view_comment_box_count=(TextView) view.findViewById(R.id.text_view_comment_box_count);
        this.scroll_view_main=(ScrollView) view.findViewById(R.id.scroll_view_main);
        this.relative_layout_main=(RelativeLayout) view.findViewById(R.id.relative_layout_main);
        this.like_button_whatsapp_fragement_video=(LikeButton) view.findViewById(R.id.like_button_whatsapp_fragement_video);
        this.like_button_messenger_fragement_video=(LikeButton) view.findViewById(R.id.like_button_messenger_fragement_video);
        this.like_button_twitter_fragement_video=(LikeButton) view.findViewById(R.id.like_button_twitter_fragement_video);
        this.like_button_snapshat_fragement_video=(LikeButton) view.findViewById(R.id.like_button_snapshat_fragement_video);
        this.like_button_hike_fragement_video=(LikeButton) view.findViewById(R.id.like_button_hike_fragement_video);

        this.like_button_fav_fragement_video=(LikeButton) view.findViewById(R.id.like_button_fav_fragement_video);
        this.like_botton_share_fragement_video=(LikeButton) view.findViewById(R.id.like_botton_share_fragement_video);

        this.text_view_fragement_video_name_user=(TextView) view.findViewById(R.id.text_view_fragement_video_name_user);
        this.text_view_fragement_video_title=(TextView) view.findViewById(R.id.text_view_fragement_video_title);
        this.circle_image_view_fragement_video_user=(CircleImageView) view.findViewById(R.id.circle_image_view_fragement_video_user);

        this.linear_layout_control_image_fragment=view.findViewById(R.id.linear_layout_control_image_fragment);

        this.linearLayoutManager=  new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            this.card_view_fragement_video=view.findViewById(R.id.card_view_fragement_video);

        this.linearLayoutManagerCOmment=  new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        this.text_view_wallpaper_comments_count=(TextView) view.findViewById(R.id.text_view_wallpaper_comments_count);
        this.relative_layout_comment_section=(RelativeLayout) view.findViewById(R.id.relative_layout_comment_section);
        this.edit_text_comment_add=(EditText) view.findViewById(R.id.edit_text_comment_add);
        this.progress_bar_comment_add=(ProgressBar) view.findViewById(R.id.progress_bar_comment_add);
        this.progress_bar_comment_list=(ProgressBar) view.findViewById(R.id.progress_bar_comment_list);
        this.image_button_comment_add=(ImageView) view.findViewById(R.id.image_button_comment_add);
        this.recycle_view_comment=(RecyclerView) view.findViewById(R.id.recycle_view_comment);
        this.commentAdapter = new CommentAdapter(commentList, getActivity().getApplication());
        this.recycle_view_comment.setHasFixedSize(true);
        this.recycle_view_comment.setAdapter(commentAdapter);
        this.recycle_view_comment.setLayoutManager(linearLayoutManagerCOmment);
        this.imageView_empty_comment=(ImageView) view.findViewById(R.id.imageView_empty_comment);
        this.like_button_comments_wallpaper=(LikeButton) view.findViewById(R.id.like_botton_comment_activity_gif);
        this.card_view_wallpaper_comments=(CardView) view.findViewById(R.id.card_view_wallpaper_comments);
        image_button_comment_add.setEnabled(false);


        this.button_follow_user_activity=(Button) view.findViewById(R.id.button_follow_user_activity);
        this.relative_layout_dialog_top=(RelativeLayout) view.findViewById(R.id.relative_layout_dialog_top);
        this.emojis=view.findViewById(R.id.emojis);
        this.social=view.findViewById(R.id.social);

        final FavoritesStorage storageFavorites= new FavoritesStorage(getActivity().getApplicationContext());

      /*  List<VideoPojo> favorites_list = storageFavorites.loadImagesFavorites();
        Boolean exist = false;
        if (favorites_list==null){
            favorites_list= new ArrayList<>();
        }
        for (int i = 0; i <favorites_list.size() ; i++) {
            if (favorites_list.get(i).getStatusId()==(status_id)){
                exist = true;
            }
        }*/
       /* if (exist  == false) {
            like_button_fav_fragement_video.setLiked(false);
        }else{
            like_button_fav_fragement_video.setLiked(true);
        }*/

       if (isfavourite.equals("1"))
       {
           like_button_fav_fragement_video.setLiked(true);
       }
       else
       {
           like_button_fav_fragement_video.setLiked(false);
       }

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(getActivity().getApplicationContext(), Util.getUserAgent(getActivity().getApplicationContext(), "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        window = new Timeline.Window();
        ivHideControllerButton = (ImageView) view.findViewById(R.id.exo_controller);

        progress_bar_comment_list.setVisibility(View.VISIBLE);
        recycle_view_comment.setVisibility(View.GONE);
        imageView_empty_comment.setVisibility(View.GONE);
    }

    private void initAction() {
        this.image_view_load_video_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative_layout_fragement_video_thum.setVisibility(View.GONE);
                initializePlayer();

            }
        });
        this.image_view_load_video_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative_layout_fragement_video_thum.setVisibility(View.GONE);
                initializePlayer();
            }
        });
        this.like_button_like_fragement_video.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                //addLike(status_id);
                likereaction="like";
                liketext= status_like1;
                like = Integer.parseInt(status_like);
                totallike = (like + 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(total);

                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(status_sad);
                new addlikes().execute();
                setReaction("like");



            }

            @Override
            public void unLiked(LikeButton likeButton) {
                //deleteLike(status_id);
                likereaction="like";
                liketext= status_like1;
                like = Integer.parseInt(total);
                totallike = (like - 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(total);

                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(status_sad);
                new adddislikes().execute();
                setReaction("none");



                prefManager.setString("reaction_"+52,"none");
            }
        });

        this.like_button_love_fragement_video.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                //addLove(status_id);

                likereaction="love";
                liketext= status_love1;
                like = Integer.parseInt(status_love);
                totallike = (like + 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(total);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(status_sad);

                new addlikes().execute();
                setReaction("love");
               // text_view_love_fragement_video.setText(format(status_love1+1));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                //deleteLove(status_id);
                likereaction="love";
                liketext= status_love1;
                like = Integer.parseInt(total);
                totallike = (like - 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(total);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(status_sad);
                new adddislikes().execute();
               // text_view_love_fragement_video.setText(format(status_love1-1));
                setReaction("none");

                prefManager.setString("reaction_"+52,"none");
            }
        });
        this.like_button_woow_fragement_video.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                likereaction="wow";
                liketext= status_wow1;
                like = Integer.parseInt(status_wow);
                totallike = (like + 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(total);
                text_view_sad_fragement_video.setText(status_sad);
                new addlikes().execute();
                setReaction("woow");
              //  text_view_woow_fragement_video.setText(format(status_wow1+1));
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                //deleteWoow(status_id);

                likereaction="wow";
                liketext= status_wow1;
                like = Integer.parseInt(total);
                totallike = (like - 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(total);
                text_view_sad_fragement_video.setText(status_sad);
                new adddislikes().execute();
               // text_view_woow_fragement_video.setText(format(status_wow1-1));

                setReaction("none");
                prefManager.setString("reaction_"+52,"none");
            }
        });

        this.like_button_angry_fragement_video.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                //addAngry(status_id);

                likereaction="angry";
                liketext= status_angry1;
                like = Integer.parseInt(status_angry);
                totallike = (like + 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(total);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(status_sad);
                new addlikes().execute();
                setReaction("angry");
              ///  text_view_angry_fragement_video.setText(format(status_angry1+1));

            }

            @Override
            public void unLiked(LikeButton likeButton) {
               //deleteAngry(status_id);

                likereaction="angry";
                liketext= status_angry1;
                like = Integer.parseInt(total);
                totallike = (like - 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(total);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(status_sad);

                new adddislikes().execute();
                //text_view_angry_fragement_video.setText(format(status_angry1-1));

                setReaction("none");
                prefManager.setString("reaction_"+52,"none");
            }
        });
        this.like_button_sad_fragement_video.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
               // addSad(status_id);
                likereaction="sad";
                liketext= status_sad1;
                like = Integer.parseInt(status_sad);
                totallike = (like + 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(total);

                new addlikes().execute();
                setReaction("sad");
               text_view_sad_fragement_video.setText(format(status_sad1+1));

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                //deleteSad(status_id);
                likereaction="sad";
                liketext= status_sad1;
                like = Integer.parseInt(total);
                totallike = (like - 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(status_haha);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(total);
                new adddislikes().execute();
             //   text_view_sad_fragement_video.setText(format(status_sad1-1));

                setReaction("none");
                prefManager.setString("reaction_"+52,"none");
            }
        });
        this.like_button_haha_fragement_video.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                ///addHaha(status_id);
                likereaction="haha";
                liketext= status_haha1;

                like = Integer.parseInt(status_haha);
                totallike = (like + 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(total);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(status_sad);
              //  text_view_haha_fragement_video.setText(format(status_haha1+1));
                new addlikes().execute();
                setReaction("haha");
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                //deleteHaha(status_id);
                likereaction="haha";
                liketext= status_haha1;
                like = Integer.parseInt(total);
                totallike = (like - 1);
                total = String.valueOf(totallike);
                text_view_like_fragement_video.setText(status_like);
                text_view_love_fragement_video.setText(status_love);
                text_view_angry_fragement_video.setText(status_angry);
                text_view_haha_fragement_video.setText(total);
                text_view_woow_fragement_video.setText(status_wow);
                text_view_sad_fragement_video.setText(status_sad);

                new adddislikes().execute();
             //   text_view_haha_fragement_video.setText(format(status_haha1-1));

                setReaction("none");
                prefManager.setString("reaction_"+52,"none");
            }
        });
        this.image_view_comment_box_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentBox();
            }
        });
        this.button_follow_user_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                follow();
            }
        });
        this.relative_layout_dialog_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),UserActivity.class);
                intent.putExtra("id",user_id);
                intent.putExtra("name",user_name);
                intent.putExtra("image",user_profile_picture);
                startActivity(intent);
            }
        });
        this.like_button_whatsapp_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_button_whatsapp_fragement_video.setLiked(false);


                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5001;
                    } else {
                        if (!downloading) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,0,WHATSAPP_ID);
                            else
                                new DownloadFileFromURL().execute(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,0,WHATSAPP_ID);*/
                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Download the No.1 and original VIDEO STATUS APP with New & Fresh Quotes \uD83D\uDE07.\n" + "https://goo.gl/3vUFAM";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            //File file = new File(getApplicationContext().getFilesDir().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(WHATSAPP_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,WHATSAPP_ID);
                            }


                        }
                    }
                }else{
                    if (!downloading) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,0,WHATSAPP_ID);
                        else
                            new DownloadFileFromURL().execute(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,0,WHATSAPP_ID);

                    */

                        Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video/*");

                        String downloadurl = "Download the No.1 and original VIDEO STATUS APP with New & Fresh Quotes \uD83D\uDE07.\n" + "https://goo.gl/3vUFAM";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        //File file = new File(getApplicationContext().getFilesDir().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(WHATSAPP_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,WHATSAPP_ID);
                        }

                    }
                }

            }
        });
        this.like_button_whatsapp_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_button_whatsapp_fragement_video.setLiked(false);


                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5001;
                    } else {



                        Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video/*");

                        String downloadurl = "Download the No.1 and original VIDEO STATUS APP with New & Fresh Quotes \uD83D\uDE07.\n" + "https://goo.gl/3vUFAM";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        //File file = new File(getApplicationContext().getFilesDir().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(WHATSAPP_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,WHATSAPP_ID);
                        }
                        /*if (!downloading) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,0,WHATSAPP_ID);
                            else
                                new DownloadFileFromURL().execute(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,0,WHATSAPP_ID);
                        }*/
                    }
                }else{
                    if (!downloading) {
                /*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,0,WHATSAPP_ID);
                        else
                            new DownloadFileFromURL().execute(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,0,WHATSAPP_ID);

                   */
                        Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video/*");

                        String downloadurl = "Download the No.1 and original VIDEO STATUS APP with New & Fresh Quotes \uD83D\uDE07.\n" + "https://goo.gl/3vUFAM";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        //File file = new File(getApplicationContext().getFilesDir().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(WHATSAPP_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,WHATSAPP_ID);
                        }
                    }
                }
            }
        });
        this.like_button_messenger_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_button_messenger_fragement_video.setLiked(false);





                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5002;
                    } else {
                        if (!downloading) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,MESSENGER_ID);
                            else

                                new DownloadFileFromURL().execute(status_data, status_title, 0,MESSENGER_ID);*/


                           // Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setPackage(MESSENGER_ID);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");

                           // final String final_text = getResources().getString(R.string.download_more_from_link);
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                           // shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
                           // shareIntent.putExtra(Intent.EXTRA_STREAM, uriSting);

                            shareIntent.setType("video/mp4");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";


                            try {

                                File finalfile = new File(uriSting);
                                if (finalfile.exists()) {
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, downloadurl);
                                   // shareIntent.setPackage(MESSENGER_ID);
                                    startActivity(Intent.createChooser(shareIntent, "Share video"));
                                } else {
                                    new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,MESSENGER_ID);
                                }
                                startActivity(shareIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.messenger_not_installed) , Toast.LENGTH_SHORT, true).show();
                            }


                        /*    Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video*//*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(MESSENGER_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,MESSENGER_ID);
                            }*/
                        }
                    }
                }else{
                    if (!downloading) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,MESSENGER_ID);
                        else
                            new DownloadFileFromURL().execute(status_data, status_title, 0,MESSENGER_ID);*/



                   /*     Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video*//*");

                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(MESSENGER_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,MESSENGER_ID);
                        }*/



                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setPackage(MESSENGER_ID);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");

                        // final String final_text = getResources().getString(R.string.download_more_from_link);
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        // shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
                        // shareIntent.putExtra(Intent.EXTRA_STREAM, uriSting);

                        shareIntent.setType("video/mp4");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";


                        try {

                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                shareIntent.putExtra(Intent.EXTRA_TEXT, downloadurl);
                                // shareIntent.setPackage(MESSENGER_ID);
                                startActivity(Intent.createChooser(shareIntent, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,MESSENGER_ID);
                            }
                            startActivity(shareIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.messenger_not_installed) , Toast.LENGTH_SHORT, true).show();
                        }

                    }
                }
            }
        });
        this.like_button_facebook_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_button_facebook_fragement_video.setLiked(false);


                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5003;
                    } else {
                        if (!downloading) {
                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,FACEBOOK_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, 0,FACEBOOK_ID);*/
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setPackage(FACEBOOK_ID);
                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");

                            shareIntent.setType("video/*");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            File finalfile = new File(uriSting);

                            try {

                                if (finalfile.exists()) {
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, downloadurl);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                    startActivity(shareIntent);
                                } else {
                                    new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,FACEBOOK_ID);
                                }

                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.error(getActivity().getApplicationContext(), getResources().getString(R.string.facebook_not_installed), Toast.LENGTH_SHORT, true).show();
                            }
                        }
                            /*Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video*//*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(FACEBOOK_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,FACEBOOK_ID);
                            }
                        }*/

                    }
                }else{
                    if (!downloading) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,FACEBOOK_ID);
                        else
                            new DownloadFileFromURL().execute(status_data, status_title, 0,FACEBOOK_ID);*/


                   /*     Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video*//*");

                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(FACEBOOK_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,FACEBOOK_ID);
                        }*/

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setPackage(FACEBOOK_ID);
                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");

                        shareIntent.setType("video/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        File finalfile = new File(uriSting);

                        try {

                            if (finalfile.exists()) {
                                shareIntent.putExtra(Intent.EXTRA_TEXT, downloadurl);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                startActivity(shareIntent);
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,FACEBOOK_ID);
                            }

                        } catch (android.content.ActivityNotFoundException ex) {
                            Toasty.error(getActivity().getApplicationContext(), getResources().getString(R.string.facebook_not_installed), Toast.LENGTH_SHORT, true).show();
                        }

                    }

                }
            }
        });
        this.like_button_instagram_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_button_instagram_fragement_video.setLiked(false);



                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5004;
                    } else {
                        if (!downloading) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,INSTAGRAM_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, 0,INSTAGRAM_ID);*/


                            /*Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video*//*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(INSTAGRAM_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,INSTAGRAM_ID);
                            }*/

                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setPackage(INSTAGRAM_ID);

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            // final String final_text = getResources().getString(R.string.download_more_from_link);




                            shareIntent.setType("video/*");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            try {

                                File finalfile = new File(uriSting);
                                if (finalfile.exists()) {
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                    //shareIntent.setPackage("com.instagram.android");
                                    shareIntent.putExtra(Intent.EXTRA_TEXT,downloadurl );
                                    //shareIntent.putExtra(Intent.EXTRA_STREAM, uriSting);
                                    startActivity(shareIntent);
                                } else {
                                    new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,INSTAGRAM_ID);
                                }
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.error(getActivity().getApplicationContext(), getResources().getString(R.string.instagram_not_installed) , Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    }
                }else{
                    if (!downloading) {
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,INSTAGRAM_ID);
                        else
                            new DownloadFileFromURL().execute(status_data, status_title, 0,INSTAGRAM_ID);*/


                        /*Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video*//*");

                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(INSTAGRAM_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,INSTAGRAM_ID);
                        }*/

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setPackage(INSTAGRAM_ID);

                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        // final String final_text = getResources().getString(R.string.download_more_from_link);


                        //shareIntent.putExtra(Intent.EXTRA_STREAM, uriSting);

                        shareIntent.setType("video/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        try {

                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                shareIntent.putExtra(Intent.EXTRA_TEXT,downloadurl );
                              //  shareIntent.setPackage("com.instagram.android");
                                startActivity(shareIntent);
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,INSTAGRAM_ID);
                            }
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toasty.error(getActivity().getApplicationContext(), getResources().getString(R.string.instagram_not_installed) , Toast.LENGTH_SHORT, true).show();
                        }

                    }
                }
            }
        });
        this.like_button_twitter_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_button_twitter_fragement_video.setLiked(false);



                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5005;
                    } else {
                        if (!downloading) {
                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,TWITTER_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, 0,TWITTER_ID);*/




                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(TWITTER_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,TWITTER_ID);
                            }
                        }
                    }
                }else{
                    if (!downloading) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,TWITTER_ID);
                        else
                            new DownloadFileFromURL().execute(status_data, status_title, 0,TWITTER_ID);*/


                        Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video/*");

                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(TWITTER_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,TWITTER_ID);
                        }
                    }
                }
            }
        });
        this.like_botton_share_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_botton_share_fragement_video.setLiked(false);



                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5006;
                    } else {
                        if (!downloading) {
                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,SHARE_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, extension, 0,SHARE_ID);*/


                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(SHARE_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,SHARE_ID);
                            }
                        }
                    }
                }else{
                    if (!downloading) {
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,  0,SHARE_ID);
                        else
                            new DownloadFileFromURL().execute(status_data, status_title,0,SHARE_ID);*/


                        Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video/*");

                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(SHARE_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,SHARE_ID);
                        }
                    }
                }
            }
        });
        this.like_button_download_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_button_download_fragement_video.setLiked(false);




                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5007;
                    } else {
                        if (!downloading) {
                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,DOWNLOAD_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, 0,DOWNLOAD_ID);*/

                          /*  Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video*//*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);*/

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                             /*   videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(DOWNLOAD_ID);*/
                            //    startActivity(Intent.createChooser(videoshare, "Share video"));
                                Toast.makeText(getApplicationContext(),"already downloaded",Toast.LENGTH_SHORT).show();
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,DOWNLOAD_ID);
                            }
                        }
                    }
                }else{
                    if (!downloading) {
                     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,DOWNLOAD_ID);
                        else
                            new DownloadFileFromURL().execute(status_data, status_title, 0,DOWNLOAD_ID);*/

                      /*  Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video*//*");

                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);*/

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                         /*   videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(DOWNLOAD_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));*/
                            Toast.makeText(getApplicationContext(),"already downloaded",Toast.LENGTH_SHORT).show();

                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,DOWNLOAD_ID);
                        }
                    }
                }
            }
        });

        this.like_button_hike_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_button_hike_fragement_video.setLiked(false);




                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5008;
                    } else {
                        if (!downloading) {
                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,HIKE_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, 0,HIKE_ID);*/


                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(HIKE_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,HIKE_ID);
                            }
                        }
                    }
                }else{
                    if (!downloading) {
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,HIKE_ID);
                        else
                            new DownloadFileFromURL().execute(status_data, status_title, 0,HIKE_ID);*/
                        Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video/*");

                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(HIKE_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,HIKE_ID);
                        }
                    }
                }
            }
        });
        this.like_button_snapshat_fragement_video.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                like_button_snapshat_fragement_video.setLiked(false);



                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5009;
                    } else {
                        if (!downloading) {
                          /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,SNAPSHAT_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title,0,SNAPSHAT_ID);*/

                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(SNAPSHAT_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,SNAPSHAT_ID);
                            }
                        }
                    }
                }else{
                    if (!downloading) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, 0,SNAPSHAT_ID);
                        else
                            new DownloadFileFromURL().execute(status_data, status_title, 0,SNAPSHAT_ID);*/


                        Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("video/*");

                        String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                        videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                        File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                        String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                        File finalfile = new File(uriSting);
                        if (finalfile.exists()) {
                            videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                            videoshare.setPackage(SNAPSHAT_ID);
                            startActivity(Intent.createChooser(videoshare, "Share video"));
                        } else {
                            new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,SNAPSHAT_ID);
                        }
                    }
                }
            }
        });
        this.like_button_fav_fragement_video.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
               /* if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5010;
                    } else {
                        //favorite();
                        new FavouriteOperation().execute();
                    }
                }else{
                    //favorite();
                    new FavouriteOperation().execute();
                }*/
                new FavouriteOperation().execute();
            }
            @Override
            public void unLiked(LikeButton likeButton) {
                /*if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 5010;
                    } else {
                       // favorite();
                        new FavouriteOperation().execute();
                    }
                }else{
                   // favorite();
                    new FavouriteOperation().execute();
                }*/
                new FavouriteOperation().execute();
            }
        });


        this.image_button_comment_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*addComment();*/
                new AddComm().execute();
            }
        });

        this.edit_text_comment_add.addTextChangedListener(new CommentTextWatcher(this.edit_text_comment_add));



        this.like_button_comments_wallpaper.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                like_button_comments_wallpaper.setLiked(false);
                showCommentBox();
            }
            @Override
            public void unLiked(LikeButton likeButton) {
                like_button_comments_wallpaper.setLiked(false);
                showCommentBox();
            }
        });

    }

    private class AddComm extends AsyncTask<String, Void, Void> {
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
                    json_main.put("user_id", "27");
                    json_main.put("status_id", "52");
                    json_main.put("comment_text", email);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(Constant_store.API_URL + "addcomment.php", ServiceHandler.POST, json_main);

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
                        .make(text_view_comment_box_count, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();

            } else {

                if ("true".equalsIgnoreCase(status)) {

                    progress_bar_comment_list.setVisibility(View.GONE);
                    recycle_view_comment.setVisibility(View.VISIBLE);
                    imageView_empty_comment.setVisibility(View.GONE);

                    edit_text_comment_add.setText("");
                    new getComm().execute();
                  /*  Snackbar snackbar = Snackbar
                            .make(text_view_comment_box_count, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();*/

                } else {
                    progress_bar_comment_list.setVisibility(View.GONE);
                    recycle_view_comment.setVisibility(View.VISIBLE);
                    imageView_empty_comment.setVisibility(View.GONE);

                    Snackbar snackbar = Snackbar
                            .make(text_view_comment_box_count, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                }
            }
            super.onPostExecute(result);
        }
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

                    json_main.put("status_id",id_status);

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
              /* Snackbar snackbar = Snackbar.make(button_try_again, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();
*/
            } else {

                if ("true".equalsIgnoreCase(status)) {
                   /* Snackbar snackbar = Snackbar.make(button_try_again, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();*/

                } else {
                    /*Snackbar snackbar = Snackbar.make(button_try_again, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();*/
                }
            }
            super.onPostExecute(result);
        }
    }



    private class getComm extends AsyncTask<String, Void, Void> {
        String response;
        String status, msg;

        @Override
        protected void onPreExecute() {

            commentList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(String... urls) {

            if (nw.isOnline() == true) {
                JSONObject json_main = new JSONObject();

                try {
                    json_main.put("c_key", Constant_store.c_Key);
                    json_main.put("c_secret", Constant_store.c_Secret);
                    json_main.put("user_id", "27");
                    json_main.put("status_id", "52");

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(Constant_store.API_URL + "video_comments.php", ServiceHandler.POST, json_main);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsMain = js.getJSONObject("response");
                    status = jsMain.getString("type");
                    msg = jsMain.getString("responseMsg");

                    if (status.contains("true")) {
                        JSONArray videoarray = jsMain.getJSONObject("responseData").getJSONArray("commentDetail");
                        JSONObject jsonObject = jsMain.getJSONObject("responseData");

                        if (videoarray.length() == 0) {

                        } else {
                            for (int i = 0; i < videoarray.length(); i++) {

                                JSONObject jsFinal = videoarray.getJSONObject(i);

                                GetCommPojo status = new GetCommPojo(jsFinal.getString("comment_id"), jsFinal.getString("comment_text"), jsFinal.getString("user_id"), jsFinal.getString("user_name"), jsFinal.getString("user_profile_picture"), jsFinal.getString("comment_created_date"));
                                commentList.add(status);
                            }
                        }


                    } else {


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
                        .make(text_view_comment_box_count, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();

            } else {

                if ("true".equalsIgnoreCase(status)) {

                    progress_bar_comment_list.setVisibility(View.GONE);
                    recycle_view_comment.setVisibility(View.VISIBLE);
                    imageView_empty_comment.setVisibility(View.GONE);
                    relative_layout_comment_section.setVisibility(View.VISIBLE);

                    commentAdapter = new CommentAdapter(commentList, getActivity().getApplication());
                    recycle_view_comment.setHasFixedSize(true);
                    recycle_view_comment.setAdapter(commentAdapter);

                    recycle_view_comment.setNestedScrollingEnabled(false);

                    /*Snackbar snackbar = Snackbar
                            .make(text_view_comment_box_count, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();*/

                } else {
                    progress_bar_comment_list.setVisibility(View.GONE);
                    recycle_view_comment.setVisibility(View.VISIBLE);
                    imageView_empty_comment.setVisibility(View.GONE);

                    Snackbar snackbar = Snackbar
                            .make(text_view_comment_box_count, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                }
            }
            super.onPostExecute(result);
        }
    }

    private class CommentTextWatcher implements TextWatcher {
        private View view;
        private CommentTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_text_comment_add:
                    ValidateComment();
                    break;
            }
        }
    }

    private boolean ValidateComment() {
        email = edit_text_comment_add.getText().toString().trim();
        if (email.isEmpty()) {
            image_button_comment_add.setEnabled(false);
            return false;
        }else{
            image_button_comment_add.setEnabled(true);
        }
        return true;
    }
/*
    public void getComments(){
        progress_bar_comment_list.setVisibility(View.VISIBLE);
        recycle_view_comment.setVisibility(View.GONE);
        imageView_empty_comment.setVisibility(View.GONE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<GetCommPojo>> call = service.getComments(status_id);
        call.enqueue(new Callback<List<GetCommPojo>>() {
            @Override
            public void onResponse(Call<List<GetCommPojo>> call, Response<List<GetCommPojo>> response) {
                if(response.isSuccessful()) {
                    commentList.clear();
                    comments = response.body().size();
                    text_view_wallpaper_comments_count.setText(comments+"");
                    text_view_comment_box_count.setText(comments+" "+getActivity().getResources().getString(R.string.comments));
                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            commentList.add(response.body().get(i));
                        }
                        commentAdapter.notifyDataSetChanged();

                        progress_bar_comment_list.setVisibility(View.GONE);
                        recycle_view_comment.setVisibility(View.VISIBLE);
                        imageView_empty_comment.setVisibility(View.GONE);

                        recycle_view_comment.setNestedScrollingEnabled(false);
                    } else {
                        progress_bar_comment_list.setVisibility(View.GONE);
                        recycle_view_comment.setVisibility(View.GONE);
                        imageView_empty_comment.setVisibility(View.VISIBLE);

                    }
                }else{

                }
                recycle_view_comment.setNestedScrollingEnabled(false);

            }

            @Override
            public void onFailure(Call<List<GetCommPojo>> call, Throwable t) {


            }
        });
    }
*/
    public void showCommentBox(){
        /*getComments();*/
        /*new AddComm().execute();*/
        new getComm().execute();
        if (card_view_wallpaper_comments.getVisibility() == View.VISIBLE)
        {
            Animation c= AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                    R.anim.slide_down);
            c.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    card_view_wallpaper_comments.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            card_view_wallpaper_comments.startAnimation(c);


        }else{
            Animation c= AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                    R.anim.slide_up);
            c.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    card_view_wallpaper_comments.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            card_view_wallpaper_comments.startAnimation(c);

        }

    }

/*
    public void favorite(){
        final FavoritesStorage storageFavorites= new FavoritesStorage(getActivity().getApplicationContext());

        List<VideoPojo> favorites_list = storageFavorites.loadImagesFavorites();
        Boolean exist = false;
        if (favorites_list==null){
            favorites_list= new ArrayList<>();
        }
        for (int i = 0; i <favorites_list.size() ; i++) {
            if (favorites_list.get(i).getStatusId()==(status_id)){
                exist = true;
            }
        }
        if (exist  == false) {
            ArrayList<VideoPojo> audios= new ArrayList<VideoPojo>();
            for (int i = 0; i < favorites_list.size(); i++) {
                audios.add(favorites_list.get(i));
            }




            VideoPojo fragement_Video_joke = new VideoPojo();
            fragement_Video_joke.setStatusId(status_id);
            fragement_Video_joke.setStatusTitle(status_title);
            fragement_Video_joke.setStatusView(downloads);
            //fragement_Video_joke.getStatusImage(status_image);
            fragement_Video_joke.setStatusData(status_data);
            fragement_Video_joke.setStatusImage(  status_image);
            fragement_Video_joke.setUserId(user_id);
            fragement_Video_joke.setUserName(user_name);
            //fragement_Video_joke.setTags(tags);
            fragement_Video_joke.setStatusReview(review);
            fragement_Video_joke.setUserProfilePicture(user_profile_picture);
            fragement_Video_joke.setStatusComments(comments);
            //fragement_Video_joke.setComment(comment);
           //fragement_Video_joke.setCreated(created);
            fragement_Video_joke.setStatusView(downloads);
            fragement_Video_joke.setStatusData(extension);
            //fragement_Video_joke.setType(type);

            fragement_Video_joke.setStatusLike(status_like);
            fragement_Video_joke.setStatusWow(status_love);
            fragement_Video_joke.setStatusAngry(status_angry);
            fragement_Video_joke.setStatusSad(status_sad);
            fragement_Video_joke.setStatusHaha(status_haha);
            fragement_Video_joke.setStatusWow(status_wow);



            audios.add(fragement_Video_joke);
            storageFavorites.storeImage(audios);
        }else{
            ArrayList<VideoPojo> new_favorites= new ArrayList<VideoPojo>();
            for (int i = 0; i < favorites_list.size(); i++) {
                if (favorites_list.get(i).getStatusId()!=(status_id)){
                    new_favorites.add(favorites_list.get(i));

                }
            }
            storageFavorites.storeImage(new_favorites);
        }
    }
*/
/*
    public void AddDownloadLocal(String localpath){
        final DownloadStorage downloadStorage= new DownloadStorage(getActivity().getApplicationContext());
        List<VideoPojo> download_list = downloadStorage.loadImagesFavorites();
        Boolean exist = false;
        if (download_list==null){
            download_list= new ArrayList<>();
        }
        for (int i = 0; i <download_list.size() ; i++) {
            if (download_list.get(i).getStatusId()==(status_id)){
                exist = true;
            }
        }
        if (exist  == false) {
            ArrayList<VideoPojo> audios= new ArrayList<VideoPojo>();
            for (int i = 0; i < download_list.size(); i++) {
                audios.add(download_list.get(i));
            }
            VideoPojo videodownloaded = new VideoPojo();
            videodownloaded.setStatusId(status_id);
            videodownloaded.setStatusTitle(status_title);
            videodownloaded.setStatusView(downloads);
            videodownloaded.setStatusImage(status_image);
            videodownloaded.setStatusData(status_data);
            videodownloaded.setStatusImage(status_image);
            videodownloaded.setUserName(user_name);
            videodownloaded.setUserId(user_id);
            //videodownloaded.setTags(tags);
            videodownloaded.setStatusReview(review);
            videodownloaded.setUserProfilePicture(user_profile_picture);
            videodownloaded.setStatusComments(comments);
            //videodownloaded.setComment(comment);
           // videodownloaded.setCreated(created);
           // videodownloaded.setDownloads(downloads);
            //videodownloaded.setExtension(extension);
            //videodownloaded.setType(type);

            videodownloaded.setStatusLike(status_like);
            videodownloaded.setStatusLove(status_love);
            videodownloaded.setStatusAngry(status_angry);
            videodownloaded.setStatusSad(status_sad);
            videodownloaded.setStatusHaha(status_haha);
            videodownloaded.setStatusWow(status_wow);
            videodownloaded.setLocal(localpath);

            audios.add(videodownloaded);
            downloadStorage.storeImage(audios);
        }
    }
*/

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),   Manifest.permission.READ_CONTACTS)) {
                    Intent intent_image  =  new Intent(getActivity().getApplicationContext(), PermissionActivity.class);
                    startActivity(intent_image);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                    getActivity().finish();
                } else {
                    Intent intent_image  =  new Intent(getActivity().getApplicationContext(), PermissionActivity.class);
                    startActivity(intent_image);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                    getActivity().finish();
                }
            }

        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAdDownload.loadAd(adRequest);
    }

    private void initInterstitialAdPrepare() {
        mInterstitialAdDownload = new InterstitialAd(getActivity());
        mInterstitialAdDownload.setAdUnitId(getResources().getString(R.string.ad_unit_id_interstitial));

        mInterstitialAdDownload.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                switch (open_action){

                    case 5001:{
                        if (!downloading) {
                          /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,extension,0,WHATSAPP_ID);
                            else
                          new DownloadFileFromURL().execute(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title,extension,0,WHATSAPP_ID);*/
                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Download the No.1 and original VIDEO STATUS APP with New & Fresh Quotes \uD83D\uDE07.\n" + "https://goo.gl/3vUFAM";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            //File file = new File(getApplicationContext().getFilesDir().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(WHATSAPP_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,WHATSAPP_ID);
                            }


                        }
                        break;
                    }
                    case 5002:{
                        if (!downloading) {
                          /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, extension, 0,MESSENGER_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, extension, 0,MESSENGER_ID);*/

                        /*    Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video*//*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(MESSENGER_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,MESSENGER_ID);
                            }*/


                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setPackage(MESSENGER_ID);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");

                            // final String final_text = getResources().getString(R.string.download_more_from_link);
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            // shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
                            // shareIntent.putExtra(Intent.EXTRA_STREAM, uriSting);

                            shareIntent.setType("video/mp4");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";


                            try {

                                File finalfile = new File(uriSting);
                                if (finalfile.exists()) {
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, downloadurl);
                                    // shareIntent.setPackage(MESSENGER_ID);
                                    startActivity(Intent.createChooser(shareIntent, "Share video"));
                                } else {
                                    new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,MESSENGER_ID);
                                }
                                startActivity(shareIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.messenger_not_installed) , Toast.LENGTH_SHORT, true).show();
                            }

                        }
                        break;
                    }
                    case 5003:{
                        if (!downloading) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, extension, 0,FACEBOOK_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, extension, 0,FACEBOOK_ID);*/

                        /*    Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video*//*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(FACEBOOK_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,FACEBOOK_ID);
                            }*/

                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setPackage(FACEBOOK_ID);
                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");

                            shareIntent.setType("video/*");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            File finalfile = new File(uriSting);

                            try {

                                if (finalfile.exists()) {
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, downloadurl);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                    startActivity(shareIntent);
                                } else {
                                    new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,FACEBOOK_ID);
                                }

                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.error(getActivity().getApplicationContext(), getResources().getString(R.string.facebook_not_installed), Toast.LENGTH_SHORT, true).show();
                            }

                        }
                        break;
                    }
                    case 5004:{

                        if (!downloading) {



                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setPackage(INSTAGRAM_ID);

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            // final String final_text = getResources().getString(R.string.download_more_from_link);

                           //


                            shareIntent.setType("video/*");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            try {

                                File finalfile = new File(uriSting);
                                if (finalfile.exists()) {
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                    shareIntent.putExtra(Intent.EXTRA_TEXT,downloadurl );
                                   // shareIntent.setPackage("com.instagram.android");
                                    startActivity(shareIntent);
                                } else {
                                    new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,INSTAGRAM_ID);
                                }
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toasty.error(getActivity().getApplicationContext(), getResources().getString(R.string.instagram_not_installed) , Toast.LENGTH_SHORT, true).show();
                            }


                           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, extension, 0,INSTAGRAM_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, extension, 0,INSTAGRAM_ID);*/
                          /*  Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video*//*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage("com.instagram.android");
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,INSTAGRAM_ID);
                            }*/



                        }
                        break;
                    }
                    case 5005:{
                        if (!downloading) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, extension, 0,TWITTER_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, extension, 0,TWITTER_ID);*/

                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(TWITTER_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,TWITTER_ID);
                            }


                        }
                        break;
                    }
                    case 5006:{
                        if (!downloading) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, extension, 0,SHARE_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, extension, 0,SHARE_ID);*/
                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(SHARE_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,SHARE_ID);
                            }


                        }
                        break;
                    }
                    case 5007:{
                        if (!downloading) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, extension, 0,DOWNLOAD_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, extension, 0,DOWNLOAD_ID);*/

                          /*  Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video*//*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);*/

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                /*videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(DOWNLOAD_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));*/
                                Toast.makeText(getApplicationContext(),"Alredy downloaded",Toast.LENGTH_LONG).show();
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,DOWNLOAD_ID);
                            }

                        }
                        break;
                    }
                    case 5008:{
                        if (!downloading) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, extension, 0,HIKE_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, extension, 0,HIKE_ID);*/

                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(HIKE_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,HIKE_ID);
                            }
                        }
                        break;
                    }
                    case 5009:{
                        if (!downloading) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,status_data, status_title, extension, 0,SNAPSHAT_ID);
                            else
                                new DownloadFileFromURL().execute(status_data, status_title, extension, 0,SNAPSHAT_ID);*/

                            Intent videoshare = new Intent(Intent.ACTION_SEND);
                            videoshare.setType("video/*");

                            String downloadurl = "Now Download the latest and best videos for WhatsApp status\n" + "https://play.google.com/store/apps/details?id=com.fourarc.videostatus";
                            videoshare.putExtra(Intent.EXTRA_TEXT, downloadurl);

                            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");
                            File finalfile = new File(uriSting);
                            if (finalfile.exists()) {
                                videoshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriSting));
                                videoshare.setPackage(SNAPSHAT_ID);
                                startActivity(Intent.createChooser(videoshare, "Share video"));
                            } else {
                                new DownloadFileFromURL2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl,SNAPSHAT_ID);
                            }
                        }
                        break;
                    }
                    case 5010:{
                      // favorite();
                        break;
                    }

                }

                requestNewInterstitial();


            }
        });

        requestNewInterstitial();
    }

    public boolean check(){
        PrefManager prf = new PrefManager(getActivity().getApplicationContext());
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

                if (seconds > Integer.parseInt(getResources().getString(R.string.AD_MOB_TIME))) {
                    prf.setString("LAST_DATE_ADS", strDate);
                    return  true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }
    public void loadLang(){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Language>> call = service.languageAll();
        call.enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, final Response<List<Language>> response) {

            }
            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
            }
        });
    }
    public void addComment(){


        PrefManager prf= new PrefManager(getActivity().getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")){

            byte[] data = new byte[0];
            String comment_final ="";
            try {
                data = edit_text_comment_add.getText().toString().getBytes("UTF-8");
                comment_final = Base64.encodeToString(data, Base64.DEFAULT);

            } catch (UnsupportedEncodingException e) {
                comment_final = edit_text_comment_add.getText().toString();
                e.printStackTrace();
            }
            progress_bar_comment_add.setVisibility(View.VISIBLE);
            image_button_comment_add.setVisibility(View.GONE);
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.addCommentImage(Constant_store.c_Key,Constant_store.c_Secret,id_status,Constant_store.customer_id,email);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body().getCode()==200){
                            //comments++ ;
                          //  text_view_wallpaper_comments_count.setText(comments +"");
                            text_view_comment_box_count.setText(text_view_wallpaper_comments_count.getText()+" "+getActivity().getResources().getString(R.string.comments));
                            recycle_view_comment.setVisibility(View.VISIBLE);
                            imageView_empty_comment.setVisibility(View.GONE);
                            Toasty.success(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            edit_text_comment_add.setText("");
                            String id="";
                            String content="";
                            String user="";
                            String image="";

                            for (int i=0;i<response.body().getValues().size();i++){
                                if (response.body().getValues().get(i).getName().equals("id")){
                                    id=response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("content")){
                                    content=response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("user")){
                                    user=response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("image")){
                                    image=response.body().getValues().get(i).getValue();
                                }
                            }
                            Comment comment= new Comment();
                            comment.setId(Integer.parseInt(id));
                            comment.setUser(user);
                            comment.setContent(content);
                            comment.setImage(image);
                            comment.setEnabled(true);
                            comment.setCreated(getResources().getString(R.string.now_time));
                            /*commentList.add(comment);*/
                            commentAdapter.notifyDataSetChanged();

                        }else{
                            Toasty.error(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    recycle_view_comment.scrollToPosition(recycle_view_comment.getAdapter().getItemCount()-1);
                    recycle_view_comment.scrollToPosition(recycle_view_comment.getAdapter().getItemCount()-1);
                    commentAdapter.notifyDataSetChanged();
                    progress_bar_comment_add.setVisibility(View.GONE);
                    image_button_comment_add.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    progress_bar_comment_add.setVisibility(View.VISIBLE);
                    image_button_comment_add.setVisibility(View.GONE);
                }
            });
        }else{
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }

    }
    public void follow(){

        PrefManager prf= new PrefManager(getActivity().getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            button_follow_user_activity.setText(getResources().getString(R.string.loading));
            button_follow_user_activity.setEnabled(false);
            String follower = prf.getString("ID_USER");
            String key = prf.getString("TOKEN_USER");
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.follow(user_id, Integer.parseInt(follower), key);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getCode().equals(200)){
                            button_follow_user_activity.setText("UnFollow");
                        }else if (response.body().getCode().equals(202)) {
                            button_follow_user_activity.setText("Follow");

                        }
                    }
                    button_follow_user_activity.setEnabled(true);

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    button_follow_user_activity.setEnabled(true);
                }
            });
        }else{
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
        }
    }
    private void getUser() {
        PrefManager prf= new PrefManager(getActivity().getApplicationContext());
        Integer follower= -1;
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            button_follow_user_activity.setEnabled(false);
            follower = Integer.parseInt(prf.getString("ID_USER"));
        }
        if (follower!=user_id){
            button_follow_user_activity.setVisibility(View.VISIBLE);
        }
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.getUser(user_id,follower);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){

                    for (int i=0;i<response.body().getValues().size();i++){

                        if (response.body().getValues().get(i).getName().equals("follow")){
                            if (response.body().getValues().get(i).getValue().equals("true"))
                                button_follow_user_activity.setText("UnFollow");
                            else
                                button_follow_user_activity.setText("Follow");
                        }
                    }

                }else{


                }
                button_follow_user_activity.setEnabled(true);
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                button_follow_user_activity.setEnabled(true);
            }
        });
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


    private class addlikes extends AsyncTask<String, Void, Void> {
        String response;
        String status, msg;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... urls) {

            if (nw.isOnline() == true) {
                removeReaction(prefManager.getString("reaction_"+52));

                JSONObject json_main = new JSONObject();

                try {
                    json_main.put("c_key", Constant_store.c_Key);
                    json_main.put("c_secret", Constant_store.c_Secret);
                    json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));
                    json_main.put("status_id", id_status);
                    json_main.put("like_key", likereaction);


                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(Constant_store.API_URL + "video_like.php", ServiceHandler.POST, json_main);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsMain = js.getJSONObject("response");
                    status = jsMain.getString("type");
                    msg = jsMain.getString("responseMsg");

                    if (status.equals("false")) {
                    } else {
                        JSONObject jsFinal = jsMain.getJSONObject("responseData");
                        setReaction(likereaction);
                        liketext= Integer.valueOf(response.toString());
                        text_view_like_fragement_video.setText(format(liketext+1));
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
                        .make(text_view_comment_box_count, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();

            } else {

                if ("true".equalsIgnoreCase(status)) {
                    likereaction="";


                  /*  Snackbar snackbar = Snackbar
                            .make(text_view_comment_box_count, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();*/

                } else {

                    Snackbar snackbar = Snackbar
                            .make(text_view_comment_box_count, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                }
            }
            super.onPostExecute(result);
        }
    }

    private class adddislikes extends AsyncTask<String, Void, Void> {
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

                    json_main.put("status_id", id_status);
                    json_main.put("like_key", likereaction);


                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(Constant_store.API_URL + "video_like.php", ServiceHandler.POST, json_main);

                    JSONObject js = new JSONObject(response);
                    JSONObject jsMain = js.getJSONObject("response");
                    status = jsMain.getString("type");
                    msg = jsMain.getString("responseMsg");

                    if (status.equals("false")) {
                    } else {
                        JSONObject jsFinal = jsMain.getJSONObject("responseData");
                        setReaction("none");
                        liketext= Integer.valueOf(response.toString());
                        //text_view_like_fragement_video.setText(format(liketext+1));


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
                        .make(text_view_comment_box_count, "Internet is not available. Please turn on and try again.", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundResource(R.color.colorPrimary);
                snackbar.show();

            } else {

                if ("true".equalsIgnoreCase(status)) {
                    likereaction="";


                  /*  Snackbar snackbar = Snackbar
                            .make(text_view_comment_box_count, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();*/

                } else {

                    Snackbar snackbar = Snackbar
                            .make(text_view_comment_box_count, msg, Snackbar.LENGTH_LONG);

                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.colorPrimary);
                    snackbar.show();
                }
            }
            super.onPostExecute(result);
        }
    }



/*
    private class GetStatusOperation2 extends AsyncTask<String, Void, Void> {

        String response = null;
        String status, responseMsg = "Something went wrong..!!";


        @Override
        protected void onPreExecute() {
            VideoList = new ArrayList<>();


        }

        @Override
        protected Void doInBackground(String... urls) {

            if (nw.isOnline() == true) {
                JSONObject json_main = new JSONObject();

                try {
                    json_main.put("c_key", Constant_store.c_Key);
                    json_main.put("c_secret", Constant_store.c_Secret);
                    json_main.put("page", page_no);
                    */
/*json_main.put("user_id", Utility.getStringSharedPreferences(getActivity(), Constant_store.customer_id));*//*

                    json_main.put("user_id", "27");

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

                    }else {
                        responseMsg =jsmain.getString("asdfdsdfsd");
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


            } else {


                if (status.equalsIgnoreCase("true")) {


                } else {

                }

            }
            super.onPostExecute(result);
        }
    }
*/


    public void addLike(Integer id){
        removeReaction(prefManager.getString("reaction_"+id));
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageAddLike(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_like1=response.body();
                    text_view_like_fragement_video.setText(format(status_like1));
                    setReaction("like");
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);
            }
        });
    }
    public void addLove(Integer id){
        removeReaction(prefManager.getString("reaction_"+id));
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageAddLove(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_love1=response.body();
                    text_view_love_fragement_video.setText(format(status_love1));
                    setReaction("love");
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void addSad(Integer id){
        removeReaction(prefManager.getString("reaction_"+id));
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageAddSad(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_sad1=response.body();
                    text_view_sad_fragement_video.setText(format(status_sad1));
                    setReaction("sad");
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void addAngry(Integer id){
        removeReaction(prefManager.getString("reaction_"+id));
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageAddAngry(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_angry1=response.body();
                    text_view_angry_fragement_video.setText(format(status_angry1));
                    setReaction("angry");
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void addHaha(Integer id){
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        removeReaction(prefManager.getString("reaction_"+id));
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageAddHaha(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_haha1=response.body();
                    text_view_haha_fragement_video.setText(format(status_haha1));
                    setReaction("haha");
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void addWoow(Integer id){
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        removeReaction(prefManager.getString("reaction_"+id));
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageAddWoow(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_wow1=response.body();
                    text_view_woow_fragement_video.setText(format(status_wow1));
                    setReaction("woow");

                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }

    public void deleteWoow(Integer id){

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageDeleteWoow(id);
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_wow1=response.body();
                    text_view_woow_fragement_video.setText(format(status_wow1));
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void deleteLike(Integer id){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        Call<Integer> call = service.imageDeleteLike(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_like1=response.body();
                    text_view_like_fragement_video.setText(format(status_like1));
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void deleteAngry(Integer id){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        Call<Integer> call = service.imageDeleteAngry(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_angry1=response.body();
                    text_view_angry_fragement_video.setText(format(status_angry1));
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void deleteHaha(Integer id){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        Call<Integer> call = service.imageDeleteHaha(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_haha1=response.body();
                    text_view_haha_fragement_video.setText(format(status_haha1));
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void deleteSad(Integer id){
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageDeleteSad(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_sad1=response.body();
                    text_view_sad_fragement_video.setText(format(status_sad1));
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void deleteLove(Integer id){
        linear_layout_reactions_loading.setVisibility(View.VISIBLE);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageDeleteLove(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                if(response.isSuccessful()){
                    status_love1=response.body();
                    text_view_love_fragement_video.setText(format(status_love1));
                }
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                linear_layout_reactions_loading.setVisibility(View.GONE);

            }
        });
    }
    public void setReaction( String reaction){
        text_view_like_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count));
        text_view_love_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count));
        text_view_angry_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count));
        text_view_sad_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count));
        text_view_haha_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count));
        text_view_woow_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count));

        text_view_like_fragement_video.setTextColor(getResources().getColor(R.color.primary_text));
        text_view_woow_fragement_video.setTextColor(getResources().getColor(R.color.primary_text));
        text_view_love_fragement_video.setTextColor(getResources().getColor(R.color.primary_text));
        text_view_sad_fragement_video.setTextColor(getResources().getColor(R.color.primary_text));
        text_view_angry_fragement_video.setTextColor(getResources().getColor(R.color.primary_text));
        text_view_haha_fragement_video.setTextColor(getResources().getColor(R.color.primary_text));

        like_button_like_fragement_video.setLiked(false);
        like_button_love_fragement_video.setLiked(false);
        like_button_angry_fragement_video.setLiked(false);
        like_button_haha_fragement_video.setLiked(false);
        like_button_sad_fragement_video.setLiked(false);
        like_button_woow_fragement_video.setLiked(false);


        if (reaction.equals("like")){
            prefManager.setString("reaction_"+status_id,"like");
            text_view_like_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count_select));
            text_view_like_fragement_video.setTextColor(getResources().getColor(R.color.white));
            like_button_like_fragement_video.setLiked(true);

        }else if (reaction.equals("woow")){
            prefManager.setString("reaction_"+status_id,"woow");
            text_view_woow_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count_select));
            text_view_woow_fragement_video.setTextColor(getResources().getColor(R.color.white));
            like_button_woow_fragement_video.setLiked(true);

        }else if (reaction.equals("love")){
            prefManager.setString("reaction_"+status_id,"love");
            text_view_love_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count_select));
            text_view_love_fragement_video.setTextColor(getResources().getColor(R.color.white));
            like_button_love_fragement_video.setLiked(true);

        }else if (reaction.equals("angry")){
            prefManager.setString("reaction_"+status_id,"angry");
            text_view_angry_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count_select));
            text_view_angry_fragement_video.setTextColor(getResources().getColor(R.color.white));
            like_button_angry_fragement_video.setLiked(true);

        }else if (reaction.equals("sad")){
            prefManager.setString("reaction_"+status_id,"sad");
            text_view_sad_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count_select));
            text_view_sad_fragement_video.setTextColor(getResources().getColor(R.color.white));
            like_button_sad_fragement_video.setLiked(true);

        }else if (reaction.equals("haha")){
            prefManager.setString("reaction_"+status_id,"haha");
            text_view_haha_fragement_video.setBackground(getResources().getDrawable(R.drawable.bg_card_count_select));
            text_view_haha_fragement_video.setTextColor(getResources().getColor(R.color.white));
            like_button_haha_fragement_video.setLiked(true);
        }

    }
    public void removeReaction(String  reaction){
        if (reaction.equals("like")){
            deleteLike(status_id);
        }else if (reaction.equals("woow")){
            deleteWoow(status_id);
        }else if (reaction.equals("love")){
            deleteLove(status_id);
        }else if (reaction.equals("angry")){
            deleteAngry(status_id);
        }else if (reaction.equals("sad")){
            deleteSad(status_id);
        }else if (reaction.equals("haha")){
            deleteHaha(status_id);
        }
    }



    /**
     * Background Async Task to download file
     * */



    class DownloadFileFromURL2 extends AsyncTask<String, String, String> {
        private String old = "-100";
        private boolean runing = true;
        private String share_app;
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //player_progress.setVisibility(View.VISIBLE);
           // txt_per.setVisibility(View.VISIBLE);
            //txt_per.setText(0);
           // img_download.setVisibility(View.GONE);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                //OutputStream output = new FileOutputStream("/sdcard/video status/video"+status_id+".jpg");
                File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");
                //File file = new File(getApplicationContext().getFilesDir().getPath(), "video status");
                if (!file.exists()) {
                    file.mkdirs();
                }
                String uriSting = (file.getAbsolutePath() + "/"
                        + status_title+".mp4");
                OutputStream output=new FileOutputStream(uriSting);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                // Log.e("Error: ", e.getMessage());
            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            //txt_per.setText(Integer.parseInt(progress[0]));
          //  txt_per.setText(progress[0]);

            try {
                if (!progress[0].equals(old)) {
                    old = progress[0];
                    Log.v("download", progress[0] + "%");
                    setDownloading(true);
                    setProgressValue(Integer.parseInt(progress[0]));
                }
            } catch (Exception e) {

            }

        }
        @Override
        protected void onPostExecute(String file_url) {
            // player_progress.setVisibility(View.GONE);
            //  txt_per.setVisibility(View.GONE);
            //  img_download.setImageResource(R.drawable.ic_tick_check);
            //  img_download.setVisibility(View.VISIBLE);
          /*  String dir_path = Environment.getExternalStorageDirectory().toString() +  "video status";

            String localpath=  dir_path+status_title.toString().replace("/","_")+"_"+status_id+"."+"mp4";
            AddDownloadLocal(localpath);*/

           // progress_bar_fragement_video.setVisibility(View.INVISIBLE);
        }

        private void download() {
            Toasty.success(getActivity().getApplicationContext(), getResources().getString(R.string.images_downloaded), Toast.LENGTH_SHORT, true).show();
        }
        public void shareWhatsapp(String path){


            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "video status");

            String uriSting = (file.getAbsolutePath() + "/" + status_title + ".mp4");


            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(WHATSAPP_ID);


            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriSting);


            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.whatsapp_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareFacebook(String path){
          //  Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));

        }
        public void shareMessenger(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(MESSENGER_ID);


            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.messenger_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareSnapshat(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(SNAPSHAT_ID);

            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.snapchat_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareHike(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(HIKE_ID);


            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);


            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.hike_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareInstagram(String path){
          //  Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));

        }
        public void shareTwitter(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(TWITTER_ID);


            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(), getResources().getString(R.string.twitter_not_installed), Toast.LENGTH_SHORT, true).show();
            }
        }
        public void share(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);


            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(Intent.createChooser(shareIntent,getResources().getString(R.string.share_via)+ " " + getResources().getString(R.string.app_name) ));
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.app_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }

    }
    public void AddDownloadLocal(String localpath){
        final DownloadStorage downloadStorage= new DownloadStorage(getActivity().getApplicationContext());
        List<VideoPojo> download_list = downloadStorage.loadImagesFavorites();
        Boolean exist = false;
        if (download_list==null){
            download_list= new ArrayList<>();
        }
        for (int i = 0; i <download_list.size() ; i++) {
            if (download_list.get(i).getStatusId().equals(id_status)){
                exist = true;
            }
        }
        if (exist  == false) {
            ArrayList<VideoPojo> audios= new ArrayList<VideoPojo>();
            for (int i = 0; i < download_list.size(); i++) {
                audios.add(download_list.get(i));
            }
            VideoPojo videodownloaded = new VideoPojo();
            videodownloaded.setStatusId(id_status);
            videodownloaded.setStatusTitle(status_title);
            videodownloaded.setStatusView(status_view);
            videodownloaded.setStatusImage(status_image);
            videodownloaded.setStatusData(status_data);
            videodownloaded.setStatusImage(status_image);
            videodownloaded.setStatusIsfavourite("1");
           // videodownloaded.set(user);
            videodownloaded.setUserName(String.valueOf(user_id));
          //  videodownloaded.setTags(tags);
           // videodownloaded.setStatusReview(sta);
            videodownloaded.setUserProfilePicture(user_profile_picture);
            videodownloaded.setStatusComments(String.valueOf(status_comments));
           // videodownloaded.setComment(comment);
           // videodownloaded.setCreated(created);
          //  videodownloaded.setd(downloads);
         //   videodownloaded.se(extension);
           // videodownloaded.sett(type);

            videodownloaded.setStatusLike(status_like);
            videodownloaded.setStatusLove(status_love);
            videodownloaded.setStatusAngry(status_angry);
            videodownloaded.setStatusSad(status_sad);
            videodownloaded.setStatusHaha(status_haha);
            videodownloaded.setStatusWow(status_wow);
            videodownloaded.setLocal(localpath);

            audios.add(videodownloaded);
            downloadStorage.storeImage(audios);
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
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setDownloading(true);
            Log.v("prepost","ok");
        }
        public boolean dir_exists(String dir_path)
        {
            boolean ret = false;
            File dir = new File(dir_path);
            if(dir.exists() && dir.isDirectory())
                ret = true;
            return ret;
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            runing = false;
        }
        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(Object... f_url) {
            int count;
            try {
                URL url = new URL((String) f_url[0]);
                String title = (String) f_url[1];
                String extension = (String) f_url[2];
                this.position = (int) f_url[3];
                this.share_app = (String) f_url[4];
                Log.v("v",(String) f_url[0]);

                URLConnection conection = url.openConnection();
                conection.setRequestProperty("Accept-Encoding", "identity");
                conection.connect();

                int lenghtOfFile = conection.getContentLength();
                Log.v("lenghtOfFile",lenghtOfFile+"");

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);



                String dir_path = Environment.getExternalStorageDirectory().toString() + "/StatusVideos/";

                if (!dir_exists(dir_path)){
                    File directory = new File(dir_path);
                    if(directory.mkdirs()){
                        Log.v("dir","is created 1");
                    }else{
                        Log.v("dir","not created 1");

                    }
                    if(directory.mkdir()){
                        Log.v("dir","is created 2");
                    }else{
                        Log.v("dir","not created 2");

                    }
                }else{
                    Log.v("dir","is exist");
                }
                File file= new File(dir_path+title.toString().replace("/","_")+"_"+status_id+"."+extension);
                if(!file.exists()){
                    Log.v("dir","file is exist");
                    OutputStream output = new FileOutputStream(dir_path+title.toString().replace("/","_")+"_"+status_id+"."+extension);


                    byte data[] = new byte[1024];

                    long total = 0;


                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress(""+(int)((total*100)/lenghtOfFile));
                        // writing data to file
                        output.write(data, 0, count);
                        if (!runing){
                            Log.v("v","not rurning");
                        }
                    }

                    output.flush();

                    output.close();
                    input.close();

                }
                MediaScannerConnection.scanFile(getActivity().getApplicationContext(), new String[] { dir_path+title.toString().replace("/","_")+"_"+status_id+"."+extension },
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(new File(dir_path+title.toString().replace("/","_")+"_"+status_id+"."+extension));
                    scanIntent.setData(contentUri);
                    getActivity().sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    getActivity().sendBroadcast(intent);
                }
                String path = dir_path + title.toString().replace("/", "_") + "_" + status_id + "." + extension;
            } catch (Exception e) {
                //Log.v("ex",e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            try {
                if (!progress[0].equals(old)) {
                    old = progress[0];
                    Log.v("download", progress[0] + "%");
                    setDownloading(true);
                    setProgressValue(Integer.parseInt(progress[0]));
                }
            } catch (Exception e) {

            }

        }
        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {

            setDownloading(false);

            if (path==null){
                if (local!=null){
                    switch (share_app) {
                        case WHATSAPP_ID:
                            shareWhatsapp(local);
                            break;
                        case FACEBOOK_ID:
                            shareFacebook(local);
                            break;
                        case MESSENGER_ID:
                            shareMessenger(local);
                            break;
                        case INSTAGRAM_ID:
                            shareInstagram(local);
                            break;
                        case SHARE_ID:
                            share(local);
                            break;
                        case TWITTER_ID:
                            shareTwitter(local);
                            break;
                        case SNAPSHAT_ID:
                            shareSnapshat(local);
                            break;
                        case HIKE_ID:
                            shareHike(local);
                            break;
                        case DOWNLOAD_ID:
                            download();
                            break;
                    }
                }else {
                    try {
                        Toasty.error(App.getInstance(), getResources().getString(R.string.download_failed), Toast.LENGTH_SHORT, true).show();
                    }catch (Exception e){

                    }
                }

            }else {
                addDownload(status_id);
           //  AddDownloadLocal(path);
                switch (share_app) {
                    case WHATSAPP_ID:
                        shareWhatsapp(path);
                        break;
                    case FACEBOOK_ID:
                        shareFacebook(path);
                        break;
                    case MESSENGER_ID:
                        shareMessenger(path);
                        break;
                    case INSTAGRAM_ID:
                        shareInstagram(path);
                        break;
                    case SHARE_ID:
                        share(path);
                        break;
                    case TWITTER_ID:
                        shareTwitter(path);
                        break;
                    case SNAPSHAT_ID:
                        shareSnapshat(path);
                        break;
                    case HIKE_ID:
                        shareHike(path);
                        break;
                    case DOWNLOAD_ID:
                        download();
                        break;
                }
            }
        }

        private void download() {
            Toasty.success(getActivity().getApplicationContext(), getResources().getString(R.string.images_downloaded), Toast.LENGTH_SHORT, true).show();
        }
        public void shareWhatsapp(String path){

            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(WHATSAPP_ID);

            
            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.whatsapp_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareFacebook(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(FACEBOOK_ID);

            
            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.facebook_not_installed), Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareMessenger(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(MESSENGER_ID);

            
            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.messenger_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareSnapshat(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(SNAPSHAT_ID);

            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.snapchat_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareHike(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(HIKE_ID);

            
            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);


            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.hike_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareInstagram(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(INSTAGRAM_ID);

            
            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(), getResources().getString(R.string.instagram_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
        public void shareTwitter(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(TWITTER_ID);

            
            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(), getResources().getString(R.string.twitter_not_installed), Toast.LENGTH_SHORT, true).show();
            }
        }
        public void share(String path){
            Uri imageUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", new File(path));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);

            
            final String final_text = getResources().getString(R.string.download_more_from_link);

            shareIntent.putExtra(Intent.EXTRA_TEXT,final_text );
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            shareIntent.setType(type);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(Intent.createChooser(shareIntent,getResources().getString(R.string.share_via)+ " " + getResources().getString(R.string.app_name) ));
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(getActivity().getApplicationContext(),getResources().getString(R.string.app_not_installed) , Toast.LENGTH_SHORT, true).show();
            }
        }
    }
    public void setDownloading(Boolean downloading){
        if (downloading){
            relative_layout_progress_fragement_video.setVisibility(View.VISIBLE);
        }else{
            relative_layout_progress_fragement_video.setVisibility(View.GONE);
        }
        this.downloading = downloading;
    }
    public void setProgressValue(int progress){
        this.progress_bar_fragement_video.setProgress(progress);
        this.text_view_progress_fragement_video.setText(getResources().getString(R.string.downloading)+" "+progress+" %");
    }
    public void addDownload(Integer id){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.imageAddDownload(id);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {

            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
    private final String TAG = VideoActivity.class.getSimpleName();
    private RelativeLayout nativeBannerAdContainer;
    private LinearLayout adView;
    private NativeBannerAd nativeBannerAd;

    public void initAds(){
        if (prefManager.getString("SUBSCRIBED").equals("TRUE"))
            return;
        if (!getResources().getString(R.string.FACEBOOK_ADS_ENABLED_BANNER).equals("true"))
            return;
        // Instantiate a NativeBannerAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeBannerAd = new NativeBannerAd(getActivity(), getResources().getString(R.string.FACEBOOK_ADS_NATIVE_BANNER_PLACEMENT_ID));
        nativeBannerAd.setAdListener(new NativeAdListener() {
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
                // Race condition, load() called again before last ad was displayed
                if (nativeBannerAd == null || nativeBannerAd != ad) {
                    return;
                }
                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
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
        // load the ad
        nativeBannerAd.loadAd();
    }
    private void inflateAd(NativeBannerAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeBannerAdContainer = view.findViewById(R.id.native_banner_ad_container);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_layout, nativeBannerAdContainer, false);
        nativeBannerAdContainer.addView(adView);

        // Add the AdChoices icon (NativeBannerAdActivity.this, nativeBannerAd, true);
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(getActivity().getApplicationContext(),nativeBannerAd,true);
        adChoicesContainer.addView(adChoicesView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        AdIconView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }

    private void loadMore() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<VideoPojo>> call = service.getvideo(Constant_store.c_Key,Constant_store.c_Secret);
        call.enqueue(new Callback<List<VideoPojo>>() {
            @Override
            public void onResponse(Call<List<VideoPojo>> call, Response<List<VideoPojo>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()!=0){
                        videoList.clear();
                        for (int i=0;i<response.body().size();i++){
                            videoList.add(response.body().get(i));
                        }
                        videoAdapter.notifyDataSetChanged();
                        recycler_view_status_load_more.setNestedScrollingEnabled(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<VideoPojo>> call, Throwable t) {

            }
        });
    }

}
