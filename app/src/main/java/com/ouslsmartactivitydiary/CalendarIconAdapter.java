package com.ouslsmartactivitydiary;

import static com.ouslsmartactivitydiary.CalendarItem.ACTIVITIES;
import static com.ouslsmartactivitydiary.CalendarItem.CALENDAR;
import static com.ouslsmartactivitydiary.CalendarItem.MONDAY;
import static com.ouslsmartactivitydiary.CalendarItem.TUESDAY;
import static com.ouslsmartactivitydiary.CalendarItem.WEDNESDAY;
import static com.ouslsmartactivitydiary.CalendarItem.THURSDAY;
import static com.ouslsmartactivitydiary.CalendarItem.FRIDAY;
import static com.ouslsmartactivitydiary.CalendarItem.SATURDAY;
import static com.ouslsmartactivitydiary.CalendarItem.SUNDAY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// we use different adapter because we have to render details same time in one fragment
public class CalendarIconAdapter extends RecyclerView.Adapter {

    Context context;
    List<CalendarItem> calendarIcons;

    public CalendarIconAdapter(Context context, List<CalendarItem> calendarIcons) {
        this.context = context;
        this.calendarIcons = calendarIcons;
    }


    @Override
    public int getItemViewType(int position) {
        switch (calendarIcons.get(position).getViewType()) {
            case 2:
                return CALENDAR;
            case 11:
                return MONDAY;
            case 22:
                return TUESDAY;
            case 33:
                return WEDNESDAY;
            case 44:
                return THURSDAY;
            case 55:
                return FRIDAY;
            case 66:
                return SATURDAY;
            case 77:
                return SUNDAY;
            default:
                return -1;

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        switch (viewType) {
//            case CALENDAR:
//            default:
//                return null;
//        }
        return new CalendarViewHolder(LayoutInflater.from(context).inflate(R.layout.item_calendar_icon, parent, false));

    }

    @SuppressLint({"ResourceAsColor", "NonConstantResourceId", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

//        switch (calendarIcons.get(position).getViewType()) {
//            case CALENDAR:
//
//        }
        ((CalendarViewHolder) holder).calendarIcon.setImageResource(calendarIcons.get(position).getActivityIcon());

    }

    @Override
    public int getItemCount() {
        return calendarIcons.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CalendarItem position);
    }


}
