package com.ouslsmartactivitydiary.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.ouslsmartactivitydiary.data.MyFirebaseMessagingService;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.ViewPagerAdapter;
import com.ouslsmartactivitydiary.data.DatabaseHelper;
import com.ouslsmartactivitydiary.fragment.MyCalendarFragment;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MyCalendarFragment.OnNotificationCountListener, MyFirebaseMessagingService.DataCallback {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;

    AppCompatTextView textView;
    private Toolbar actionBar;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    MenuItem notificationIcon;
    int notificationCount;
    View badgeLayout;
    TextView badgeCount;

    DatabaseHelper databaseHelper;
    Cursor itemsNotification, data;
    int accountID;

//    private MyViewModel myViewModelNotification;
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_SETTINGS = 2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService(this);

        databaseHelper = new DatabaseHelper(this);

        actionBar = findViewById(R.id.myActionBar);
        //Change action bar title
        textView = findViewById(R.id.title_actionbar);
        textView.setText("Dashboard");

        //drawer related
        drawerLayout =findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,actionBar,R.string.open_nav,R.string.close_nav);
        toggle.getDrawerArrowDrawable().setColorFilter(getResources().getColor(R.color.Theme), PorterDuff.Mode.SRC_OVER);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //create notification badge
        badgeLayout = getLayoutInflater().inflate(R.layout.badge_layout, null);
        badgeCount = badgeLayout.findViewById(R.id.badgeCount);

        //////////////////// those are set the badge number at the beginning //////////////
        itemsNotification = databaseHelper.getAllNotifications();
        //get the notifications by account ID
        if (itemsNotification.moveToLast()) {
            int unread = 0;
            do {
                if (itemsNotification.getString(3).equals("unread")) {
                    unread++;
                }
            } while (itemsNotification.moveToPrevious());
            badgeCount.setVisibility(View.VISIBLE);
            badgeCount.setText(String.valueOf(unread));
            if (unread == 0) {
                badgeCount.setVisibility(View.GONE);

            } else if (unread>99) {
                badgeCount.setVisibility(View.VISIBLE);
                badgeCount.setText("99+");

            }
        } else {
            badgeCount.setVisibility(View.GONE);
        }
        itemsNotification.close();

//        myViewModelNotification = new ViewModelProvider(this).get(MyViewModel.class);

        // Observe the myViewModelNotification LiveData object
//        myViewModelNotification.getNotificationCount().observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer integer) {
//                badgeCount.setText(String.valueOf(integer));
//            }
//        });


        // Find the menu item you want to add the badge to
        MenuItem notificationMenuItem = actionBar.getMenu().findItem(R.id.action_notification);

        // Set the badge as the action view for the menu item
        notificationMenuItem.setActionView(badgeLayout);
        badgeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivityForResult(intentNotification, REQUEST_CODE);
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

        // set variables for tab bar and bottom navigation bar
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);


        // Implementation of tab navigation
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // select tab position
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    //When try to back from starting screen this method will execute
    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Exit App");
            alertDialog.setMessage("Do you want to exit ?");

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
//                biometricPrompt.authenticate(promptInfo);
                }
            });
            alertDialog.show();
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_staff:
                Intent intentStaff = new Intent(this, StaffActivity.class);
                startActivity(intentStaff);
                break;

            case R.id.nav_profile:
                Intent intentProfile = new Intent(this, ProfileActivity.class);
                startActivity(intentProfile);
                break;

            case R.id.nav_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivityForResult(intentSettings, REQUEST_CODE_SETTINGS);
                break;

            case R.id.nav_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }, 1000);

        return true;
    }

    @Override
    public void onNotificationCount() {
        //////////////////// those are set the badge number at the beginning //////////////
        itemsNotification = databaseHelper.getAllNotifications();
        //get the notifications by account ID
        if (itemsNotification.moveToLast()) {
            int unread = 0;
            do {
                if (itemsNotification.getString(3).equals("unread")) {
                    unread++;
                }
            } while (itemsNotification.moveToPrevious());
            badgeCount.setVisibility(View.VISIBLE);
            badgeCount.setText(String.valueOf(unread));
            if (unread == 0) {
                badgeCount.setVisibility(View.GONE);

            } else if (unread>99) {
                badgeCount.setVisibility(View.VISIBLE);
                badgeCount.setText("99+");

            }
        } else if (itemsNotification.getCount() == 0) {
            badgeCount.setText("0");
            badgeCount.setVisibility(View.GONE);
        }
        itemsNotification.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Handle the result from the child activity
                // You can access data from the Intent if the child activity passed any
                String resultData = data.getStringExtra("key");
                onNotificationCount();
                // Process the resultData as needed
            } else if (resultCode == RESULT_CANCELED) {
                // Handle the case where the user canceled the operation in the child activity
            }
        }

        if (requestCode == REQUEST_CODE_SETTINGS && data != null) {
            Toast.makeText(this, "Please refresh for color changes !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDataReceived() {
        onNotificationCount();
    }
}