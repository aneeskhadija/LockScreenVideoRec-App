package com.lockscreen_video_recorder_uhudSoft.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lockscreen_video_recorder_uhudSoft.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {


    private Timer timer;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(i < 100){
                    i = i + 5;
                }else{
                    timer.cancel();
                    Intent i = new Intent(SplashScreen.this, PrivacyPolicy.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 0,100);

    }
}