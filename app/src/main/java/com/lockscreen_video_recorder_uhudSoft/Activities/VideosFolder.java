package com.lockscreen_video_recorder_uhudSoft.Activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lockscreen_video_recorder_uhudSoft.Adapters.VideoListAdapter;
import com.lockscreen_video_recorder_uhudSoft.Models.VideoModel;
import com.lockscreen_video_recorder_uhudSoft.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class VideosFolder extends AppCompatActivity {

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) getVideos();
        }
    });
    ArrayList<VideoModel> arrayList;
    RecyclerView videoRecyclerView;
    VideoListAdapter videoAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_folder);



        this.setTitle("Recorded Videos");
        videoRecyclerView = findViewById(R.id.id_RVVideos);
        videoRecyclerView.setHasFixedSize(true);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }else{
            getVideos();
        }
    }

    public void getVideos() {
        arrayList.clear();
        String path = "/storage/emulated/0/Movies/LockscreenRecorder";

        File file = new File(path);

        File[] files = file.listFiles();

        if(files != null){
            for (File file1 : files){

                if (file1.getPath().endsWith(".mp4")){
                    arrayList.add(new VideoModel(file1.getPath()));
                }
            }
        }

        Collections.reverse(arrayList);
        videoAdapter = new VideoListAdapter(this, arrayList);
        videoRecyclerView.setAdapter(videoAdapter);

        videoAdapter.setClickListener((view, path1) -> startActivity(new Intent(this, VideosPlayer.class).putExtra("video", path1)));

    }

    @Override
    public void onResume() {
        super.onResume();
        getVideos();
    }
}