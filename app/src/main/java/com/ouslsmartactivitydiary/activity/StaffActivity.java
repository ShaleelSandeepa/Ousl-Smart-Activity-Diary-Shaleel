package com.ouslsmartactivitydiary.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ouslsmartactivitydiary.CalendarAdapter;
import com.ouslsmartactivitydiary.CourseAdapter;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.StaffAdapter;
import com.ouslsmartactivitydiary.data.DatabaseHelper;
import com.ouslsmartactivitydiary.dialog.CalendarDialog;
import com.ouslsmartactivitydiary.item.CalendarItem;
import com.ouslsmartactivitydiary.item.CourseItem;
import com.ouslsmartactivitydiary.item.StaffItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffActivity extends AppCompatActivity {

    private ImageView backIcon;
    private AppCompatTextView textView;
    private Toolbar actionBar;

    //recycler
    List<StaffItem> staffItemList;
    List<CourseItem> courseItemList;
    RecyclerView recyclerAddedCourseLoad, recyclerStaff;
    CourseAdapter courseAdapter;
    StaffAdapter staffAdapter;

    //widgets
    EditText editTextCourse;
    ProgressBar progressBar;

    //database
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    DatabaseHelper databaseHelper;
    Cursor cursor;

    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        databaseHelper = new DatabaseHelper(this);

        actionBar = findViewById(R.id.myActionBar);
        //Change action bar title
        textView = findViewById(R.id.title_actionbar);
        textView.setText("Academic Staff");

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

        refreshData();

        // Reference the SwipeRefreshLayout from the layout
        swipeRefreshLayout = findViewById(R.id.swipRefresh_layout_staff);
        // Set up the refresh listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

    }

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    private void initializeContent() {
        editTextCourse = findViewById(R.id.editTextCourse);
        editTextCourse.setText("");

        progressBar = findViewById(R.id.progressBar);

        recyclerStaff = findViewById(R.id.recyclerStaff);
        recyclerAddedCourseLoad = findViewById(R.id.recyclerAddedCourseLoad);
        recyclerAddedCourseLoad.setVisibility(View.GONE);
        courseItemList = new ArrayList<>();
        staffItemList = new ArrayList<>();

        loadAllStaff("");

        // this will load calendar activities below to weekly calendar
        staffAdapter = new StaffAdapter(this, staffItemList, new StaffAdapter.OnStaffItemClickListener() {
            @Override
            public void onItemClick(StaffItem position, int index) {

            }
        });
        recyclerStaff.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerStaff.setAdapter(staffAdapter);
        staffAdapter.notifyDataSetChanged();

        editTextCourse.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"NotifyDataSetChanged"})
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // The cursor is now active
                    courseItemList.clear();
                    staffItemList.clear();
                    recyclerAddedCourseLoad.setVisibility(View.VISIBLE);

                    getAddedCourses();

                    courseAdapter = new CourseAdapter(StaffActivity.this, courseItemList, new CourseAdapter.OnItemClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onItemClick(CourseItem position) {
                            editTextCourse.setText((position.getCourseCode()+" - "+position.getCourseName()));
                            recyclerAddedCourseLoad.setVisibility(View.GONE);
                            loadAllStaff(position.getCourseCode());
                        }
                    });
                    recyclerAddedCourseLoad.setLayoutManager(new LinearLayoutManager(StaffActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerAddedCourseLoad.setAdapter(courseAdapter);
                    courseAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    public void getAddedCourses() {
        cursor = databaseHelper.getAllCourses();
        while (cursor.moveToNext()) {
            courseItemList.add(new CourseItem(6, cursor.getString(1), cursor.getString(2)));
        }
    }

    public void loadAllStaff(String cc) {

        progressBar.setVisibility(View.VISIBLE);

        collectionReference = firestore.collection("academicStaff");
        collectionReference.orderBy(FieldPath.documentId(), Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                String name = documentSnapshot.getString("name");
                                String email = documentSnapshot.getString("email");
                                Map<String, String> coursesData = (Map<String, String>) documentSnapshot.get("courses");

                                if (coursesData != null) {
                                    StringBuilder resultStringBuilder = new StringBuilder();
                                    for (Map.Entry<String, String> entry : coursesData.entrySet()) {
                                        String key = entry.getKey();
                                        String value = String.valueOf(entry.getValue());
                                        // Append the key-value pair to the StringBuilder with a new line
                                        resultStringBuilder.append(key).append(": ").append(value).append("\n");
                                    }
                                    String resultString = resultStringBuilder.toString();

                                    if (cc.equals("")) {
                                        staffItemList.add(new StaffItem(documentSnapshot.getId(), name, email, resultString));
                                    } else {
                                        for (Map.Entry<String, String> entry : coursesData.entrySet()) {
                                            if (entry.getKey().equals(cc)) {
                                                staffItemList.add(new StaffItem(documentSnapshot.getId(), name, email, resultString));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            staffAdapter.notifyDataSetChanged();
                        }
                    }
                });


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
}