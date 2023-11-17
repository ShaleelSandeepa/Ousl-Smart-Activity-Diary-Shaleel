package com.ouslsmartactivitydiary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

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

        Intent intentDashboard = new Intent(this, DashboardActivity.class);
        startActivity(intentDashboard);
    }

}