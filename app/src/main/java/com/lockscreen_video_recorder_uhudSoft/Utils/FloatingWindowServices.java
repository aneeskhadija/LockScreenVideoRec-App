package com.lockscreen_video_recorder_uhudSoft.Utils;

import static android.view.View.GONE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.legacy.app.FragmentCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lockscreen_video_recorder_uhudSoft.MainActivity;
import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;
import com.lockscreen_video_recorder_uhudSoft.ui.CameraVideoFrag;
import com.lockscreen_video_recorder_uhudSoft.ui.HomeFrag;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

public class FloatingWindowServices extends Service {

    private static final String TAG = "FloatingWindowService";

    private static final boolean DEBUG = true;
    private Chronometer mChronometer;

    public WindowManager windowManager;
    public WindowManager.LayoutParams params;
    public View myView;
    Context context;
    ConstraintLayout cameraLayout;
    SharedPrefs prefs;

    private final int noWidth = 1;
    private final int smallWidth = 300;
    private final int mediumWidth = 400;
    private final int largeWidth = 500;

    private final int noHeight = 1;
    private final int smallHeight = 450;
    private final int mediumHeight = 550;
    private final int largeHeight = 650;

    private int overlayWidth;
    private int overlayHeight;

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    private static final int REQUEST_VIDEO_PERMISSIONS = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private AutoFitTextureView mTextureView;
    private Button mButtonVideo;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mPreviewSession;

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    private Size mPreviewSize;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    public static boolean mIsRecordingVideo;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;

            try {
                startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mCameraOpenCloseLock.release();
            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

    };
    private Integer mSensorOrientation;
    private String mNextVideoAbsolutePath;
    private CaptureRequest.Builder mPreviewBuilder;

    /*public static FragCameraVideo newInstance() {
        return new FragCameraVideo();
    }*/

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    SharedPreferences preferences;

    //commented the onCreate Method and pasted all the code in on command method.

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service running");

        context = this;

        try {
            showNotificationForeground();
            Intent foregroundIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, foregroundIntent,
                    PendingIntent.FLAG_IMMUTABLE);

            Notification notification = new NotificationCompat.Builder(this, "ForeGroundID")
                    .setContentTitle("LockScreen Video Recorder")
                    .setContentText("LockScreen Video Recording started!")
                    .setSmallIcon(R.drawable.notif_cam_icon)
                    .setContentIntent(pendingIntent).build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startForeground(123, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
                        | ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
            } else {
                startForeground(123, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("NotificationError", "onStartCommand: " + e.getMessage());
        }

        Log.d(TAG, "onCreate: Service started");

        myView = View.inflate(context, R.layout.overlay_layout, null);

        prefs = new SharedPrefs(context);
        cameraLayout = myView.findViewById(R.id.id_CameraLayout);
        mChronometer = (Chronometer) myView.findViewById(R.id.chronometer);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        String prevId = preferences.getString("preview_size_value", "3");

        String prevId = prefs.getStr("preview_size_value", "3");

        if (prevId.equals("0")) {
            overlayWidth = smallWidth;
            overlayHeight = smallHeight;
        } else if (prevId.equals("1")) {
            overlayWidth = mediumWidth;
            overlayHeight = mediumHeight;
        } else if (prevId.equals("2")) {
            overlayWidth = largeWidth;
            overlayHeight = largeHeight;
        } else {
            overlayWidth = noWidth;
            overlayHeight = noHeight;
        }

        params = new WindowManager.LayoutParams(
                overlayWidth,
                overlayHeight,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.START | Gravity.CENTER;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        windowManager.addView(myView, params);

        try {
            windowManager.addView(myView, params);
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would not display
            // the 'Force Close' message
        }

        mTextureView = (AutoFitTextureView) myView.findViewById(R.id.texture);
        mButtonVideo = (Button) myView.findViewById(R.id.video);
        mButtonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: mIsCameraRecording " + mIsRecordingVideo);

                if (mIsRecordingVideo) {
                    stopRecordingVideo();
                    sendMessage();
                    windowManager.removeView(myView);
                    stopForeground(true);
                    stopSelf();

                } else {

                    try {
                        startRecordingVideo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());

        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        }
        Log.d(TAG, "onCreate: mTextureView.isAvailable() " + mTextureView.isAvailable());

        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("AD", "Action E" + event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("AD", "Action Down");
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d("AD", "Action Up");
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);
                        if (Xdiff < 10 && Ydiff < 10) {
//                                if (isViewCollapsed()) {
//                                    collapsedView.setVisibility(View.GONE);
//                                    expandedView.setVisibility(View.VISIBLE);
//                                }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("AD", "Action Move");
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        try {
                            windowManager.updateViewLayout(myView, params);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                }
                return false;
            }
        });

