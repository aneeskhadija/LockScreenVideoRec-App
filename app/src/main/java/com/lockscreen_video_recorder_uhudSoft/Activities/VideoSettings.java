package com.lockscreen_video_recorder_uhudSoft.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;
import com.lockscreen_video_recorder_uhudSoft.ui.PreviewSizeFrag;
import com.lockscreen_video_recorder_uhudSoft.ui.VideoQualityFrag;

public class VideoSettings extends AppCompatActivity {

    SharedPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_settings);
        getSupportActionBar().hide();

        prefs = new SharedPrefs(this);

        // below line is used to check if frame layout is empty or not.
        if (findViewById(R.id.id_vsaFrameLayout) != null) {
            if (savedInstanceState != null) {
                return;
            }
            // below line is to inflate our fragment.
//            getFragmentManager().beginTransaction().add((R.id.vsa_framelayout), new PreviewSizeFragment()).commit();
            if(prefs.getStr("video_fragment").equals("preview_fragment")){
                getSupportFragmentManager().beginTransaction().add(R.id.id_vsaFrameLayout, new PreviewSizeFrag()).commit();
            }else{
                getSupportFragmentManager().beginTransaction().add(R.id.id_vsaFrameLayout, new VideoQualityFrag()).commit();
            }
        }
    }
}