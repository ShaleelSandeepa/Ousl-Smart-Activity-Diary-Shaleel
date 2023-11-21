package com.ouslsmartactivitydiary.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeTransform;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.ouslsmartactivitydiary.AppearanceAdapter;
import com.ouslsmartactivitydiary.dialog.AppearanceDialog;
import com.ouslsmartactivitydiary.item.AppearanceItem;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity implements AppearanceDialog.OnDismissListener {

    private ImageView backIcon;
    private AppCompatTextView textView;
    private Toolbar actionBar;

    // recycler related
    private RecyclerView recyclerViewAppearance;
    private List<AppearanceItem> appearanceList;
    AppearanceAdapter appearanceAdapter;

    CardView appearanceCard;
    ImageView dropdownIcon;
    LinearLayout appearanceLinear, appearanceTopLinear;
    ScrollView settingsScrollView;
    SwitchMaterial mySwitchFingerprint, mySwitchNotification;
    DatabaseHelper databaseHelper;
    Cursor data, cursor;

    //Fingerprint related variables
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Boolean isFingerprintOn = false;
    Boolean isNotificationOn = false;
    Boolean authentication = false;

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        actionBar = findViewById(R.id.myActionBar);
        //Change action bar title
        textView = findViewById(R.id.title_actionbar);
        textView.setText("Settings");

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //change the status bar color
        getWindow().setStatusBarColor(getColor(R.color.white));
        //change the status bar to dark theme
        getWindow().getDecorView().setBackgroundColor(getColor(R.color.white));
        //change status bar color to dark
        if (Build.VERSION.SDK_INT >= 23){
            View decor = getWindow().getDecorView();
            if (decor.getSystemUiVisibility()!= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            else
                decor.setSystemUiVisibility(0);
        }

        settingsScrollView = findViewById(R.id.settingsScrollView);
        mySwitchFingerprint = findViewById(R.id.switchFingerprint);
        mySwitchNotification = findViewById(R.id.switchNotification);
        appearanceCard = findViewById(R.id.appearanceCard);
        dropdownIcon = findViewById(R.id.dropdownIcon);
        appearanceLinear = findViewById(R.id.appearanceLinear);
        appearanceTopLinear = findViewById(R.id.appearanceTopLinear);

        initializeContent();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NotifyDataSetChanged")
    private void initializeContent() {

        //generate settings from database
        cursor = databaseHelper.getSetting(1);
        if (cursor.moveToNext()){
            if (cursor.getInt(2) == 0) {
                isNotificationOn = false;
                mySwitchNotification.setChecked(false);
            } else if (cursor.getInt(2) == 1) {
                isNotificationOn = true;
                mySwitchNotification.setChecked(true);
            }
        }
        cursor = databaseHelper.getSetting(3);
        if (cursor.moveToNext()){
            if (cursor.getInt(2) == 0) {
                isFingerprintOn = false;
                mySwitchFingerprint.setChecked(false);
            } else if (cursor.getInt(2) == 1) {
                isFingerprintOn = true;
                mySwitchFingerprint.setChecked(true);
            }
        }

        recyclerViewAppearance = findViewById(R.id.recyclerViewAppearance);

        appearanceList = new ArrayList<>();

        appearanceList.add(new AppearanceItem(1, R.drawable.ic_ds_2, "DS"));
        appearanceList.add(new AppearanceItem(1, R.drawable.ic_ps_2, "PS"));
        appearanceList.add(new AppearanceItem(1, R.drawable.ic_labtest_2, "LAB"));
        appearanceList.add(new AppearanceItem(1, R.drawable.ic_viva_2, "VIVA"));
        appearanceList.add(new AppearanceItem(1, R.drawable.ic_assignment_2, "TMA"));
        appearanceList.add(new AppearanceItem(1, R.drawable.ic_quiz_2, "QUIZ"));
        appearanceList.add(new AppearanceItem(1, R.drawable.ic_cat_2, "CAT"));
        appearanceList.add(new AppearanceItem(1, R.drawable.ic_final_2, "FINAL"));

        appearanceAdapter = new AppearanceAdapter(this, appearanceList, new AppearanceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AppearanceItem position) {
                AppearanceDialog appearanceDialog = new AppearanceDialog(position);
                appearanceDialog.setOnDismissListener(SettingsActivity.this);
                appearanceDialog.show(getSupportFragmentManager(), "appearance_dialog");
            }
        });

        recyclerViewAppearance.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerViewAppearance.setAdapter(appearanceAdapter);
        appearanceAdapter.notifyDataSetChanged();

        //Notification Switch
        mySwitchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean isUpdated = databaseHelper.updateSettings(1, 1);
                    if (isUpdated) {
                        isNotificationOn = true;
                        Toast.makeText(SettingsActivity.this, "Notification Enabled", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Switch is in the off state
                    boolean isUpdated = databaseHelper.updateSettings(1, 0);
                    if (isUpdated) {
                        isNotificationOn = false;
                        Toast.makeText(SettingsActivity.this, "Notification Disable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Fingerprint Switch
        mySwitchFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean isUpdated = databaseHelper.updateSettings(3, 1);
                    if (isUpdated) {
                        Toast.makeText(SettingsActivity.this, "Fingerprint Enabled", Toast.LENGTH_SHORT).show();
                        isFingerprintOn = true;
                    }
                } else {
                    // Switch is in the off state
                    cursor = databaseHelper.getSetting(3);
                    if (cursor.getCount() != 0){
                        if (cursor.moveToFirst()) {
                            if (cursor.getInt(2) == 1) {
                                //user have to authenticate his fingerprint for turn off
                                fingerPrint();
                            }
                        }
                    }
                }
            }
        });

        appearanceTopLinear.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(appearanceLinear, new ChangeTransform());
                if (appearanceLinear.getVisibility() == View.GONE) {
                    startRotationAnimation(0f, 90f);
                    appearanceLinear.setVisibility(View.VISIBLE);
                } else {
                    startRotationAnimation(90f, 0f);
                    appearanceLinear.setVisibility(View.GONE);
                }
            }
        });
    }

    public void fingerPrint() {
        // FINGERPRINT CODES
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Device Doesn't have Fingerprint", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Not Working !", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No Fingerprint assigned !", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                break;
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
        }

        Executor executors = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(SettingsActivity.this, executors, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(SettingsActivity.this, "Authentication successful !", Toast.LENGTH_SHORT).show();
                authentication = true;
                checkAuthentication();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new  BiometricPrompt.PromptInfo.Builder().setTitle("OUSL SMART ACTIVITY DIARY")
                .setDescription("Authentication Required !").setDeviceCredentialAllowed(true).build();

        try {
            mySwitchFingerprint.setChecked(true);
            biometricPrompt.authenticate(promptInfo);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void checkAuthentication() {
        turnOffFingerPrint();
    }

    protected void turnOffFingerPrint() {
        boolean isUpdated = databaseHelper.updateSettings(3, 0);
        if (isUpdated) {
            Toast.makeText(SettingsActivity.this, "Fingerprint Disable", Toast.LENGTH_SHORT).show();
            isFingerprintOn = false;
            mySwitchFingerprint.setChecked(false);
        }
    }


    private void startRotationAnimation(float from, float to) {
        // Create an ObjectAnimator for the rotation property
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(dropdownIcon, "rotation", from, to);

        // Set the duration of the animation in milliseconds
        rotateAnimator.setDuration(500); // 1000 milliseconds = 1 second

        // Set the interpolator for smooth acceleration and deceleration
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        // Start the rotation animation
        rotateAnimator.start();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDismissListen() {
        initializeContent();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("key", "changed"); // Put any data you want to send back
        setResult(RESULT_OK, resultIntent);
    }
}