        return START_STICKY;
    }

    private void showNotificationForeground() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("ForeGroundID",
                    "Foreground Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called");

        try {
            if (myView != null) {
                windowManager.removeView(myView);
                prefs.setBool("isRecordingVideo", false);
                stopRecordingVideo();

                if (mIsRecordingVideo) {
                    stopRecordingVideo();
                    mIsRecordingVideo = false;
                    prefs.setBool("isRecordingVideo", false);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        if (DEBUG) Log.i("service_configurations", "onConfigurationChanged(Configuration)");

    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(int width, int height) {

        try {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                Log.d(TAG, "tryAcquire");

                String cameraId;
                if (prefs.getStr("video_camera_value", "0").equals("0")) {
                    cameraId = manager.getCameraIdList()[0];
                } else {
                    cameraId = manager.getCameraIdList()[1];
                }

                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics
                        .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                if (map == null) {
                    throw new RuntimeException("Cannot get available preview/video sizes");
                }
                mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        width, height, mVideoSize);

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }
                configureTransform(width, height);
                mMediaRecorder = new MediaRecorder();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                manager.openCamera(cameraId, mStateCallback, null);
            } catch (CameraAccessException e) {
                Toast.makeText(context, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException | ArrayIndexOutOfBoundsException e) {

        }


    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    private void startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {

            closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                  Activity activity = getActivity();
//                  if (null != activity) {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
//                  }
                        }
                    }, mBackgroundHandler);

            if (mIsRecordingVideo) {
                stopRecordingVideo();
                windowManager.removeView(myView);
                mIsRecordingVideo = false;
                prefs.setBool("isRecordingVideo", false);
                stopForeground(true);
                stopSelf();
                Log.d("startPreview", "Recording stopped in startpreview");

            } else {
                Log.d("startPreview", "Recording started in startpreview");

                try {
                    startRecordingVideo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        try {
            if (null == mCameraDevice) {
                return;
            }

            try {
                setUpCaptureRequestBuilder(mPreviewBuilder);
                HandlerThread thread = new HandlerThread("CameraPreview");
                thread.start();
                mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException| IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    private void configureTransform(int viewWidth, int viewHeight) {
//    Activity activity = getActivity();
//    if (null == mTextureView || null == mPreviewSize || null == activity) {
//      return;
//    }

        int rotation = windowManager.getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private void setUpMediaRecorder() throws IOException {
//    final Activity activity = getActivity();
//    if (null == activity) {
//      return;
//    }

//
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        // this code is used to get the directory path for video but i am commenting and using another dir.
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
//            mNextVideoAbsolutePath = getVideoFilePath(context);
        }

        // getOutputMediaFile method creates and returns a file directory (Folder).
//        File mCurrentFile = getOutputMediaFile();
//        mMediaRecorder.setOutputFile(mCurrentFile);
//        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        getVideoPath();

        CamcorderProfile profile;
//        String videoQuality = preferences.getString("video_quality_value", "1");
        String videoQuality = prefs.getStr("video_quality_value", "1");

        if (videoQuality.equals("0")) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);

        } else if (videoQuality.equals("1")) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);

        } else if (videoQuality.equals("2")) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);

        } else {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        }

        mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
        mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioEncodingBitRate(profile.audioBitRate);
        mMediaRecorder.setAudioSamplingRate(profile.audioSampleRate);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        int rotation = windowManager.getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mMediaRecorder.prepare();
    }

    private static final String VIDEO_DIRECTORY_NAME = "LockscreenRecorder";

    public File getOutputMediaFile() {

        // External sdcard file location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                VIDEO_DIRECTORY_NAME);
//         Create storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "VID_" + timeStamp + ".mp4");
        return mediaFile;
    }

    private String getVideoFilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + System.currentTimeMillis() + ".mp4";
    }

    private void getVideoPath() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        String videoFileName = "video_" + timeStamp;
