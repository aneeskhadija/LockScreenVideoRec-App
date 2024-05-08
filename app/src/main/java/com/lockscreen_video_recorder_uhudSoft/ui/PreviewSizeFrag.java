package com.lockscreen_video_recorder_uhudSoft.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;

public class PreviewSizeFrag extends Fragment {

    boolean isLoadingAd = false;

   // Button btnSmallPreview, btnMediumPreview, btnLargePreview, btnNoPreview;
    RadioButton rdb_smallPreview, rdb_mediumPreview, rdb_largePreview, rdb_noPreview;
    SharedPrefs prefs;
    Button btnSubmit;
    String str_preview;
   // AdView mAdView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preview_size, container, false);
        // Facebook Load Banner Ad
        /*com.facebook.ads.AdView adView = new com.facebook.ads.AdView(getActivity(), "905200500907458_905201104240731", AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();*/

        rdb_smallPreview = view.findViewById(R.id.id_rdbSmallPreview);
        rdb_mediumPreview = view.findViewById(R.id.id_rdbMediumPreview);
        rdb_largePreview = view.findViewById(R.id.id_rdbLargePreview);
        rdb_noPreview = view.findViewById(R.id.id_rdbNoPreview);
        btnSubmit = view.findViewById(R.id.id_submitButton);



        prefs = new SharedPrefs(getActivity());
        if (prefs.getStr("preview_size_value").equals( "0")){

            rdb_smallPreview.setChecked(true);

            rdb_mediumPreview.setChecked(false);
            rdb_largePreview.setChecked(false);
            rdb_noPreview.setChecked(false);
        }
        else if(prefs.getStr("preview_size_value").equals("1")){

            rdb_mediumPreview.setChecked(true);

            rdb_smallPreview.setChecked(false);
            rdb_largePreview.setChecked(false);
            rdb_noPreview.setChecked(false);

        }
        else if(prefs.getStr("preview_size_value").equals("2")){

            rdb_largePreview.setChecked(true);

            rdb_mediumPreview.setChecked(false);
            rdb_smallPreview.setChecked(false);
            rdb_noPreview.setChecked(false);

        }
        else{

            rdb_noPreview.setChecked(true);

            rdb_mediumPreview.setChecked(false);
            rdb_smallPreview.setChecked(false);
            rdb_largePreview.setChecked(false);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rdb_smallPreview.isChecked()) {
                    str_preview = "Small Preview";
                    prefs.setStr("preview_size_value","0");
                } else if (rdb_mediumPreview.isChecked()) {
                    str_preview = "Medium Preview";
                    prefs.setStr("preview_size_value","1");
                } else if (rdb_largePreview.isChecked()) {
                    str_preview = "Large Preview";
                    prefs.setStr("preview_size_value","2");
                } else if (rdb_noPreview.isChecked()) {
                    str_preview = "No Preview";
                    prefs.setStr("preview_size_value","3");
                }
                Toast.makeText(getContext(), str_preview, Toast.LENGTH_LONG).show(); // print the value of selected super star
            }
        });

        return view;
    }
}