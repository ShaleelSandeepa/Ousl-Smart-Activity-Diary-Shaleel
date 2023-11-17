package com.ouslsmartactivitydiary;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder {

    ImageView activityIcon, calendarIcon;
    TextView courseCode, activityName, date, day;
    CardView cardView;

    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        activityIcon = itemView.findViewById(R.id.itemIcon);
        courseCode = itemView.findViewById(R.id.itemCourseCode);
        activityName = itemView.findViewById(R.id.itemActivityName);
        date = itemView.findViewById(R.id.itemDate);
        day = itemView.findViewById(R.id.itemDay);
        cardView = itemView.findViewById(R.id.calendarItemCard);

        calendarIcon = itemView.findViewById(R.id.itemCalendarIcon);
    }
}
