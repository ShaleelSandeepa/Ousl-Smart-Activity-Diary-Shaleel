package com.ouslsmartactivitydiary.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ouslsmartactivitydiary.CalendarAdapter;
import com.ouslsmartactivitydiary.dialog.CalendarDialog;
import com.ouslsmartactivitydiary.item.CalendarItem;
import com.ouslsmartactivitydiary.CourseAdapter;
import com.ouslsmartactivitydiary.item.CourseItem;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseWiseFragment extends Fragment {

    Context context;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    String fieldName = "date";
    DatabaseHelper databaseHelper;
    Cursor cursor;

    //widgets
    EditText editTextCourse;

    //recycler
    List<CalendarItem> activityItemList;
    List<CourseItem> courseItemList;
    RecyclerView recyclerCourseWiseActivities, recyclerAddedCourseLoad;
    CourseAdapter courseAdapter;
    CalendarAdapter calendarAdapter;

    Date currentDate, yesterday, timeStamp, endTime;
    String category, courseCode, activity, date, dateMonth, day, note;
    int icon;
    String[] datePart;
    boolean isFound = false;
    boolean isRefreshed = false;

    private SwipeRefreshLayout swipe_refresh_layout_courses;
    private View rootView;
    LinearLayout noCourseLinear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_course_wise, container, false);
        swipe_refresh_layout_courses = rootView.findViewById(R.id.swipe_refresh_layout_course_wise);
        // Set the refresh listener
        swipe_refresh_layout_courses.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeContent();

    }

    @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
    private void initializeContent() {

        databaseHelper = new DatabaseHelper(getContext());
        noCourseLinear = rootView.findViewById(R.id.noCourseLinear);

        cursor = databaseHelper.getAllCourses();
        if (cursor.getCount() == 0) {
            noCourseLinear.setVisibility(View.VISIBLE);
        } else {
            noCourseLinear.setVisibility(View.GONE);
        }

        recyclerAddedCourseLoad = rootView.findViewById(R.id.recyclerAddedCourseLoad);
        recyclerAddedCourseLoad.setVisibility(View.GONE);
        courseItemList = new ArrayList<>();
        activityItemList = new ArrayList<>();

        editTextCourse = rootView.findViewById(R.id.editTextCourse);
        editTextCourse.setText("");

        activityItemList.clear();
        recyclerCourseWiseActivities = rootView.findViewById(R.id.recyclerCourseWiseActivities);

        // this will load calendar activities below to weekly calendar
        calendarAdapter = new CalendarAdapter(getContext(), activityItemList, new CalendarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CalendarItem position) {
                CalendarDialog calendarDialog = new CalendarDialog(position);
                calendarDialog.show(getParentFragmentManager(), "calendar_dialog");
            }
        });
        recyclerCourseWiseActivities.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerCourseWiseActivities.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();

        editTextCourse.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"NotifyDataSetChanged"})
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    cursor = databaseHelper.getAllCourses();
                    if (cursor.getCount() == 0) {
                        noCourseLinear.setVisibility(View.VISIBLE);
                    } else {
                        noCourseLinear.setVisibility(View.GONE);
                    }
                    // The cursor is now active
                    courseItemList.clear();
                    activityItemList.clear();
                    recyclerAddedCourseLoad.setVisibility(View.VISIBLE);

                    getAddedCourses();

                    courseAdapter = new CourseAdapter(getContext(), courseItemList, new CourseAdapter.OnItemClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onItemClick(CourseItem position) {
                            editTextCourse.setText((position.getCourseCode()+" - "+position.getCourseName()));
                            recyclerAddedCourseLoad.setVisibility(View.GONE);
                            addCourseWiseActivities(position.getCourseCode());
                        }
                    });
                    recyclerAddedCourseLoad.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recyclerAddedCourseLoad.setAdapter(courseAdapter);
                    courseAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });



        editTextCourse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    @SuppressLint("NotifyDataSetChanged")
    public void addCourseWiseActivities(String cc) {
        loadDetails(activityItemList, cc);
        calendarAdapter.notifyDataSetChanged();
    }

    public void loadDetails(List<CalendarItem> activityItemList, String cc) {

        String activityCollection = " ";

        cursor = databaseHelper.getUserData(1);
        if (cursor.moveToFirst()) {
            activityCollection = cursor.getString(5);
            if (activityCollection.equals("")) {
                Toast.makeText(context, "Please select your Programme in your Profile", Toast.LENGTH_SHORT).show();
                activityCollection = " ";
            }
        }

        collectionReference = FirebaseFirestore.getInstance().collection(activityCollection);
        collectionReference.orderBy(fieldName, Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for (DocumentChange documentChange : value.getDocumentChanges()) {

                            if (documentChange.getDocument().getString("courseCode").equals(cc)) {
                                // check document change type
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    addList(documentChange);
                                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    for (int i=0; i<activityItemList.size(); i++) {
                                        if (activityItemList.get(i).getId().equals(documentChange.getDocument().getId())){
                                            isFound = true;
                                            updateList(documentChange, i);
                                        }
                                    }
                                    // add activity only if not fount in the Array list, but in the firebase
                                    if (!isFound){
                                        isRefreshed = false;
                                        addList(documentChange);
                                    }
                                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                    for (int i=0; i<activityItemList.size(); i++) {
                                        if (activityItemList.get(i).getId().equals(documentChange.getDocument().getId())){
                                            activityItemList.remove(i);
                                        }
                                    }
                                }
                            }

                            calendarAdapter.notifyDataSetChanged();

                        }
                    }
                });
    }

    public void addList(DocumentChange documentChange) {
        currentDate = new Date();
        fetchDataFromDocument(documentChange);
        if(documentChange.getDocument().getTimestamp("endTime") != null) {
            if (documentChange.getDocument().getTimestamp(fieldName).compareTo(new Timestamp(currentDate)) >= 0 ||
                    documentChange.getDocument().getTimestamp("endTime").compareTo(new Timestamp(currentDate)) >= 0) {
                activityItemList.add(new CalendarItem(3, category, documentChange.getDocument().getId(), icon, courseCode, activity, dateMonth, datePart[0], timeStamp, endTime, note));
                isFound = false;
            }
        } else {
            if (documentChange.getDocument().getTimestamp(fieldName).compareTo(new Timestamp(currentDate)) >= 0) {
                activityItemList.add(new CalendarItem(3, category, documentChange.getDocument().getId(), icon, courseCode, activity, dateMonth, datePart[0], timeStamp, endTime, note));
                isFound = false;
            }
        }

    }

    public void updateList(DocumentChange documentChange, int index) {
        currentDate = new Date();
        fetchDataFromDocument(documentChange);
        if (documentChange.getDocument().getTimestamp(fieldName).compareTo(new Timestamp(currentDate)) >= 0) {
            activityItemList.set(index, new CalendarItem(3, category, documentChange.getDocument().getId(), icon, courseCode, activity, dateMonth, datePart[0], timeStamp, endTime, note));
        } else {
            activityItemList.remove(index);
            isFound = false;
        }
    }

    //load data to variables from changed document
    public void fetchDataFromDocument(DocumentChange documentChange) {
        category = documentChange.getDocument().getString("category");
        switch (category) {
            case "LAB":
                icon = R.drawable.ic_labtest;
                break;
            case "TMA":
                icon = R.drawable.ic_assignment;
                break;
            case "DS":
                icon = R.drawable.ic_ds;
                break;
            case "PS":
                icon = R.drawable.ic_ps;
                break;
            case "CAT":
                icon = R.drawable.ic_cat;
                break;
            case "FINAL":
                icon = R.drawable.ic_final;
                break;
            case "VIVA":
                icon = R.drawable.ic_viva;
                break;
            case "QUIZ":
                icon = R.drawable.ic_quiz;
                break;
            default:
                icon = R.drawable.ic_error;
        }
        courseCode = documentChange.getDocument().getString("courseCode");
        activity = documentChange.getDocument().getString("activity");
        timeStamp = documentChange.getDocument().getDate(fieldName);
        endTime = documentChange.getDocument().getDate("endTime");
        note = documentChange.getDocument().getString("note");
        date = documentChange.getDocument().getDate(fieldName).toString();
        datePart = date.split(" ");
        dateMonth = null;
        if (datePart.length >= 3) {
            dateMonth = datePart[2] + " " + datePart[1];
        }
        day = datePart[0];
    }

    public void getAddedCourses() {
        cursor = databaseHelper.getAllCourses();
        while (cursor.moveToNext()) {
            courseItemList.add(new CourseItem(6, cursor.getString(1), cursor.getString(2)));
        }
    }

    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Call custom method to reinitialize or refresh the content
                initializeContent();

                // After refreshing, make sure to call setRefreshing(false) to stop the refresh animation.
                swipe_refresh_layout_courses.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;

    }
}