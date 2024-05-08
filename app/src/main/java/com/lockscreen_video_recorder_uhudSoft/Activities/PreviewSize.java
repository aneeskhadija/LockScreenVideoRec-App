package com.lockscreen_video_recorder_uhudSoft.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;

public class PreviewSize extends AppCompatActivity {

    RadioButton rdb_smallPreview, rdb_mediumPreview, rdb_largePreview, rdb_noPreview;
    SharedPrefs prefs;
    AppCompatButton btnSubmit;
    String str_preview;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_size);



        rdb_smallPreview = findViewById(R.id.id_rdbSmallPreview);
        rdb_mediumPreview = findViewById(R.id.id_rdbMediumPreview);
        rdb_largePreview = findViewById(R.id.id_rdbLargePreview);
        rdb_noPreview = findViewById(R.id.id_rdbNoPreview);
        btnSubmit = findViewById(R.id.id_submitButton);

        prefs = new SharedPrefs(PreviewSize.this);
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
                Toast.makeText(PreviewSize.this, str_preview, Toast.LENGTH_LONG).show(); // print the value of selected super star
            }
        });
    }
}