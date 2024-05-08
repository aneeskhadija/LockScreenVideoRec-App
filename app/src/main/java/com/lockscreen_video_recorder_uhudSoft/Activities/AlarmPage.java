package com.lockscreen_video_recorder_uhudSoft.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lockscreen_video_recorder_uhudSoft.AdsFolder.admob_MyNativeAd;
import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;
import com.lockscreen_video_recorder_uhudSoft.Utils.AlarmReceiver;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmPage extends AppCompatActivity {

    private TextView alrmTime, alrmDate;
    private MaterialTimePicker timePicker;
    private MaterialDatePicker datePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private SharedPrefs prefs;
    AppCompatButton btnSaveAlarm, btnCancelAlrm;
    RelativeLayout image_AlrmTime, image_AlrmDate;
    String str_currentDate, str_currentTime;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_page);

        // Load Native Ad
        FrameLayout frameLayout = findViewById(R.id.id_nativeSettingAd);
        new admob_MyNativeAd(this, frameLayout);

        // Load Native Ad
        /*FrameLayout nativeSettingFrameLayout = findViewById(R.id.id_nativeSettingAd);
        // for native Ad
        new admob_MyNativeAd(this,nativeSettingFrameLayout);*/

        this.setTitle("Set Alarm");

        alrmTime = findViewById(R.id.id_txtAlarmTime);
        alrmDate = findViewById(R.id.id_txtAlarmDate);
        prefs = new SharedPrefs(this);

        btnSaveAlarm = findViewById(R.id.id_btnSaveAlarm);
        image_AlrmDate = findViewById(R.id.id_imgAlarmDate);
        image_AlrmTime = findViewById(R.id.id_imgSelectTime);
        btnCancelAlrm = findViewById(R.id.id_btnCancelAlarm);

        str_currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        str_currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // if date is not selected it returns DNF and not null
        if (prefs.getStr("alarm_date").equals("DNF")) {
            alrmDate.setText(str_currentDate);
        } else {
            alrmDate.setText(prefs.getStr("alarm_date"));
        }

        if (prefs.getStr("alarm_time").equals("DNF")) {
            alrmTime.setText(str_currentTime);
        } else {
            alrmTime.setText(prefs.getStr("alarm_time"));
        }

        createNotificationChannel();

        image_AlrmTime.setOnClickListener(v -> showTimePicker());

        image_AlrmDate.setOnClickListener(v -> showDatePicker());

        btnCancelAlrm.setOnClickListener(v -> cancelAlarm());

        btnSaveAlarm.setOnClickListener(v -> setAlarm());
    }

    private void cancelAlarm() {
        Intent i = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_IMMUTABLE| PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled!", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) AlarmPage.this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_IMMUTABLE| PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        if(calendar != null){
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Alarm Saved Successfully!", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Select the time first", Toast.LENGTH_SHORT).show();
        }

    }

    private void showTimePicker() {

        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Recording Alarm Time")
                .build();

        timePicker.show(this.getSupportFragmentManager(), "alarmid");
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time;
                if (timePicker.getHour() >= 12) {
//                    alrmTime.setText(String.format("%02d", (timePicker.getHour()-12)+ " : "+ String.format("%02d", timePicker.getMinute())+ " PM"));
                    time = (timePicker.getHour() - 12) + " : " + timePicker.getMinute() + " PM";
                } else {
                    time = timePicker.getHour() + " : " + timePicker.getMinute() + " AM";
                }
                alrmTime.setText(time);
                prefs.setStr("alarm_time", time);

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

            }
        });
    }

    private void showDatePicker() {
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date").build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String date = "" + datePicker.getHeaderText();
            alrmDate.setText(date);
            prefs.setStr("alarm_date", date);
        });
        datePicker.show(this.getSupportFragmentManager(), "Tag");
    }

    public void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence alrnName = "AlarmReminder";
            String description = "Channel for Alarm manager";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = null;
            channel = new NotificationChannel("alarmid", alrnName, importance);
            channel.setDescription(description);

            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}