//        String videoFileName = "video_" + System.currentTimeMillis() + ".mp4";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver contentResolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, videoFileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + File.separator + VIDEO_DIRECTORY_NAME);
//            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
//            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);

            ContentResolver resolver = context.getContentResolver();
            Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri uriSavedVideo = resolver.insert(collection, contentValues);

            ParcelFileDescriptor pfd;
            try {
                pfd = context.getContentResolver().openFileDescriptor(uriSavedVideo, "w");

                mMediaRecorder.setOutputFile(pfd.getFileDescriptor());

                FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
                // Get the already saved video as fileinputstream from here
                File storageDir = new File(
                        context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                        VIDEO_DIRECTORY_NAME);
                File imageFile = new File(storageDir, "Myvideo");

                FileInputStream in = new FileInputStream(imageFile);

                byte[] buf = new byte[16834];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                out.close();
                in.close();
                pfd.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            contentValues.clear();
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
            context.getContentResolver().update(uriSavedVideo, contentValues, null, null);

        } else {
            File mCurrentFile = getOutputMediaFile();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mMediaRecorder.setOutputFile(mCurrentFile);
            }
        }

    }

    public void startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {

            Toast.makeText(context, "Recording Starting...", Toast.LENGTH_SHORT).show();

            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {

                            try{
                                // UI
                                mButtonVideo.setText(R.string.str_Stop);
                                mIsRecordingVideo = true;
                                prefs.setBool("isRecordingVideo", true);

                                // Start recording
                                mMediaRecorder.start();

                                mChronometer.setBase(SystemClock.elapsedRealtime());
                                mChronometer.setVisibility(View.VISIBLE);
                                mChronometer.start();
                            }
                            catch (NullPointerException e){

                            }


                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }

    }

    private void closePreviewSession() {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            try {
                mPreviewSession.stopRepeating();
                mPreviewSession.abortCaptures();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;

//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
//                try {
//                    mPreviewSession.stopRepeating();
//                    mPreviewSession.abortCaptures();
//                } catch (CameraAccessException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    private void stopRecordingVideo() {
        // UI
        mIsRecordingVideo = false;
        prefs.setBool("isRecordingVideo", false);
        mButtonVideo.setText(R.string.str_Record);
        // Stop recording
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeCamera();

//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(getOutputMediaFile().getAbsolutePath())));
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(mNextVideoAbsolutePath)));

        mChronometer.stop();
        mChronometer.setVisibility(View.INVISIBLE);
//        new MainActivity().stopChronometer();

        Toast.makeText(context, "Video saved Successfully!!!",
                Toast.LENGTH_SHORT).show();

    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    public static class ConfirmationDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.str_PermissionRequest)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentCompat.requestPermissions(parent, VIDEO_PERMISSIONS,
                                    REQUEST_VIDEO_PERMISSIONS);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    parent.getActivity().finish();
                                }
                            })
                    .create();
        }

    }

    // Send an Intent with an action named "custom-event-name". The Intent sent should
// be received by the ReceiverActivity.
    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}