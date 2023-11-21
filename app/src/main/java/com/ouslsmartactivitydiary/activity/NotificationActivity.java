package com.ouslsmartactivitydiary.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ouslsmartactivitydiary.item.NotificationItem;
import com.ouslsmartactivitydiary.NotificationAdapter;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;
import com.ouslsmartactivitydiary.fragment.MyCalendarFragment;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements MyCalendarFragment.OnNotificationCountListener {

    RecyclerView recyclerViewNotification;
    List<NotificationItem> notificationList;
    NotificationAdapter notificationAdapter;

    Cursor cursor, data;
    DatabaseHelper databaseHelper;
    int accountID;

    View optionLayout;
    TextView markAllAsRead, deleteAll;
    LinearLayout noCourseLinear, optionMenu;
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

        //create option badge
        optionLayout = getLayoutInflater().inflate(R.layout.option_layout, null);
        optionMenu = findViewById(R.id.optionMenu);
        optionMenu.setVisibility(View.GONE);
        optionMenu.bringToFront();

        markAllAsRead = findViewById(R.id.markAllAsRead);
        deleteAll = findViewById(R.id.deleteAll);

        // Find the menu item you want to add the badge to
        MenuItem optionMenuItem = actionBar.getMenu().findItem(R.id.action_option);

        // Set the badge as the action view for the menu item
        optionMenuItem.setActionView(optionLayout);
        optionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionMenu.getVisibility() == View.VISIBLE) {
                    optionMenu.setVisibility(View.GONE);
                } else {
                    optionMenu.setVisibility(View.VISIBLE);
                }

            }
        });

        markAllAsRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                optionMenu.setVisibility(View.GONE);

                if (notificationList.size() == 0) {
                    Toast.makeText(NotificationActivity.this, "Notifications empty !", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog = new AlertDialog.Builder(NotificationActivity.this);
                    alertDialog.setTitle("Mark all as Read");
                    alertDialog.setMessage("Do you want to mark all as read?");

                    //When click "Yes" it will execute this
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int i = 0;
                            while (i < notificationList.size()) {
                                databaseHelper.updateNotificationState(notificationList.get(i).getNotificationID(), "read");
                                i++;
                            }
                            if (i == notificationList.size()) {
                                Toast.makeText(NotificationActivity.this, "Marked all As Read", Toast.LENGTH_SHORT).show();
                                initializeContent();
                            }
                        }
                    });

                    //When click "No" it will execute this
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }

            }
        });
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionMenu.setVisibility(View.GONE);

                if (notificationList.size() == 0) {
                    Toast.makeText(NotificationActivity.this, "Notifications empty !", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog = new AlertDialog.Builder(NotificationActivity.this);
                    alertDialog.setTitle("Delete All Notifications");
                    alertDialog.setMessage("Do you want to delete all notifications ?");

                    //When click "Yes" it will execute this
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseHelper.deleteAllNotifications();
                            Toast.makeText(NotificationActivity.this, "All notifications deleted", Toast.LENGTH_SHORT).show();
                            initializeContent();
                        }
                    });

                    //When click "No" it will execute this
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }

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
            public void onItemClick(NotificationItem position, int index, String clickType) {

                if (clickType.equals("delete")) {
                    initializeContent();
                }

                //when read the notification it will refresh badge count from executing this codes
                if (clickType.equals("itemView")) {
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
                notificationList.add(new NotificationItem(cursor.getInt(0), cursor.getString(4), cursor.getString(5),
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