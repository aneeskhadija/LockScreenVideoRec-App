package com.lockscreen_video_recorder_uhudSoft.Utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.lockscreen_video_recorder_uhudSoft.MainActivity;
import com.lockscreen_video_recorder_uhudSoft.R;

public class AlarmReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

//        if (preferences.getBoolean("key_show_notification", false)) {


        try{
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarmid")
                    .setSmallIcon(R.drawable.ic_stopwatch)
                    .setContentTitle("Recording Starting")
                    .setContentText("Alarm recording has been started")
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123, builder.build());

        }catch (Exception e){
            e.printStackTrace();
            Log.d("NotificationError", "onReceive: "+e.getMessage());
        }
//        }

        sendMessage(context);

//        context.sendBroadcast(new Intent("alarm_started"));


    }

    private void sendMessage(Context context) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("alarm_started");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
