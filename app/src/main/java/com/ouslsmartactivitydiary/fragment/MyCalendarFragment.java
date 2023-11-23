package com.ouslsmartactivitydiary.fragment;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ouslsmartactivitydiary.CalendarAdapter;
import com.ouslsmartactivitydiary.dialog.CalendarDialog;
import com.ouslsmartactivitydiary.CalendarIconAdapter;
import com.ouslsmartactivitydiary.item.CalendarItem;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyCalendarFragment extends Fragment implements CalendarAdapter.AdapterCallback {

    Context context;

    TextView todayTextView;
    LinearLayout linearMonday, linearTuesday, linearWednesday, linearThursday,linearFriday, linearSaturday, linearSunday;

    //CONNECTIVITY RELATED VARIABLES
    LinearLayout connectionLostLinear;
    AlphaAnimation blinkAnimation;
    ConnectivityManager connectivityManager;
    Handler handler;
    Runnable runnable;

    RecyclerView recyclerViewActivityItem;
    RecyclerView recyclerViewMonday, recyclerViewTuesday, recyclerViewWednesday, recyclerViewThursday, recyclerViewFriday, recyclerViewSaturday, recyclerViewSunday;
    List<CalendarItem> activityItemList, calendarIconList, allActivityItems;
    List<CalendarItem> mondayList, tuesdayList, wednesdayList, thursdayList, fridayList, saturdayList, sundayList;
    CalendarAdapter calendarAdapter;
    CalendarIconAdapter calendarMondayAdapter, calendarTuesdayAdapter, calendarWednesdayAdapter, calendarThursdayAdapter, calendarFridayAdapter, calendarSaturdayAdapter, calendarSundayAdapter;

    Date currentDate, yesterday, timeStamp, endTime, weekStartDate, weekEndDate;
    Calendar calendar;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String category, courseCode, activity, date, dateMonth, day, todayTimeStamp, note;
    String[] datePart, todayParts, weekEndParts, weekStartParts;
    int icon;
    int LAB, QUIZ, PS, VIVA, DS, TMA, CAT, FINAL;
    boolean isFound = false;
    boolean isRefreshed = false;
    boolean isReddyToUpdate = false;
    boolean courseFound = false;
    boolean isFirstRun = true;
    boolean isFirstAdd = true;
    int changes, snapshotCount;
    String type;

    OnNotificationCountListener onNotificationCountListener;
    boolean isAdded;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference, collectionReferenceStat;
    String fieldName = "date";
    DatabaseHelper databaseHelper;
    Cursor cursor, colorCursor;
    int accountID;

    private CalendarItem position;
    public CalendarItem getPosition() {
        return position;
    }
    public void setPosition(CalendarItem position) {
        this.position = position;
    }

    LinearLayout noCourseLinear;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_calendar, container, false);

        blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
        blinkAnimation.setDuration(1000); // Set the duration of each animation cycle (in milliseconds)
        blinkAnimation.setRepeatCount(Animation.INFINITE); // Set the number of times to repeat (infinite in this case)

        connectionLostLinear = rootView.findViewById(R.id.connectionLostLinear);
        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        handler = new Handler();
        checkConnectivity();

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        // Set the refresh listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeContent();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeContent() {

        recyclerViewActivityItem = rootView.findViewById(R.id.recyclerCalendarActivity);
        recyclerViewMonday = rootView.findViewById(R.id.recyclerCalendarMonday);
        recyclerViewTuesday = rootView.findViewById(R.id.recyclerCalendarTuesday);
        recyclerViewWednesday = rootView.findViewById(R.id.recyclerCalendarWednesday);
        recyclerViewThursday = rootView.findViewById(R.id.recyclerCalendarThursday);
        recyclerViewFriday = rootView.findViewById(R.id.recyclerCalendarFriday);
        recyclerViewSaturday = rootView.findViewById(R.id.recyclerCalendarSaturday);
        recyclerViewSunday = rootView.findViewById(R.id.recyclerCalendarSunday);
        activityItemList = new ArrayList<>();
        allActivityItems = new ArrayList<>();
        calendarIconList = new ArrayList<>();
        mondayList = new ArrayList<>();
        tuesdayList = new ArrayList<>();
        wednesdayList = new ArrayList<>();
        thursdayList = new ArrayList<>();
        fridayList = new ArrayList<>();
        saturdayList = new ArrayList<>();
        sundayList = new ArrayList<>();

        todayTextView = rootView.findViewById(R.id.calendarTodayText);
        linearMonday = rootView.findViewById(R.id.linearMonday);
        linearTuesday = rootView.findViewById(R.id.linearTuesday);
        linearWednesday = rootView.findViewById(R.id.linearWednesday);
        linearThursday = rootView.findViewById(R.id.linearThursday);
        linearFriday = rootView.findViewById(R.id.linearFriday);
        linearSaturday = rootView.findViewById(R.id.linearSaturday);
        linearSunday = rootView.findViewById(R.id.linearSunday);

        databaseHelper = new DatabaseHelper(getContext());
        noCourseLinear = rootView.findViewById(R.id.noCourseLinear);

        currentDate = new Date();
        // Create a Calendar instance and set it to the current date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        // Subtract one day to get yesterday
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        // Get the Date object for yesterday
        yesterday = calendar.getTime();

        //check the account ID which is logged in
        cursor = databaseHelper.getAllProfiles();
        if (cursor.moveToFirst()) {
            accountID = cursor.getInt(0);
        }

        checkColors();
        loadDetails(activityItemList);

        // check any course added to the my course section
        cursor = databaseHelper.getAllCourses();
        if (cursor.getCount() == 0) {
            noCourseLinear.setVisibility(View.VISIBLE);
            recyclerViewActivityItem.setVisibility(View.GONE);
        } else {
            noCourseLinear.setVisibility(View.GONE);
            recyclerViewActivityItem.setVisibility(View.VISIBLE);
        }

        // this will load calendar activities below to weekly calendar
        calendarAdapter = new CalendarAdapter(getContext(), activityItemList, new CalendarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CalendarItem position) {
                setPosition(position);
                CalendarDialog calendarDialog = new CalendarDialog(position);
                calendarDialog.show(getParentFragmentManager(), "calendar_dialog");
            }
        }, this);
        recyclerViewActivityItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewActivityItem.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();

        // before running below code calender icons loaded and categorized well format

        calendarMondayAdapter = new CalendarIconAdapter(getContext(), mondayList);
        recyclerViewMonday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewMonday.setAdapter(calendarMondayAdapter);
        calendarMondayAdapter.notifyDataSetChanged();

        calendarTuesdayAdapter = new CalendarIconAdapter(getContext(), tuesdayList);
        recyclerViewTuesday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewTuesday.setAdapter(calendarTuesdayAdapter);
        calendarTuesdayAdapter.notifyDataSetChanged();

        calendarWednesdayAdapter = new CalendarIconAdapter(getContext(), wednesdayList);
        recyclerViewWednesday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewWednesday.setAdapter(calendarWednesdayAdapter);
        calendarWednesdayAdapter.notifyDataSetChanged();

        calendarThursdayAdapter = new CalendarIconAdapter(getContext(), thursdayList);
        recyclerViewThursday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewThursday.setAdapter(calendarThursdayAdapter);
        calendarThursdayAdapter.notifyDataSetChanged();

        calendarFridayAdapter = new CalendarIconAdapter(getContext(), fridayList);
        recyclerViewFriday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewFriday.setAdapter(calendarFridayAdapter);
        calendarFridayAdapter.notifyDataSetChanged();

        calendarSaturdayAdapter = new CalendarIconAdapter(getContext(), saturdayList);
        recyclerViewSaturday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewSaturday.setAdapter(calendarSaturdayAdapter);
        calendarSaturdayAdapter.notifyDataSetChanged();

        calendarSundayAdapter = new CalendarIconAdapter(getContext(), sundayList);
        recyclerViewSunday.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewSunday.setAdapter(calendarSundayAdapter);
        calendarSundayAdapter.notifyDataSetChanged();
    }

    public void loadDetails(List<CalendarItem> activityItemList) {

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
                        changes = 0;
                        snapshotCount = value.getDocumentChanges().size();

                        if (value.getDocumentChanges().size() == 0) {
                            Toast.makeText(context, "Check Your Programme Name in your Profile", Toast.LENGTH_SHORT).show();
                        }

                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            changes++;
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                cursor = databaseHelper.getCourseByCourseCode(documentChange.getDocument().getString("courseCode"));
                                if (cursor.moveToFirst()) {
                                    addList(documentChange);
                                    type = "ADDED";
                                }
                            } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                for (int i=0; i<activityItemList.size(); i++) {
                                    if (activityItemList.get(i).getId().equals(documentChange.getDocument().getId())){
                                        isFound = true;
                                        updateList(documentChange, i);
                                        changes++;
                                        fetchDataFromDocument(documentChange);
                                        type = "MODIFIED";
                                    }

                                    // add activity only if not fount in the Array list, but in the firebase
                                    if (!isFound && changes == 1 && isFirstAdd){
                                        isRefreshed = false;
                                        isFirstAdd = false;
                                        addList(documentChange);
                                        changes++;
                                        type = "ADDED";
                                        break;
                                    }
                                }

                            } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                if (changes == 1) {
                                    for (int i=0; i<activityItemList.size(); i++) {
                                        if (activityItemList.get(i).getId().equals(documentChange.getDocument().getId())){
                                            activityItemList.remove(i);
                                            fetchDataFromDocument(documentChange);
                                            changes++;
                                            type = "REMOVED";
                                            break;
                                        }
                                    }
                                }
                            }
                            calendarAdapter.notifyDataSetChanged();

                        }

                        loadCalendarIcons(value);
                        calendarMondayAdapter.notifyDataSetChanged();
                        calendarTuesdayAdapter.notifyDataSetChanged();
                        calendarWednesdayAdapter.notifyDataSetChanged();
                        calendarThursdayAdapter.notifyDataSetChanged();
                        calendarFridayAdapter.notifyDataSetChanged();
                        calendarSaturdayAdapter.notifyDataSetChanged();
                        calendarSundayAdapter.notifyDataSetChanged();

                        calendarAdapter.notifyDataSetChanged();

                        if (isFirstRun) {
                            isFirstRun = false;  // Set the flag to false so that the method won't be called again
                            checkType();
                        }
                    }
                });
    }

    public void checkType() {
        if (snapshotCount == 1 && type != null) {
            Toast.makeText(context, "Please Refresh Your Calendar", Toast.LENGTH_SHORT).show();
            checkNotificationSetting(type);
            onNotificationCountListener.onNotificationCount();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstRun = true;
                isFirstAdd = true;
            }
        }, 5000);
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

    public void addList(DocumentChange documentChange) {
        fetchDataFromDocument(documentChange);
        allActivityItems.add(new CalendarItem(1, category, documentChange.getDocument().getId(), icon, courseCode, activity, dateMonth, datePart[0], timeStamp, endTime, note));
        if(documentChange.getDocument().getTimestamp("endTime") != null) {
            if (documentChange.getDocument().getTimestamp(fieldName).compareTo(new Timestamp(currentDate)) >= 0 ||
                    documentChange.getDocument().getTimestamp("endTime").compareTo(new Timestamp(currentDate)) >= 0) {
                activityItemList.add(new CalendarItem(1, category, documentChange.getDocument().getId(), icon, courseCode, activity, dateMonth, datePart[0], timeStamp, endTime, note));
                isFound = true;
            }
        } else {
            if (documentChange.getDocument().getTimestamp(fieldName).compareTo(new Timestamp(currentDate)) >= 0) {
                activityItemList.add(new CalendarItem(1, category, documentChange.getDocument().getId(), icon, courseCode, activity, dateMonth, datePart[0], timeStamp, endTime, note));
                isFound = true;
            }
        }

    }

    public void updateList(DocumentChange documentChange, int index) {
        fetchDataFromDocument(documentChange);
        if (documentChange.getDocument().getTimestamp(fieldName).compareTo(new Timestamp(currentDate)) >= 0) {
            activityItemList.set(index, new CalendarItem(1, category, documentChange.getDocument().getId(), icon, courseCode, activity, dateMonth, datePart[0], timeStamp, endTime, note));
        } else {
            activityItemList.remove(index);
            isFound = false;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadCalendarIcons(QuerySnapshot value) {

        cursor = databaseHelper.getAllCourses();

        calendarIconList.clear();
        calculateReverseDateAmount();

        weekStartParts = weekStartDate.toString().split(" ");
        weekEndParts = weekEndDate.toString().split(" ");

        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

            // check that icon related to added course or not
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(1).equals(documentSnapshot.getString("courseCode"))){

                        datePart = documentSnapshot.getDate(fieldName).toString().split(" ");

                        if ((documentSnapshot.getDate(fieldName).compareTo(weekStartDate)>=0 ||
                                (datePart[1]+datePart[2]+datePart[5]).equals(weekStartParts[1]+weekStartParts[2]+weekStartParts[5])) &&
                                (documentSnapshot.getDate(fieldName).compareTo(weekEndDate)<=0 ||
                                        (datePart[1]+datePart[2]+datePart[5]).equals(weekEndParts[1]+weekEndParts[2]+weekEndParts[5]))) {

                            category = documentSnapshot.getString("category");
                            switch (category) {
                                case "LAB":
                                    icon = R.drawable.ic_labtest_2;
                                    break;
                                case "TMA":
                                    icon = R.drawable.ic_assignment_2;
                                    break;
                                case "DS":
                                    icon = R.drawable.ic_ds_2;
                                    break;
                                case "PS":
                                    icon = R.drawable.ic_ps_2;
                                    break;
                                case "CAT":
                                    icon = R.drawable.ic_cat_2;
                                    break;
                                case "FINAL":
                                    icon = R.drawable.ic_final_2;
                                    break;
                                case "VIVA":
                                    icon = R.drawable.ic_viva_2;
                                    break;
                                case "QUIZ":
                                    icon = R.drawable.ic_quiz_2;
                                    break;
                                default:
                                    icon = R.drawable.ic_error_2;
                                    break;
                            }
                            switch (datePart[0]) {
                                case "Mon":
                                    calendarIconList.add(new CalendarItem(11, documentSnapshot.getId(), icon));
                                    break;
                                case "Tue":
                                    calendarIconList.add(new CalendarItem(22, documentSnapshot.getId(), icon));
                                    break;
                                case "Wed":
                                    calendarIconList.add(new CalendarItem(33, documentSnapshot.getId(), icon));
                                    break;
                                case "Thu":
                                    calendarIconList.add(new CalendarItem(44, documentSnapshot.getId(), icon));
                                    break;
                                case "Fri":
                                    calendarIconList.add(new CalendarItem(55, documentSnapshot.getId(), icon));
                                    break;
                                case "Sat":
                                    calendarIconList.add(new CalendarItem(66, documentSnapshot.getId(), icon));
                                    break;
                                case "Sun":
                                    calendarIconList.add(new CalendarItem(77, documentSnapshot.getId(), icon));
                                    break;
                            }

                        }

                    }
                } while (cursor.moveToNext());
            }
            cursor.moveToFirst();

        }
        categorizeCalendarIcons();
    }

    @SuppressLint("SetTextI18n")
    public void calculateReverseDateAmount() {
        todayTimeStamp = currentDate.toString();
        todayParts = todayTimeStamp.split(" ");
        todayTextView.setText((todayParts[2]+" "+todayParts[1]+" "+todayParts[5]));

        linearMonday.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        linearTuesday.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        linearWednesday.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        linearThursday.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        linearFriday.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        linearSaturday.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        linearSunday.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        switch (todayParts[0]) {
            case "Mon":
                weekStartDate = currentDate;
                calculateWeekEnd(6);
                linearMonday.setBackgroundColor(ContextCompat.getColor(context, R.color.LighterBlue2));
                break;
            case "Tue":
                calculateWeekStart(-1);
                calculateWeekEnd(5);
                linearTuesday.setBackgroundColor(ContextCompat.getColor(context, R.color.LighterBlue2));
                break;
            case "Wed":
                calculateWeekStart(-2);
                calculateWeekEnd(4);
                linearWednesday.setBackgroundColor(ContextCompat.getColor(context, R.color.LighterBlue2));
                break;
            case "Thu":
                calculateWeekStart(-3);
                calculateWeekEnd(3);
                linearThursday.setBackgroundColor(ContextCompat.getColor(context, R.color.LighterBlue2));
                break;
            case "Fri":
                calculateWeekStart(-4);
                calculateWeekEnd(2);
                linearFriday.setBackgroundColor(ContextCompat.getColor(context, R.color.LighterBlue2));
                break;
            case "Sat":
                calculateWeekStart(-5);
                calculateWeekEnd(1);
                linearSaturday.setBackgroundColor(ContextCompat.getColor(context, R.color.LighterBlue2));
                break;
            case "Sun":
                calculateWeekStart(-6);
                weekEndDate = currentDate;
                linearSunday.setBackgroundColor(ContextCompat.getColor(context, R.color.LighterBlue2));
                break;
        }
    }

    public void calculateWeekStart(int amount) {
        // Get the current date
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // Subtract one week from the current date
        calendar.add(Calendar.DAY_OF_YEAR, amount);
        // Get the date one week ago
        weekStartDate = calendar.getTime();

    }

    public void calculateWeekEnd(int amount) {
        // Get the current date
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // Subtract one week from the current date
        calendar.add(Calendar.DAY_OF_YEAR, amount);
        // Get the date one week ago
        weekEndDate = calendar.getTime();
    }

    public void categorizeCalendarIcons() {
        mondayList.clear();
        tuesdayList.clear();
        wednesdayList.clear();
        thursdayList.clear();
        fridayList.clear();
        saturdayList.clear();
        sundayList.clear();
        for (int i=0; i<calendarIconList.size(); i++) {
            switch (calendarIconList.get(i).getViewType()){
                case 11:
                    mondayList.add(calendarIconList.get(i));
                    break;
                case 22:
                    tuesdayList.add(calendarIconList.get(i));
                    break;
                case 33:
                    wednesdayList.add(calendarIconList.get(i));
                    break;
                case 44:
                    thursdayList.add(calendarIconList.get(i));
                    break;
                case 55:
                    fridayList.add(calendarIconList.get(i));
                    break;
                case 66:
                    saturdayList.add(calendarIconList.get(i));
                    break;
                case 77:
                    sundayList.add(calendarIconList.get(i));
                    break;

            }
        }

    }

    // sort the list by date
    public void sortList(List<CalendarItem> calendarItems) {
        Collections.sort(calendarItems, new Comparator<CalendarItem>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            @Override
            public int compare(CalendarItem o1, CalendarItem o2) {
                try {
                    Date date1 = dateFormat.parse(o1.getDate());
                    Date date2 = dateFormat.parse(o2.getDate());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public void checkConnectivity() {

        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternetConnection();
                handler.postDelayed(this, 1000); // Repeat the check every 1 second
            }
        };
        handler.post(runnable);
    }

    //check the internet connection is available or not
    private void checkInternetConnection() {
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()){
            connectionLostLinear.setVisibility(View.GONE);
        } else {
            connectionLostLinear.setVisibility(View.VISIBLE);
        }
    }

    public void checkColors() {
        colorCursor = databaseHelper.getAllColors();
        while (colorCursor.moveToNext()) {
            switch (colorCursor.getInt(0)) {
                case 1:
                    LAB = colorCursor.getInt(2);
                    break;
                case 2:
                    QUIZ = colorCursor.getInt(2);
                    break;
                case 3:
                    PS = colorCursor.getInt(2);
                    break;
                case 4:
                    VIVA = colorCursor.getInt(2);
                    break;
                case 5:
                    DS = colorCursor.getInt(2);
                    break;
                case 6:
                    TMA = colorCursor.getInt(2);
                    break;
                case 7:
                    CAT = colorCursor.getInt(2);
                    break;
                case 8:
                    FINAL = colorCursor.getInt(2);
                    break;
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000); // Start the periodic check
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Stop the periodic check
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove the callback to avoid memory leaks
        handler.removeCallbacks(runnable);
    }

    private void refreshData() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Call custom method to reinitialize or refresh the content
                initializeContent();
                onNotificationCountListener.onNotificationCount();

                // After refreshing, make sure to call setRefreshing(false) to stop the refresh animation.
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);

    }

    public void checkNotificationSetting(String type) {
        cursor = databaseHelper.getSetting(1);
        if (cursor.moveToFirst()){
            if (cursor.getInt(0) == 1 && cursor.getInt(2) == 1) {
                addNotification(type);
            }
        }
    }

    public void addNotification(String type) {
        /////////////////////// add one notification to the database /////////////////////////
        // Get the current date and time
        Date currentDate = new Date();
        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        // Format the time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = timeFormat.format(currentDate);

        String notificationDate;
        if (timeStamp == null) {
            notificationDate = null;
        } else {
            String[] datePart = timeStamp.toString().split(" ");
            notificationDate = (datePart[0]+" "+datePart[1]+" "+datePart[2]+" at "+datePart[3]);
        }
        String notificationEndDate;
        if (endTime == null) {
            notificationEndDate = null;
        } else {
            String[] endTimePart = endTime.toString().split(" ");
            notificationEndDate = (endTimePart[0]+" "+endTimePart[1]+" "+endTimePart[2]+" at "+endTimePart[3]);
        }

        if (type.equals("ADDED")) {
            isAdded = databaseHelper.addNotification(formattedDate, formattedTime, "unread", (courseCode+" - "+activity+" Added"),
                    ("\nDate : "+notificationDate+"\nEndTime : "+notificationEndDate+"\nNote : "+note));
            if (isAdded) {
                sendNotification("New Activity Added", "New "+courseCode+" Activity added to Activity Diary");
            }
        } else if (type.equals("MODIFIED")) {
            isAdded = databaseHelper.addNotification(formattedDate, formattedTime, "unread", (courseCode+" - "+activity+" Modified"),
                    ("\nDate : "+notificationDate+"\nEndTime : "+notificationEndDate+"\nNote : "+note));
            if (isAdded) {
                sendNotification("Activity Updated","Updated "+courseCode+" Activity on Activity Diary");
            }
        } else if (type.equals("REMOVED")) {
            isAdded = databaseHelper.addNotification(formattedDate, formattedTime, "unread", (courseCode+" - "+activity+" Deleted"),
                    ("\nDate : "+notificationDate+"\nEndTime : "+notificationEndDate+"\nNote : "+note));
            if (isAdded) {
                sendNotification("Activity Removed","Remove "+courseCode+" Activity from Activity Diary");
            }
        }

    }

    ////////////////////////// SEND NOTIFICATION ////////////////////////////
    public void sendNotification(String title, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            String channelId = "ouslsmartactivitydiary";
            CharSequence channelName = "OUSL SMART ACTIVITY DIARY";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ouslsmartactivitydiary")
                .setSmallIcon(R.drawable.ic_final_2)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Set a large icon for the notification
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.round_logo);
        builder.setLargeIcon(largeIcon);

        int notificationId = 123;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh(int type) {

        if (type == 1) {
            if (!isRefreshed) {
                initializeContent();
                isRefreshed = true;
                isReddyToUpdate = true;
            }
        }
        if (type == 2 && isReddyToUpdate) {
            initializeContent();
            isReddyToUpdate = false;
            isRefreshed = false;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;

        try {
            onNotificationCountListener = (OnNotificationCountListener) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnNotificationCountListener");
        }
    }

    public interface OnNotificationCountListener {
        void onNotificationCount();
    }
}