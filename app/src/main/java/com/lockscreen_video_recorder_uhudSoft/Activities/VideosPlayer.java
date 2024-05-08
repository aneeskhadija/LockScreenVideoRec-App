package com.lockscreen_video_recorder_uhudSoft.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lockscreen_video_recorder_uhudSoft.R;

import java.io.File;

public class VideosPlayer extends AppCompatActivity {

    private static final String TAG = "VideoPlayActivity";

    String path;
    SeekBar videoSeekbar;
    VideoView videoView;
    MediaController mediaController;
    ImageButton shareBtn, deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_videos_player);
        getSupportActionBar().hide();

        videoView = findViewById(R.id.video_view);
        shareBtn = findViewById(R.id.id_shareVideo);
        deleteBtn = findViewById(R.id.id_deleteVideo);
        mediaController = new MediaController(this);

        Intent intent1 = getIntent();
        path = intent1.getStringExtra("video");

        videoView.setVideoPath(path);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();

        shareBtn.setOnClickListener(view -> new ShareCompat.IntentBuilder(VideosPlayer.this).setType("video/*")
                .setStream(Uri.parse(path)).setChooserTitle("Share Video").startChooser());

        deleteBtn.setOnClickListener(view -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(VideosPlayer.this);
            alertDialogBuilder.setMessage("Do you want to delete this video?");
            alertDialogBuilder.setPositiveButton("Delete", (dialogInterface, i) -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    String[] projection = new String[]{MediaStore.Video.Media._ID};
                    String selection = MediaStore.Video.Media.DATA + " = ?";
                    String[] selectionArgs = new String[]{new File(path).getAbsolutePath()};
                    Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                    if (cursor.moveToFirst()) {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                        try {
                            contentResolver.delete(deleteUri, null, null);
                            boolean delete = new File(path).delete();
                            Log.e(TAG, delete + " onClick: deleted ");
                            Toast.makeText(VideosPlayer.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(VideosPlayer.this, "Error Deleting!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(VideosPlayer.this, "File Not Found.", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();

                }else {
                    File fdelete = new File(path);
                    if (fdelete.exists()) {

                        if (fdelete.delete()) {
                            Toast.makeText(VideosPlayer.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));
                        } else {
                            Toast.makeText(VideosPlayer.this, "Error Deleting", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

            });

            alertDialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            alertDialogBuilder.show();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}