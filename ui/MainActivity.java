package com.fourarc.videostatus.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.fourarc.videostatus.R;
import com.fourarc.videostatus.adapter.LanguageAdapter;
import com.fourarc.videostatus.adapter.SelectableViewHolder;
import com.fourarc.videostatus.api.apiClient;
import com.fourarc.videostatus.api.apiRest;
import com.fourarc.videostatus.config.Config;
import com.fourarc.videostatus.entity.Language;
import com.fourarc.videostatus.manager.LanguageStorage;
import com.fourarc.videostatus.manager.PrefManager;
import com.fourarc.videostatus.network.Constant_store;
import com.fourarc.videostatus.network.Utility;
import com.fourarc.videostatus.ui.fragement.FavoritesFragment;
import com.fourarc.videostatus.ui.fragement.FollowFragment;
import com.fourarc.videostatus.ui.fragement.HomeFragment;
import com.fourarc.videostatus.ui.fragement.PopularFragment;
import com.fourarc.videostatus.ui.view.BottomNavigationViewBehavior;
import com.fourarc.videostatus.ui.view.MoreAppActivity;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements SelectableViewHolder.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity ----- : ";
    private static final String LOG_TAG = "iabv3";
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    private final List<Language> languageList = new ArrayList<>();
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    ConsentForm form;
    IInAppBillingService mService;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            // Toast.makeText(MainActivity.this, "set null", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            //Toast.makeText(MainActivity.this, "set Stub", Toast.LENGTH_SHORT).show();

        }
    };
    private AlertDialog.Builder builderLanguage;
    private PrefManager prefManager;
    private Menu menu;
    private VpAdapter adapter;
    private LanguageAdapter languageAdapter;
    private ViewPager viewPager;
    private BottomNavigationViewEx bnve;
    private List<Fragment> fragments;// used for ViewPager adapter
    private CoordinatorLayout.LayoutParams layoutParams;
    private FloatingActionButton fab;
    private int tab_fab;
    private TextView text_view_name_nave_header;
    private CircleImageView circle_image_view_profile_nav_header;
    private ImageView image_view_google_nav_header;
    private ImageView image_view_facebook_nav_header;
    private Button button_login_nav_header;
    private Button button_logout_nav_header;
    private FloatingSearchView main_floating_search_view;
    private Boolean FromLogin = false;
    private FollowFragment followFragment;
    private Dialog dialog;
    private Boolean DialogOpened = false;
    private TextView text_view_go_pro;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                    Intent intent_status = new Intent(getApplicationContext(), PermissionActivity.class);
                    startActivity(intent_status);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                } else {
                    Intent intent_status = new Intent(getApplicationContext(), PermissionActivity.class);
                    startActivity(intent_status);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBuy();

        checkPermission();
        prefManager = new PrefManager(getApplicationContext());
        initData();
        iniView();
        loadLang();
        initAction();
        firebaseSubscribe();
        initEvent();
        initGDPR();
    }

    private void initGDPR() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
