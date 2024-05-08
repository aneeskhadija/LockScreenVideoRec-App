package com.lockscreen_video_recorder_uhudSoft.ui;

import static android.os.Build.VERSION.SDK_INT;
import static com.lockscreen_video_recorder_uhudSoft.AdsFolder.facebook_Ad_interstitial.facebook_interstitialAdListener;
import static com.lockscreen_video_recorder_uhudSoft.AdsFolder.facebook_Ad_interstitial.facebook_mInterstitialAd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.lockscreen_video_recorder_uhudSoft.Activities.AlarmPage;
import com.lockscreen_video_recorder_uhudSoft.Activities.PreviewSize;
import com.lockscreen_video_recorder_uhudSoft.Activities.VideosFolder;
import com.lockscreen_video_recorder_uhudSoft.AdsFolder.admob_MyNativeAd;
import com.lockscreen_video_recorder_uhudSoft.AdsFolder.facebook_Ad_interstitial;
import com.lockscreen_video_recorder_uhudSoft.MainActivity;
import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;
import com.lockscreen_video_recorder_uhudSoft.Utils.FloatingWindowServices;

public class HomeFrag extends Fragment {

    private final String TAG = HomeFrag.class.getName();
    private SharedPrefs prefs;
    public static ImageButton imgBtnStartRecoding;
    private FloatingWindowServices floatingWindowServices;
    private ImageView txtRecordingStatus;
    SharedPreferences preferences;
    LinearLayout lin_Video, lin_priveiw;
    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    ProgressDialog dialog;

    BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    startRecordingTwo();
                }
            });
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Ad is Loading...!");

        loadInterstitialAd();
        lin_Video = view.findViewById(R.id.id_lin_Video);
        lin_priveiw = view.findViewById(R.id.id_lin_Priveiw);
        //   lin_ad = view.findViewById(R.id.id_Ad_linearLayout);

        // Load Banner
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        /*FrameLayout frameLayout = view.findViewById(R.id.id_nativead);
        new admob_MyNativeAd(requireContext(), frameLayout);*/
        // admobMyInterstitial = new admob_MyInterstitial(getActivity());

        // Load Native Ad
        /*@SuppressLint({"MissingInflatedId", "LocalSuppress"}) FrameLayout frameLayout = view.findViewById(R.id.id_nativead);
        new admob_MyNativeAd(getContext(), frameLayout);*/

        requireActivity().setTitle("LockScreen Video Recording");
        floatingWindowServices = new FloatingWindowServices();

        prefs = new SharedPrefs(requireContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        txtRecordingStatus = view.findViewById(R.id.id_txtRecordingStatus);
        imgBtnStartRecoding = view.findViewById(R.id.id_StartRecording);

        if (FloatingWindowServices.mIsRecordingVideo) {
            imgBtnStartRecoding.setBackground(requireActivity().getDrawable(R.drawable.pause));
            txtRecordingStatus.setImageDrawable(getResources().getDrawable(R.drawable.stop));
        }

        lin_Video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    dialog.show();
                    new Handler().postDelayed(() -> {

                        Intent intent = new Intent(requireContext(), VideosFolder.class);
                        startActivity(intent);
                        showInterstitial();
                    }, 1500);
                } else {
                    startActivity(new Intent(requireContext(), VideosFolder.class));
                }
            }
        });



        lin_priveiw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Load Facebook
                //adCheckCounter();
                startActivity(new Intent(requireContext(), PreviewSize.class));
                //requireActivity().startService(new Intent(requireContext(), PreviewSizeFrag.class));

            }
        });

       /* lin_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.uhudsofttech.BluetoothAutoConnect")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.uhudsofttech.BluetoothAutoConnect")));
                }

            }
        });*/

        imgBtnStartRecoding.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                if (checkPermissions()) {
                    if (checkSystemAlertWindowPermission()) {
                        if (isMyServiceRunning()) {
                            requireActivity().stopService(new Intent(requireContext(), FloatingWindowServices.class));
                            if (preferences.getBoolean("key_show_notification", false)) {
                                cancelNotification();
                            }
                            //imgBtnStartRecoding.setBackground(requireActivity().getDrawable(R.drawable.ic_off_btn));
                            imgBtnStartRecoding.setBackground(requireActivity().getDrawable(R.drawable.play));
                            txtRecordingStatus.setImageDrawable(getResources().getDrawable(R.drawable.start));

                        } else if (!isMyServiceRunning()) {
                            if (SDK_INT >= Build.VERSION_CODES.O) {
                                requireActivity().startForegroundService(new Intent(requireContext(), FloatingWindowServices.class));
                            } else {
                                requireActivity().startService(new Intent(requireContext(), FloatingWindowServices.class));
                            }
                            imgBtnStartRecoding.setBackground(requireActivity().getDrawable(R.drawable.pause));
                            txtRecordingStatus.setImageDrawable(getResources().getDrawable(R.drawable.stop));

                            if (preferences.getBoolean("key_show_notification", false)) {
                                notifyRecording();
                            }
                        }
                    } else {
                        requestWindowOverlayPermission();
                    }
                }
            }
        });

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mAlarmReceiver,
                new IntentFilter("alarm_started"));

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                String message = intent.getStringExtra("message");
                Log.d("receiver", "Got message: " + message);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                       // imgBtnStartRecoding.setBackground(requireActivity().getDrawable(R.drawable.stop));
                        imgBtnStartRecoding.setBackground(context.getDrawable(R.drawable.play));
                        txtRecordingStatus.setImageDrawable(context.getDrawable(R.drawable.start));
                        if (preferences.getBoolean("key_show_notification", false)) {
                            cancelNotification();
                        }
                    }
                });

            }
        };

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        if (SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("RecordingChannel", "NoftifyRecording", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        return view;
    }


    //Admob
    @Override
    public void onResume() {
        super.onResume();
        adRequest = new AdRequest.Builder().build();

    }

    public boolean checkPermissions() {
        boolean result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result0 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == (PackageManager.PERMISSION_GRANTED);
        boolean result01 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_VIDEO) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == (PackageManager.PERMISSION_GRANTED);
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return result && result0 && result2 && result01;
        } else {
            return result && result1 && result2;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkSystemAlertWindowPermission() {
        return Settings.canDrawOverlays(requireContext());
    }

    private void requestWindowOverlayPermission() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireContext().getPackageName()));
            startActivity(intent);
        }

    }

    @SuppressLint("MissingPermission")
    public void notifyRecording() {

        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "RecordingChannel")
                    .setSmallIcon(R.drawable.notif_cam_icon)
                    .setContentTitle("LockScreen video Recorder")
                    .setContentText("LockScreen Recording started.");

            Intent notifyIntent = new Intent(requireContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, notifyIntent,
                    PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, builder.build());

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(requireContext());
            managerCompat.notify(1, builder.build());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("NotificationError", "notifyRecording: " + e.getMessage());
        }


    }

    public void cancelNotification() {
        NotificationManagerCompat.from(requireContext()).cancel(1);
    }

    public void startRecordingTwo() {
        try{
            if (SDK_INT >= Build.VERSION_CODES.O) {
                requireActivity().startForegroundService(new Intent(requireContext(), FloatingWindowServices.class));
            } else {
                requireActivity().startService(new Intent(requireContext(), FloatingWindowServices.class));
            }
            if (preferences.getBoolean("key_show_notification", false)) {
                notifyRecording();
            }
        }
        catch (IllegalStateException e){

        }


    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FloatingWindowServices.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // Facebook
    /*public void adCheckCounter() {

        // Ad_interstitials.counter++;

        if (facebook_mInterstitialAd != null) {

            facebook_mInterstitialAd.loadAd(
                    facebook_mInterstitialAd.buildLoadAdConfig()
                            .withAdListener(facebook_interstitialAdListener)
                            .build());

            *//*if (Ad_interstitials.counter > Ad_interstitials.total_counter){

                mInterstitialAd.loadAd(
                        mInterstitialAd.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener)
                                .build());
            }*//*
        } else {
            new facebook_Ad_interstitial(getActivity());
        }
    }*/

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    public void loadInterstitialAd() {
        adRequest = new AdRequest.Builder().build();

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(requireContext(), getResources().getString(R.string.AdMob_InterstitialAd_ID), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                            public void onAdClicked() {
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Ad dismissed fullscreen content.");

                                loadInterstitialAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

    }

    private void showInterstitial() {
        dialog.cancel();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(requireActivity());
        }
    }
}