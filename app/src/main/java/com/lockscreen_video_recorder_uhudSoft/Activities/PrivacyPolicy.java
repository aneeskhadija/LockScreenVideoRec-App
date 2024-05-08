package com.lockscreen_video_recorder_uhudSoft.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;

import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;

public class PrivacyPolicy extends AppCompatActivity {

    CheckBox agreeCB;
    Button btnStart;
    WebView webView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_privacy_policy);
        getSupportActionBar().hide();

        SharedPrefs prefs = new SharedPrefs(this);

        agreeCB = findViewById(R.id.id_CbAgree);
        btnStart = findViewById(R.id.id_btnstart);
        webView = findViewById(R.id.id_webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("file:///android_asset/privacypolicy.html");

        agreeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check current state of a check box (true or false)
                boolean checkBoxState = agreeCB.isChecked();

                if (checkBoxState) {
                    prefs.setBool("cb_agree", true);
                    btnStart.setEnabled(true);
                } else {
                    prefs.setBool("cb_agree", false);
                    btnStart.setEnabled(false);
                }
            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PrivacyPolicy.this, IntroductionHomeActivity.class));
                finish();
            }
        });

        if (prefs.getBool("cb_agree")){
            startActivity(new Intent(PrivacyPolicy.this, IntroductionHomeActivity.class));
            finish();
        }
    }
}