//// test
/////
        String[] publisherIds = {getResources().getString(R.string.publisher_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
// User's consent status successfully updated.
                Log.d(TAG, "onConsentInfoUpdated");
                switch (consentStatus) {
                    case PERSONALIZED:
                        Log.d(TAG, "PERSONALIZED");
                        ConsentInformation.getInstance(MainActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                        break;
                    case NON_PERSONALIZED:
                        Log.d(TAG, "NON_PERSONALIZED");
                        ConsentInformation.getInstance(MainActivity.this).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                        break;


                    case UNKNOWN:
                        Log.d(TAG, "UNKNOWN");
                        if (ConsentInformation.getInstance(MainActivity.this).isRequestLocationInEeaOrUnknown()) {
                            URL privacyUrl = null;
                            try {
// TODO: Replace with your app's privacy policy URL.
                                privacyUrl = new URL(getResources().getString(R.string.policy_privacy_url));

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
// Handle error.

                            }
                            form = new ConsentForm.Builder(MainActivity.this, privacyUrl).withListener(new ConsentFormListener() {
                                @Override

                                public void onConsentFormLoaded() {
// Consent form loaded successfully.
                                    Log.d(TAG, "onConsentFormLoaded");

                                    showform();
                                }

                                @Override
                                public void onConsentFormOpened() {
// Consent form was displayed.
                                    Log.d(TAG, "onConsentFormOpened");

                                }

                                @Override
                                public void onConsentFormClosed(

                                        ConsentStatus consentStatus, Boolean userPrefersAdFree) {
// Consent form was closed.
                                    Log.d(TAG, "onConsentFormClosed");

                                }

                                @Override
                                public void onConsentFormError(String

                                                                       errorDescription) {
// Consent form error.

                                    Log.d(TAG, "onConsentFormError");
                                    Log.d(TAG, errorDescription);
                                }
                            })

                                    .withPersonalizedAdsOption().withNonPersonalizedAdsOption()

                                    .build();
                            form.load();
                        } else {
                            Log.d(TAG, "PERSONALIZED else");
                            ConsentInformation.getInstance(MainActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                        }
                        break;


                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
// User's consent status failed to update.
                Log.d(TAG, "onFailedToUpdateConsentInfo");
                Log.d(TAG, errorDescription);
            }
        });
    }


    private void showform() {
        if (form != null) {
            Log.d(TAG, "show ok");
            form.show();
        }
    }

    private void initBuy() {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if (!BillingProcessor.isIabServiceAvailable(this)) {
            //  showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, Config.LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //  showToast("onProductPurchased: " + productId);
                Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
                updateTextViews();
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                // showToast("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {
                //  showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                // showToast("onPurchaseHistoryRestored");
                for (String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }

    private void updateTextViews() {
        PrefManager prf = new PrefManager(getApplicationContext());
        bp.loadOwnedPurchasesFromGoogle();

    }

    public Bundle getPurchases() {
        if (!bp.isInitialized()) {


            //  Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            // Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();

            return mService.getPurchases(Constants.GOOGLE_API_VERSION, getApplicationContext().getPackageName(), Constants.PRODUCT_TYPE_SUBSCRIPTION, null);
        } catch (Exception e) {
            //  Toast.makeText(this, "ex", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
        return null;
    }

    private void firebaseSubscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("StatusVideoApp");
    }

    private void initAction() {
        main_floating_search_view.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_language:
                        Intent intent = new Intent(MainActivity.this, LanguageActivity.class);
                        startActivity(intent);
                        finish();
                }
            }
        });
        main_floating_search_view.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", currentQuery);
                startActivity(intent);
            }
        });
        button_logout_nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        button_login_nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FromLogin = true;

                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }
        });
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager prf = new PrefManager(getApplicationContext());
                if (prf.getString("LOGGED").toString().equals("TRUE")) {


                    Intent intent_video = new Intent(getApplicationContext(), UploadVideoActivity.class);
                    startActivity(intent_video);
                    overridePendingTransition(R.anim.enter, R.anim.exit);


                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    FromLogin = true;

                }
            }
        });
        // set listener to change the current checked item of bottom nav when scroll view pager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("OK", "-----ViewPager-------- previous item:" + bnve.getCurrentItem() + " current item:" + position + " ------------------");
                if (position >= 2)// 2 is center
                    position++;// if page is 2, need set bottom item to 3, and the same to 3 -> 4
                bnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = 0;
                switch (item.getItemId()) {
                    case R.id.i_music:
                        position = 0;
                        break;
                    case R.id.i_backup:
                        position = 1;
                        break;
                    case R.id.i_visibility:
                        position = 2;
                        break;
                    case R.id.i_favor:
                        position = 3;
                        break;

                    case R.id.i_empty: {
                        return false;
                    }
                }
                if (previousPosition != position) {
                    viewPager.setCurrentItem(position, false);
                    previousPosition = position;
                    Log.i("OK", "-----bnve-------- previous item:" + bnve.getCurrentItem() + " current item:" + position + " ------------------");
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        getLanguages();

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_language:
                Intent intent = new Intent(MainActivity.this, LanguageActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setIconItem(final MenuItem item, String url) {

        final Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                BitmapDrawable mBitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                item.setIcon(mBitmapDrawable);
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                Log.d("DEBUG", "onBitmapFailed");
                item.setIcon(getResources().getDrawable(R.drawable.ic_global));

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
                Log.d("DEBUG", "onPrepareLoad");
            }
        };
        Picasso.with(this).load(url).placeholder(R.drawable.flag_placeholder).error(R.drawable.flag_placeholder).into(mTarget);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_exit) {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                super.onBackPressed();
            } else {
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.rate_our_app));
                builder.setMessage(getResources().getString(R.string.rate_our_app_message));
                // add the buttons
                builder.setPositiveButton(getResources().getString(R.string.rate_now), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prf.setString("NOT_RATE_APP", "TRUE");
                        final String appPackageName = getApplication().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        prf.setString("NOT_RATE_APP", "FALSE");


                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.no_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prf.setString("NOT_RATE_APP", "TRUE");
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        // prf.setString("NOT_RATE_APP", "FALSE");

                    }
                }).setIcon(R.drawable.star_on);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();

                dialog.show();

            }
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.my_profile) {
            PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("LOGGED").toString().equals("TRUE")) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", Integer.parseInt(prf.getString("ID_USER")));
                intent.putExtra("id", prf.getString("ID_USER").toString());
                intent.putExtra("image", prf.getString("IMAGE_USER").toString());
                intent.putExtra("name", prf.getString("NAME_USER").toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                FromLogin = true;
            }
        } else if (id == R.id.my_downloaded) {
            Intent intent = new Intent(getApplicationContext(), DownloadsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.nav_share) {
            /*final String appPackageName=getApplication().getPackageName();
            String shareBody = "Download "+getString(R.string.app_name)+" From :  "+"http://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));*/
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Best Whatsapp Status");
            String sAux = "Download the No.1 and original VIDEO STATUS APP \uD83D\uDE07. \n";
            sAux = sAux + "https://goo.gl/3vUFAM";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));


        } else if (id == R.id.nav_rate) {
           /* final String appPackageName=getApplication().getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }*/
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.fourarc.videostatus");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.fourarc.videostatus")));

            }


        }

        else if (id == R.id.nav_more) {
            Intent intent = new Intent(getApplicationContext(),MoreAppActivity.class);
            startActivity(intent);
        }


        else if (id == R.id.nav_help) {
            PrefManager prf = new PrefManager(getApplicationContext());

            Intent intent = new Intent(getApplicationContext(), SupportActivity.class);
            intent.putExtra("email", prf.getString("useremail").toString());
            intent.putExtra("name", prf.getString("NAME_USER").toString());
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

        } else if (id == R.id.nav_policy) {
            Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.buy_now) {
            showDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void iniView() {

        setContentView(R.layout.activity_main);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        this.main_floating_search_view = (FloatingSearchView) findViewById(R.id.main_floating_search_view);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.main_floating_search_view.attachNavigationDrawerToMenuButton(drawer);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerview = navigationView.getHeaderView(0);
        this.text_view_name_nave_header = (TextView) headerview.findViewById(R.id.text_view_name_nave_header);
        this.circle_image_view_profile_nav_header = (CircleImageView) headerview.findViewById(R.id.circle_image_view_profile_nav_header);
        this.image_view_google_nav_header = (ImageView) headerview.findViewById(R.id.image_view_google_nav_header);
        this.image_view_facebook_nav_header = (ImageView) headerview.findViewById(R.id.image_view_facebook_nav_header);
        this.button_login_nav_header = (Button) headerview.findViewById(R.id.button_login_nav_header);
        this.button_logout_nav_header = (Button) headerview.findViewById(R.id.button_logout_nav_header);
        this.viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        this.viewPager.setOffscreenPageLimit(100);
        this.bnve = (BottomNavigationViewEx) findViewById(R.id.bnve);


        bnve.enableAnimation(true);

        bnve.enableShiftingMode(false);
        bnve.setTextVisibility(true);
        bnve.enableItemShiftingMode(false);
        bnve.setTextSize(10);
        bnve.setSmallTextSize(10);

        // set adapter
        this.layoutParams = (CoordinatorLayout.LayoutParams) bnve.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior(fab));

        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.parent);

    }

    private void initEvent() {
        // set listener to change the current item of view pager when click bottom nav item
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = 0;
                switch (item.getItemId()) {
                    case R.id.i_music:
                        position = 0;
                        break;
                    case R.id.i_backup:
                        position = 1;
                        break;
                    case R.id.i_visibility:
                        position = 2;
                        break;
                    case R.id.i_favor:
                        position = 3;
                        break;

                    case R.id.i_empty: {
                        return false;
                    }
                }
                if (previousPosition != position) {
                    viewPager.setCurrentItem(position, false);
                    previousPosition = position;
                    Log.i("OK", "-----bnve-------- previous item:" + bnve.getCurrentItem() + " current item:" + position + " ------------------");
                }

                return true;
            }
        });

        // set listener to change the current checked item of bottom nav when scroll view pager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("OK", "-----ViewPager-------- previous item:" + bnve.getCurrentItem() + " current item:" + position + " ------------------");
                if (position >= 2)// 2 is center
                    position++;// if page is 2, need set bottom item to 3, and the same to 3 -> 4
                bnve.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // center item click listener
        main_floating_search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Center", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        fragments = new ArrayList<>(4);

        // create music fragment and add it
        HomeFragment homeFragment = new HomeFragment();


        // create backup fragment and add it
        PopularFragment popularFragment = new PopularFragment();

        // create friends fragment and add it
        FavoritesFragment favorFragment = new FavoritesFragment();

        // create friends fragment and add it
        followFragment = new FollowFragment();


        // add to fragments for adapter
        fragments.add(homeFragment);
        fragments.add(popularFragment);
        fragments.add(followFragment);
        fragments.add(favorFragment);

    }

    public void setFromLogin() {
        this.FromLogin = true;
    }


    @Override
    public void onItemSelected(Language item) {

        List<Language> selectedItems = languageAdapter.getSelectedItems();
        //  Toast.makeText(MainActivity.this,"Selected item is "+ item.getLanguage()+ ", Totally  selectem item count is "+selectedItems.size(),Toast.LENGTH_LONG).show();
    }


    public int getDefaultLangiage() {
        return prefManager.getInt("LANGUAGE_DEFAULT");
    }

    public void getLanguages() {
        final LanguageStorage storageFavorites = new LanguageStorage(getApplicationContext());
        List<Language> languages = storageFavorites.loadLang();
        if (languages == null) {
            languages = new ArrayList<>();
        }
        String default_lang = prefManager.getString("LANGUAGE_DEFAULT");
        if (default_lang.length() == 1) {

            for (int i = 0; i < languages.size(); i++) {
                if (languages.get(i).getId() == Integer.parseInt(default_lang)) {

                    MenuItem item = menu.findItem(R.id.action_language);
                    setIconItem(item, languages.get(i).getImage());
                }
            }
        }

    }

    public void loadLang() {

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

    public void logout() {
        PrefManager prf = new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        // prf.remove("SALT_USER");
        // prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        // prf.remove("TYPE_USER");
        prf.remove("USERN_USER");
        prf.remove("IMAGE_USER");
        prf.remove("LOGGED");
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.with(getApplicationContext()).load(Utility.getStringSharedPreferences(getApplicationContext(), Constant_store.customer_profile_picture)).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")) {
                image_view_google_nav_header.setVisibility(View.VISIBLE);
                image_view_facebook_nav_header.setVisibility(View.GONE);
            } else {
                image_view_google_nav_header.setVisibility(View.GONE);
                image_view_facebook_nav_header.setVisibility(View.VISIBLE);
            }
            button_logout_nav_header.setVisibility(View.VISIBLE);
            button_login_nav_header.setVisibility(View.GONE);
        } else {
            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.with(getApplicationContext()).load(R.drawable.profile).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
            button_logout_nav_header.setVisibility(View.GONE);
            button_login_nav_header.setVisibility(View.VISIBLE);
            image_view_google_nav_header.setVisibility(View.VISIBLE);
            image_view_facebook_nav_header.setVisibility(View.VISIBLE);
        }
        followFragment.Resume();

        Toast.makeText(getApplicationContext(), getString(R.string.message_logout), Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();

        updateTextViews();

        PrefManager prf = new PrefManager(getApplicationContext());

        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")) {
                image_view_google_nav_header.setVisibility(View.VISIBLE);
                image_view_facebook_nav_header.setVisibility(View.GONE);
            } else {
                image_view_google_nav_header.setVisibility(View.GONE);
                image_view_facebook_nav_header.setVisibility(View.VISIBLE);
            }
            button_logout_nav_header.setVisibility(View.VISIBLE);
            button_login_nav_header.setVisibility(View.GONE);
        } else {
            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.with(getApplicationContext()).load(R.drawable.profile).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200, 200).centerCrop().into(circle_image_view_profile_nav_header);
            button_logout_nav_header.setVisibility(View.GONE);
            button_login_nav_header.setVisibility(View.VISIBLE);
            image_view_google_nav_header.setVisibility(View.VISIBLE);
            image_view_facebook_nav_header.setVisibility(View.VISIBLE);
        }
        if (FromLogin) {
            followFragment.Resume();
            FromLogin = false;
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                super.onBackPressed();
            } else {

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.rate_our_app));
                builder.setMessage(getResources().getString(R.string.rate_our_app_message));
                // add the buttons
                builder.setPositiveButton(getResources().getString(R.string.rate_now), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prf.setString("NOT_RATE_APP", "TRUE");
                        final String appPackageName = getApplication().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
                builder.setNeutralButton(getResources().getString(R.string.later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        prf.setString("NOT_RATE_APP", "FALSE");


                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.no_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prf.setString("NOT_RATE_APP", "TRUE");
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        prf.setString("NOT_RATE_APP", "FALSE");

                    }
                }).setIcon(R.drawable.star_on);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();

                dialog.show();
                return;
            }
        }

    }

    public void showDialog() {
        this.dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_subscribe);
        this.text_view_go_pro = (TextView) dialog.findViewById(R.id.text_view_go_pro);
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(MainActivity.this, Config.SUBSCRIPTION_ID);
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                }
                return true;
            }
        });
        dialog.show();
        DialogOpened = true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConn);
    }

    private static class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> data;

        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }
    }
}
