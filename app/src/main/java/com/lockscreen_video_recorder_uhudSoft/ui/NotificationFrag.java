package com.lockscreen_video_recorder_uhudSoft.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;

public class NotificationFrag extends Fragment {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch swNotification;
    SharedPrefs prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        prefs = new SharedPrefs(requireContext());

        swNotification = view.findViewById(R.id.id_switchNotification);

        swNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    prefs.setBool("sw_notification_value", true);
                }else{
                    prefs.setBool("sw_notification_value", false);
                }
            }
        });

        return view;
    }

    private void setSwNotification(){
        swNotification.setChecked(prefs.getBool("sw_notification_value"));
    }

    @Override
    public void onResume() {
        super.onResume();
        setSwNotification();
    }

}