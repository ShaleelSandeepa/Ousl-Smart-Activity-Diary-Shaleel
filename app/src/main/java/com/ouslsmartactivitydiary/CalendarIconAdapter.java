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
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.security.spec.PSSParameterSpec;
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

    DatabaseHelper databaseHelper;
    Cursor colorCursor;
    int LAB, QUIZ, PS, VIVA, DS, TMA, CAT, FINAL;
    int newColor;

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
        return new CalendarViewHolder(LayoutInflater.from(context).inflate(R.layout.item_calendar_icon, parent, false));

    }

    @SuppressLint({"ResourceAsColor", "NonConstantResourceId", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        databaseHelper = new DatabaseHelper(context);
        checkColors();

        ((CalendarViewHolder) holder).calendarIcon.setImageResource(calendarIcons.get(position).getActivityIcon());

        switch (calendarIcons.get(position).getActivityIcon()) {
            case R.drawable.ic_labtest_2:
                newColor = LAB;
                break;
            case R.drawable.ic_assignment_2:
                newColor = TMA;
                break;
            case R.drawable.ic_ds_2:
                newColor = DS;
                break;
            case R.drawable.ic_ps_2:
                newColor = PS;
                break;
            case R.drawable.ic_cat_2:
                newColor = CAT;
                break;
            case R.drawable.ic_final_2:
                newColor = FINAL;
                break;
            case R.drawable.ic_viva_2:
                newColor = VIVA;
                break;
            case R.drawable.ic_quiz_2:
                newColor = QUIZ;
                break;
            default:
                newColor = ContextCompat.getColor(context, R.color.DarkGray);
        }

        // Get the drawable from the ImageView
        Drawable originalDrawable = ((CalendarViewHolder) holder).calendarIcon.getDrawable();
        // Create a copy of the drawable to avoid modifying the original
        Drawable wrappedDrawable = DrawableCompat.wrap(originalDrawable).mutate();
        // Apply the color tint
        DrawableCompat.setTint(wrappedDrawable, newColor);

        ((CalendarViewHolder) holder).calendarIcon.setImageDrawable(wrappedDrawable);

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
    public int getItemCount() {
        return calendarIcons.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CalendarItem position);
    }


}
