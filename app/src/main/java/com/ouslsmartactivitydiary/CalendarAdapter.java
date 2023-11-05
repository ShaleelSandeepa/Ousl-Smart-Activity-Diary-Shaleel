package com.ouslsmartactivitydiary;

import static com.ouslsmartactivitydiary.CalendarItem.ACTIVITIES;
import static com.ouslsmartactivitydiary.CalendarItem.CALENDAR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter {

    Context context;
    List<CalendarItem> calendarItems;
CalendarAdapter.OnItemClickListener onItemClickListener;

    public CalendarAdapter(Context context, List<CalendarItem> calendarItems, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.calendarItems = calendarItems;
        this.onItemClickListener = onItemClickListener;
    }



    @Override
    public int getItemViewType(int position) {
        switch (calendarItems.get(position).getViewType()) {
            case 1:
                return ACTIVITIES;
            case 2:
                return CALENDAR;
            default:
                return -1;

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ACTIVITIES:
                return new CalendarViewHolder(LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false));
            default:
                return null;
        }
    }

    @SuppressLint({"ResourceAsColor", "NonConstantResourceId", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        switch (calendarItems.get(position).getViewType()) {
            case ACTIVITIES:
                ((CalendarViewHolder) holder).activityIcon.setImageResource(calendarItems.get(position).getActivityIcon());

                //change the calendar activity card color
                switch (calendarItems.get(position).getActivityIcon()){
                    case R.drawable.ic_labtest:
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.LAB));
                        break;
                    case R.drawable.ic_outline_assignment:
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.TMA));
                        break;
                }

                ((CalendarViewHolder) holder).courseCode.setText(calendarItems.get(position).getCourseCode());
                ((CalendarViewHolder) holder).activityName.setText(calendarItems.get(position).getActivityName());
                ((CalendarViewHolder) holder).date.setText(calendarItems.get(position).getDate());
                ((CalendarViewHolder) holder).day.setText(calendarItems.get(position).getDay());
                ((CalendarViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(calendarItems.get(position));

                    }
                });
        }

    }

    @Override
    public int getItemCount() {
        return calendarItems.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CalendarItem position);
    }
}
