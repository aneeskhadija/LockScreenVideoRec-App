package com.lockscreen_video_recorder_uhudSoft.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;

public class VideoQualityFrag extends Fragment {

    AppCompatButton btnSubmit;
    SharedPrefs prefs;

    private RadioGroup rgQuality;
    RadioButton rdb_btnQuality_214P, rdb_btnQuality_480p, rdb_btnQuality_720p, rdb_btnQuality_1080p;
    boolean checked;
    String str_214P, str_480P, str_720P, str_1080P;

    AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_quality, container, false);
        prefs = new SharedPrefs(getActivity());



        // Facebook Load Banner Ad
        /*com.facebook.ads.AdView adView = new com.facebook.ads.AdView(getActivity(), "905200500907458_905201104240731", AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();*/

        rdb_btnQuality_214P = view.findViewById(R.id.id_btnQuality_214P);
        rdb_btnQuality_480p = view.findViewById(R.id.id_btnQuality_480p);
        rdb_btnQuality_720p = view.findViewById(R.id.id_btnQuality_720p);
        rdb_btnQuality_1080p = view.findViewById(R.id.id_btnQuality_1080p);
        btnSubmit = view.findViewById(R.id.id_submitButton);

        if (prefs.getStr("video_quality_value").equals("0")) {

            rdb_btnQuality_214P.setChecked(true);

           // btnQuality214p.setEnabled(false);
            rdb_btnQuality_480p.setChecked(false);
            rdb_btnQuality_720p.setChecked(false);
            rdb_btnQuality_1080p.setChecked(false);

        } else if(prefs.getStr("video_quality_value").equals("1")){

            rdb_btnQuality_480p.setChecked(true);

            rdb_btnQuality_214P.setChecked(false);
            rdb_btnQuality_720p.setChecked(false);
            rdb_btnQuality_1080p.setChecked(false);

        } else if(prefs.getStr("video_quality_value").equals("2")){

            rdb_btnQuality_720p.setChecked(true);

            rdb_btnQuality_214P.setChecked(false);
            rdb_btnQuality_480p.setChecked(false);
            rdb_btnQuality_1080p.setChecked(false);

        } else{

            rdb_btnQuality_1080p.setChecked(true);

            rdb_btnQuality_214P.setChecked(false);
            rdb_btnQuality_480p.setChecked(false);
            rdb_btnQuality_720p.setChecked(false);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rdb_btnQuality_214P.isChecked()) {
                    str_214P = "Quality 214p";
                    prefs.setStr("video_quality_value","0");
                } else if (rdb_btnQuality_480p.isChecked()) {
                    str_214P = "Quality 480p";
                    prefs.setStr("video_quality_value","1");
                } else if (rdb_btnQuality_720p.isChecked()) {
                    str_214P = "Quality 720p";
                    prefs.setStr("video_quality_value","2");
                } else if (rdb_btnQuality_1080p.isChecked()) {
                    str_214P = "Quality 1080p";
                    prefs.setStr("video_quality_value","3");
                }
                Toast.makeText(getContext(), str_214P, Toast.LENGTH_LONG).show(); // print the value of selected super star
            }
        });


        return view;
    }

}