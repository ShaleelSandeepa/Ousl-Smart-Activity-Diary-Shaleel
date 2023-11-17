package com.ouslsmartactivitydiary.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ouslsmartactivitydiary.ItemNotification;
import com.ouslsmartactivitydiary.MyViewModel;
import com.ouslsmartactivitydiary.NotificationAdapter;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;
import com.ouslsmartactivitydiary.fragment.MyCalendarFragment;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements MyCalendarFragment.OnNotificationCountListener {

    RecyclerView recyclerViewNotification;
    List<ItemNotification> notificationList;
    NotificationAdapter notificationAdapter;

    Cursor cursor, data;
    DatabaseHelper databaseHelper;
    int accountID;
    Button readAll, deleteAll;
    LinearLayout noCourseLinear;
    AlertDialog.Builder alertDialog;
//    MyViewModel myViewModelNotification;

    private ImageView backIcon;
    private AppCompatTextView textView;
    private Toolbar actionBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        actionBar = findViewById(R.id.myActionBar);
        //Change action bar title
        textView = findViewById(R.id.title_actionbar);
        textView.setText("Notification");

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

        initializeContent();

        // Reference the SwipeRefreshLayout from the layout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_notification);

        // Set up the refresh listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeContent() {

        noCourseLinear = findViewById(R.id.noCourseLinear);

        recyclerViewNotification = findViewById(R.id.recyclerNotification);
        notificationList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        //get the notifications
        getNotifications();

        notificationAdapter = new NotificationAdapter(this, notificationList, new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onItemClick(ItemNotification position, int index, String clickType) {

                if (clickType.equals("delete")) {
//                    Intent intent = getIntent();
//                    finish(); // Finish the current instance of the activity
//                    startActivity(intent); // Start a new instance of the activity
                    initializeContent();
//                    getNotifications();
//                    notificationList.remove(index);
//                    notificationAdapter.notifyDataSetChanged();
                }

                //when read the notification it will refresh badge count from executing this codes
                if (clickType.equals("itemView")) {
//                    Cursor data = databaseHelper.getAllNotifications();
//                    if (data.moveToLast()) {
//                        int unread = 0;
//                        do {
//                            if (data.getString(3).equals("unread")) {
//                                unread++;
//                            }
//                        } while (data.moveToPrevious());
//                        //send the count of unread notifications to home activity via view model
//                        myViewModelNotification = new ViewModelProvider(NotificationActivity.this).get(MyViewModel.class);
//                        myViewModelNotification.setNotificationCount(unread);
//                    }
                    onNotificationCount();
                }
            }
        });

        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewNotification.setAdapter(notificationAdapter);
        notificationAdapter.notifyDataSetChanged();

    }

    public void getNotifications() {
        notificationList.clear();
        cursor = databaseHelper.getAllNotifications();
        if (cursor.getCount() == 0) {
            noCourseLinear.setVisibility(View.VISIBLE);
        } else {
            noCourseLinear.setVisibility(View.GONE);
        }

        if (cursor.moveToLast()) {
            int unread = 0;
            do {
                //add notifications to the recycler view
                notificationList.add(new ItemNotification(cursor.getInt(0), cursor.getString(4), cursor.getString(5),
                        cursor.getString(1), cursor.getString(2)));
                if (cursor.getString(3).equals("unread")) {
                    unread++;
                }
            } while (cursor.moveToPrevious());
            //send the count of unread notifications to home activity via view model
//            myViewModelNotification = new ViewModelProvider(this).get(MyViewModel.class);
//            myViewModelNotification.setNotificationCount(unread);
        }
    }

    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Call custom method to reinitialize or refresh the content
                initializeContent();

                // After refreshing, make sure to call setRefreshing(false) to stop the refresh animation.
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        // In the child activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("key", "notify"); // Put any data you want to send back
        setResult(RESULT_OK, resultIntent);
        finish();
        super.onBackPressed();

    }

    @Override
    public void onNotificationCount() {

    }
}