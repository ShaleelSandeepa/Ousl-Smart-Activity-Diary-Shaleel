package com.ouslsmartactivitydiary.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ouslsmartactivitydiary.dialog.AddNewCourse;
import com.ouslsmartactivitydiary.CourseAdapter;
import com.ouslsmartactivitydiary.item.CourseItem;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyCoursesFragment extends Fragment implements AddNewCourse.OnDismissListener {

    private RecyclerView recyclerViewCourses;
    private FloatingActionButton fab;
    private List<CourseItem> courseItemList;
    CourseAdapter courseAdapter;

    AlertDialog.Builder alertDialog;

    DatabaseHelper databaseHelper;
    Cursor cursor;

    private SwipeRefreshLayout swipe_refresh_layout_courses;
    private View rootView;
    LinearLayout noCourseLinear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_courses, container, false);

        swipe_refresh_layout_courses = rootView.findViewById(R.id.swipe_refresh_layout_courses);
        // Set the refresh listener
        swipe_refresh_layout_courses.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        return rootView;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeContent();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeContent() {

        databaseHelper = new DatabaseHelper(getContext());
        noCourseLinear = rootView.findViewById(R.id.noCourseLinear);

        recyclerViewCourses = rootView.findViewById(R.id.recyclerViewCourses);
        fab = rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAppcompatDialog();
            }
        });

        courseItemList = new ArrayList<>();

        cursor = databaseHelper.getAllCourses();
        if (cursor.getCount() == 0) {
            noCourseLinear.setVisibility(View.VISIBLE);
        } else {
            noCourseLinear.setVisibility(View.GONE);
        }
        while (cursor.moveToNext()) {
            courseItemList.add(new CourseItem(3, cursor.getString(1), cursor.getString(2)));
        }

        sortList(courseItemList);

        courseAdapter = new CourseAdapter(getContext(), courseItemList, new CourseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CourseItem position) {
                alertDialog = new AlertDialog.Builder(requireContext());
                alertDialog.setTitle("Remove Course");
                alertDialog.setMessage("Remove "+position.getCourseCode() +" - "+ position.getCourseName());

                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeCourseFromDialog(position);
                    }
                });

                //When click "No" it will execute this
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewCourses.setAdapter(courseAdapter);
        courseAdapter.notifyDataSetChanged();
    }

    // sort the list by course code
    public void sortList(List<CourseItem> courseItems) {
        Collections.sort(courseItems, new Comparator<CourseItem>() {
            @Override
            public int compare(CourseItem o1, CourseItem o2) {
                return o1.getCourseCode().compareTo(o2.getCourseCode());
            }
        });
    }

    // Remove course from course list
    @SuppressLint("NotifyDataSetChanged")
    public void removeCourseFromDialog(CourseItem position) {
        boolean isCourseRemove = databaseHelper.deleteCourseByCode(position.getCourseCode());
        if (isCourseRemove) {
            Toast.makeText(getContext(), ("Successfully deleted "+position.getCourseCode()), Toast.LENGTH_SHORT).show();
            courseItemList.remove(position);
            courseAdapter.notifyDataSetChanged();
            cursor = databaseHelper.getAllCourses();
            if (cursor.getCount() == 0) {
                noCourseLinear.setVisibility(View.VISIBLE);
            } else {
                noCourseLinear.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getContext(), "failed to delete course", Toast.LENGTH_SHORT).show();
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

    public void addAppcompatDialog() {
        AddNewCourse addNewCourse = new AddNewCourse();
        if (isAdded()) {
            addNewCourse.setOnDismissListener(this);
        }
        addNewCourse.show(getParentFragmentManager(), "add_new_course");
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        // Set the listener during onAttach
//        setListener();
//    }
//
//    private void setListener() {
//        if (getParentFragment() instanceof AddNewCourse.OnDismissListener) {
//            AddNewCourse.OnDismissListener listener = (AddNewCourse.OnDismissListener) getParentFragment();
//            // Set the listener
//            // This assumes that AddNewCourse.OnDismissListener is implemented by the parent fragment
//            // Adjust the condition and type casting based on your actual implementation
//            setOnDismissListener(listener);
//        }
//    }

    @Override
    public void onDismissListen() {
        initializeContent();
    }
}