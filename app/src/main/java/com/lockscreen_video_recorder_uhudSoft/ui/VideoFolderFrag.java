package com.lockscreen_video_recorder_uhudSoft.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lockscreen_video_recorder_uhudSoft.Activities.VideosPlayer;
import com.lockscreen_video_recorder_uhudSoft.Adapters.VideoListAdapter;
import com.lockscreen_video_recorder_uhudSoft.Models.VideoModel;
import com.lockscreen_video_recorder_uhudSoft.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class VideoFolderFrag extends Fragment {

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) getVideos();
        }
    });
    ArrayList<VideoModel> arrayList;
    RecyclerView videoRecyclerView;
    VideoListAdapter videoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_video_folder, container, false);

        // Facebook Load Banner Ad
        /*com.facebook.ads.AdView adView = new com.facebook.ads.AdView(getActivity(), "905200500907458_905201104240731", AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();*/

        // Load Banner
        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        requireActivity().setTitle("Recorded Videos");
        videoRecyclerView = view.findViewById(R.id.id_RVVideos);
        videoRecyclerView.setHasFixedSize(true);
        videoRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        arrayList = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else  if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }else{
            getVideos();
        }
        return view;
    }

    public void getVideos() {
        arrayList.clear();
        String path = "/storage/emulated/0/Movies/LockScreen Video Recorder";

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
        videoAdapter = new VideoListAdapter(requireContext(), arrayList);
        videoRecyclerView.setAdapter(videoAdapter);

        videoAdapter.setClickListener((view, path1) -> startActivity(new Intent(requireContext(), VideosPlayer.class).putExtra("video", path1)));

    }

    @Override
    public void onResume() {
        super.onResume();
        getVideos();
    }
}