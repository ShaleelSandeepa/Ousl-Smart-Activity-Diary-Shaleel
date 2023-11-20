package com.ouslsmartactivitydiary.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    String msg;
    ConstraintLayout mainConstraint;

    Boolean authentication = false;

    //Fingerprint related variables
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    DatabaseHelper databaseHelper;
    Cursor cursor, colorCursor;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        mainConstraint = findViewById(R.id.mainConstraint);
        mainConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("News")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        msg = "Done";
                        if (!task.isSuccessful()){
                            msg = "Failed";
                        }
                    }
                });

        databaseHelper = new DatabaseHelper(this);
        //generate settings from database
        cursor = databaseHelper.getSetting(1);
        if (cursor.getCount() == 0){
            databaseHelper.addSettings(1, "notification", 0);
        }
        cursor = databaseHelper.getSetting(2);
        if (cursor.getCount() == 0){
            databaseHelper.addSettings(2, "lightSensor", 0);
        }
        cursor = databaseHelper.getSetting(3);
        if (cursor.getCount() == 0){
            databaseHelper.addSettings(3, "fingerPrint", 0);
            goSplash();
        } else {
            // if fingerprint is on in database,
            if (cursor.moveToNext()) {
                if (cursor.getInt(2) == 1) {
                    //user have to authenticate his fingerprint for login
                    fingerPrint();
                } else {
                    goSplash();
                }
            }
        }
        cursor.close();

        saveColors();
    }

    public void saveColors() {
        colorCursor = databaseHelper.getAllColors();
        if (colorCursor.getCount() == 0) {
            databaseHelper.addColor(1, "LAB", getResources().getColor(R.color.LAB));
            databaseHelper.addColor(2, "QUIZ", getResources().getColor(R.color.QUIZ));
            databaseHelper.addColor(3, "PS", getResources().getColor(R.color.PS));
            databaseHelper.addColor(4, "VIVA", getResources().getColor(R.color.VIVA));
            databaseHelper.addColor(5, "DS", getResources().getColor(R.color.DS));
            databaseHelper.addColor(6, "TMA", getResources().getColor(R.color.TMA));
            databaseHelper.addColor(7, "CAT", getResources().getColor(R.color.CAT));
            databaseHelper.addColor(8, "FINAL", getResources().getColor(R.color.FINAL));
        }
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

        biometricPrompt = new BiometricPrompt(MainActivity.this, executors, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Authentication successful !", Toast.LENGTH_SHORT).show();
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
            biometricPrompt.authenticate(promptInfo);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void checkAuthentication() {
        goSplash();
    }

    protected void goSplash() {
        Intent splash = new Intent(this, SplashActivity.class);
        startActivity(splash);
    }

    //When try to back from starting screen this method will execute
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to exit OUSL Smart Activity Diary ?");

        //When click "Yes" it will execute this
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                onStop();
                onDestroy();
            }
        });

        //When click "No" it will execute this
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                biometricPrompt.authenticate(promptInfo);
            }
        });
        alertDialog.show();
    }

}