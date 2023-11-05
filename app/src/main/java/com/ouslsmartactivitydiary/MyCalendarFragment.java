package com.ouslsmartactivitydiary;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MyCalendarFragment extends Fragment {

    RecyclerView recyclerViewActivityItem;
    List<CalendarItem> activityItemList;
    CalendarAdapter calendarAdapter;

    private CalendarItem position;

    public CalendarItem getPosition() {
        return position;
    }

    public void setPosition(CalendarItem position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_calendar, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewActivityItem = view.findViewById(R.id.recyclerCalendarActivity);
        activityItemList = new ArrayList<>();

        activityItemList.add(new CalendarItem(1, R.drawable.ic_labtest, "EEI4366", "Lab Test 3", "19/7", "Wed"));
        activityItemList.add(new CalendarItem(1, R.drawable.ic_outline_assignment, "EEI4366", "TMA 1", "19/7", "Wed"));
        activityItemList.add(new CalendarItem(1, R.drawable.ic_labtest, "EEI4366", "Lab Test 3", "19/7", "Wed"));
        activityItemList.add(new CalendarItem(1, R.drawable.ic_labtest, "EEI4366", "Lab Test 3", "19/7", "Wed"));
        activityItemList.add(new CalendarItem(1, R.drawable.ic_labtest, "EEI4366", "Lab Test 3", "19/7", "Wed"));
        activityItemList.add(new CalendarItem(1, R.drawable.ic_labtest, "EEI4366", "Lab Test 3", "19/7", "Wed"));
        activityItemList.add(new CalendarItem(1, R.drawable.ic_labtest, "EEI4366", "Lab Test 3", "19/7", "Wed"));

        calendarAdapter = new CalendarAdapter(getContext(), activityItemList, new CalendarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CalendarItem position) {
                setPosition(position);
                CalendarDialog calendarDialog = new CalendarDialog(position);
                calendarDialog.show(getParentFragmentManager(), "calendar_dialog");
            }
        });
        recyclerViewActivityItem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewActivityItem.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();

    }
}