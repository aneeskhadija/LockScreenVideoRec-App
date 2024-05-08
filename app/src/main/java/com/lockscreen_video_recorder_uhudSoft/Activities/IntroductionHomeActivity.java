package com.lockscreen_video_recorder_uhudSoft.Activities;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.lockscreen_video_recorder_uhudSoft.Adapters.ViewPagerAdapter;
import com.lockscreen_video_recorder_uhudSoft.MainActivity;
import com.lockscreen_video_recorder_uhudSoft.R;

import java.util.List;

import butterknife.internal.Utils;

public class IntroductionHomeActivity extends AppCompatActivity {
    ViewPager mSLideViewPager;
    LinearLayout mDotLayout;
    Button backbtn, nextbtn, skipbtn, allowBtn;
    private static final String TAG = "IntroMainActivity";

    private boolean drawOverlays = false;
    private AppOpsManager.OnOpChangedListener onOpChangedListener = null;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_introduction_home);
        getSupportActionBar().hide();

        backbtn = findViewById(R.id.backbtn);
        nextbtn = findViewById(R.id.nextbtn);
        skipbtn = findViewById(R.id.skipButton);
        allowBtn = findViewById(R.id.btn_allow);


        if (checkCameraPermission() && checkMicrophonePermission() && checkStoragePermission() && checkSystemAlertWindowPermission()) {
            Intent i = new Intent(IntroductionHomeActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        if (checkCameraPermission()) {
            allowBtn.setClickable(false);
            allowBtn.setText(R.string.allowed);
            if (SDK_INT >= Build.VERSION_CODES.M) {
                allowBtn.setBackgroundColor(getColor(R.color.inactive));
            }
        } else {
            allowBtn.setClickable(true);
            allowBtn.setText(R.string.allow);
            if (SDK_INT >= Build.VERSION_CODES.M) {
                allowBtn.setBackgroundColor(getColor(R.color.primaryback));
            }
        }


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getitem(0) > 0) {

                    mSLideViewPager.setCurrentItem(getitem(-1), true);

                }
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getitem(0) < 3) {
                    mSLideViewPager.setCurrentItem(getitem(1), true);

                } else {

                    Intent i = new Intent(IntroductionHomeActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

                }
            }
        });

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(IntroductionHomeActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        allowBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (getitem(0) <= 3) {
                    if (mSLideViewPager.getCurrentItem() == 0) {
                        if (checkCameraPermission()) {
                            mSLideViewPager.setCurrentItem(getitem(1), true);

                        } else {
                            requestCameraPermission();
                        }

                    } else if (mSLideViewPager.getCurrentItem() == 1) {
                        if (checkMicrophonePermission()) {
                            mSLideViewPager.setCurrentItem(getitem(1), true);

                        } else {
                            requestMicroPhonePermission();
                        }
                    } else if (mSLideViewPager.getCurrentItem() == 2) {
                        if (checkStoragePermission()) {
                            //main logic or main code
                            mSLideViewPager.setCurrentItem(getitem(1), true);

                        } else {
                            requestStoragePermission();
                        }
                    } else if (mSLideViewPager.getCurrentItem() == 3) {

                        if (checkSystemAlertWindowPermission()) {
                            //main logic or main code
                            Intent i = new Intent(IntroductionHomeActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();

                        } else {
                            requestWindowOverlayPermission();
                        }

                    }
                } else {

                    Intent i = new Intent(IntroductionHomeActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });

        mSLideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        viewPagerAdapter = new ViewPagerAdapter(this);

        mSLideViewPager.setAdapter(viewPagerAdapter);

        setUpindicator(0);
        mSLideViewPager.addOnPageChangeListener(viewListener);
    }

    public void setUpindicator(int position) {

        dots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            if (SDK_INT >= Build.VERSION_CODES.M) {
                dots[i].setTextColor(getResources().getColor(R.color.inactive, getApplicationContext().getTheme()));
            }
            mDotLayout.addView(dots[i]);

        }

        if (SDK_INT >= Build.VERSION_CODES.M) {
            dots[position].setTextColor(getResources().getColor(R.color.active, getApplicationContext().getTheme()));
        }

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onPageSelected(int position) {

            Log.d(TAG, "onPageSelected: " + position);

            setUpindicator(position);

            if (position > 0) {

                backbtn.setVisibility(View.VISIBLE);

            } else {

                backbtn.setVisibility(View.INVISIBLE);

            }

            if (position == 0) {
                if (checkCameraPermission()) {
                    allowBtn.setClickable(false);
                    allowBtn.setText(R.string.allowed);
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        allowBtn.setBackgroundColor(getColor(R.color.inactive));
                    }
                } else {
                    allowBtn.setClickable(true);
                    allowBtn.setText(R.string.allow);
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        allowBtn.setBackgroundColor(getColor(R.color.primaryback));
                    }
                }
            } else if (position == 1) {

                if (checkMicrophonePermission()) {
                    allowBtn.setClickable(false);
                    allowBtn.setText(R.string.allowed);
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        allowBtn.setBackgroundColor(getColor(R.color.inactive));
                    }
                } else {
                    allowBtn.setClickable(true);
                    allowBtn.setText(R.string.allow);
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        allowBtn.setBackgroundColor(getColor(R.color.primaryback));
                    }
                }

            } else if (position == 2) {

                if (checkStoragePermission()) {
                    allowBtn.setClickable(false);
                    allowBtn.setText(R.string.allowed);
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        allowBtn.setBackgroundColor(getColor(R.color.inactive));
                    }
                } else {
                    allowBtn.setClickable(true);
                    allowBtn.setText(R.string.allow);
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        allowBtn.setBackgroundColor(getColor(R.color.primaryback));
                    }
                }

            } else if (position == 3) {

                if (checkSystemAlertWindowPermission()) {
                    allowBtn.setClickable(false);
                    allowBtn.setText(R.string.allowed);
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        allowBtn.setBackgroundColor(getColor(R.color.inactive));
                    }
                } else {
                    allowBtn.setClickable(true);
                    allowBtn.setText(R.string.allow);
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        allowBtn.setBackgroundColor(getColor(R.color.primaryback));
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getitem(int i) {

        return mSLideViewPager.getCurrentItem() + i;
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private boolean checkStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private boolean checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkSystemAlertWindowPermission() {
        return Settings.canDrawOverlays(this);
    }

    private void requestCameraPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        mSLideViewPager.setCurrentItem(getitem(1), true);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()) {
                            // navigate user to app settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread().check();

    }

    private void requestMicroPhonePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        mSLideViewPager.setCurrentItem(getitem(1), true);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        if (permissionDeniedResponse.isPermanentlyDenied()) {
                            // navigate user to app settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread().check();

    }

    private void requestStoragePermission() {

        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this).withPermissions(Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            mSLideViewPager.setCurrentItem(getitem(1), true);
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            // navigate user to app settings
                            showSettingsDialog();
                        }
                    }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                    .onSameThread().check();
        } else {
            Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            mSLideViewPager.setCurrentItem(getitem(1), true);
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            // navigate user to app settings
                            showSettingsDialog();
                        }
                    }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                    .onSameThread().check();
        }
    }

    private void requestWindowOverlayPermission() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.getPackageName()));
            startActivity(intent);
        }

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(IntroductionHomeActivity.this);
        builder.setTitle("Camera Permission");
        builder.setMessage("Give Permission to Start Camera Recording");
        builder.setPositiveButton("Go to Settings", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

}