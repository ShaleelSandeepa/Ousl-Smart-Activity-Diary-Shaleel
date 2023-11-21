package com.ouslsmartactivitydiary;

import static com.ouslsmartactivitydiary.item.CalendarItem.ACTIVITIES;
import static com.ouslsmartactivitydiary.item.CalendarItem.CALENDAR;
import static com.ouslsmartactivitydiary.item.CalendarItem.COURSE_WISE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ouslsmartactivitydiary.data.DatabaseHelper;
import com.ouslsmartactivitydiary.item.CalendarItem;
import com.ouslsmartactivitydiary.item.CourseItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter {

    Context context;
    List<CalendarItem> calendarItems;
    CalendarAdapter.OnItemClickListener onItemClickListener;

    //ANIMATION RELATED VARIABLES
    AlphaAnimation blinkAnimation;
    Handler handler;
    Runnable runnable;
    Date currentDate;
    boolean isHandlerRunning = false;
    int count = 0;

    DatabaseHelper databaseHelper;
    Cursor colorCursor;
    int LAB, QUIZ, PS, VIVA, DS, TMA, CAT, FINAL;

    public interface AdapterCallback {
        void onRefresh(int type);
    }

    private AdapterCallback callback;

    // Constructor to receive the callback
    public CalendarAdapter() {

    }

    List<CourseItem> courseItemList;

    public CalendarAdapter(Context context, List<CalendarItem> calendarItems, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.calendarItems = calendarItems;
        this.onItemClickListener = onItemClickListener;
    }

    public CalendarAdapter(Context context, List<CalendarItem> calendarItems, OnItemClickListener onItemClickListener, AdapterCallback callback) {
        this.context = context;
        this.calendarItems = calendarItems;
        this.onItemClickListener = onItemClickListener;
        this.callback = callback;
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



    @Override
    public int getItemViewType(int position) {
        switch (calendarItems.get(position).getViewType()) {
            case 1:
                return ACTIVITIES;
            case 2:
                return CALENDAR;
            case 3:
                return COURSE_WISE;
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
            case CALENDAR:
                return new CalendarViewHolder(LayoutInflater.from(context).inflate(R.layout.item_calendar_icon, parent, false));
            case COURSE_WISE:
                return new CalendarViewHolder(LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false));
            default:
                return null;
        }
    }

    @SuppressLint({"ResourceAsColor", "NonConstantResourceId", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        blinkAnimation = new AlphaAnimation(0.2f, 1.0f);
        blinkAnimation.setDuration(1000); // Set the duration of each animation cycle (in milliseconds)
        blinkAnimation.setRepeatCount(Animation.INFINITE); // Set the number of times to repeat (infinite in this case)
        blinkAnimation.setRepeatMode(Animation.REVERSE); // Reverse the animation when repeating
        handler = new Handler();

        databaseHelper = new DatabaseHelper(context);
        checkColors();

        switch (calendarItems.get(position).getViewType()) {
            case ACTIVITIES:
                sortList(calendarItems);
                ((CalendarViewHolder) holder).activityIcon.setImageResource(calendarItems.get(position).getActivityIcon());

                //change the calendar activity card color
                switch (calendarItems.get(position).getCategory()){
                    case "LAB":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(LAB);
                        break;
                    case "TMA":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(TMA);
                        break;
                    case "DS":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(DS);
                        break;
                    case "PS":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(PS);
                        break;
                    case "CAT":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(CAT);
                        break;
                    case "FINAL":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(FINAL);
                        break;
                    case "VIVA":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(VIVA);
                        break;
                    case "QUIZ":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(QUIZ);
                        break;
                    default:
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.DarkGray));
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

                // set blinking animation for ongoing activities
                if (calendarItems.get(position).getEndTime() != null && position == 0 && !isHandlerRunning) {
                    currentDate = new Date();
                    isHandlerRunning = true;

                    if (calendarItems.get(position).getTimeStamp().compareTo(currentDate) <= 0 &&
                            calendarItems.get(position).getEndTime().compareTo(currentDate) >= 0) {
                        String ongoing = (calendarItems.get(position).getCourseCode()+"\n"+
                                calendarItems.get(position).getActivityName()+"\n\nis Now Happening...");
//                        Toast.makeText(context, ongoing, Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Ongoing Activity !");
                        alertDialog.setMessage(ongoing);

                        //When click "Yes" it will execute this
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }

                break;
            case COURSE_WISE:
                sortList(calendarItems);
                ((CalendarViewHolder) holder).activityIcon.setImageResource(calendarItems.get(position).getActivityIcon());

                //change the calendar activity card color
                switch (calendarItems.get(position).getCategory()){
                    case "LAB":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(LAB);
                        break;
                    case "TMA":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(TMA);
                        break;
                    case "DS":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(DS);
                        break;
                    case "PS":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(PS);
                        break;
                    case "CAT":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(CAT);
                        break;
                    case "FINAL":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(FINAL);
                        break;
                    case "VIVA":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(VIVA);
                        break;
                    case "QUIZ":
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(QUIZ);
                        break;
                    default:
                        ((CalendarViewHolder) holder).cardView.setCardBackgroundColor(context.getResources().getColor(R.color.DarkGray));
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
        return calendarItems.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CalendarItem position);
    }


}
