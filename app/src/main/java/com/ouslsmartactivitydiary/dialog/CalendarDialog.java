package com.ouslsmartactivitydiary.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ouslsmartactivitydiary.item.CalendarItem;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.util.Date;

public class CalendarDialog extends AppCompatDialogFragment {

    CalendarItem position;
    TextView courseCode, courseName, activityName, date, time, note;
    Context context;
    Date timeStamp;
    String[] timeStampParts, endTimeParts;

    int LAB, QUIZ, PS, VIVA, DS, TMA, CAT, FINAL;
    DatabaseHelper databaseHelper;
    Cursor colorCursor, cursor;

    public CalendarDialog(CalendarItem position){
        this.position = position;
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        context = getContext();
        databaseHelper = new DatabaseHelper(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_calender_dialog, null);


        courseCode = view.findViewById(R.id.dialogCourseCode);
        courseName = view.findViewById(R.id.dialogCourseName);
        activityName = view.findViewById(R.id.dialogActivityName);
        date = view.findViewById(R.id.dialogActivityDate);
        time = view.findViewById(R.id.dialogActivityTime);
        note = view.findViewById(R.id.dialogActivityNote);

        courseCode.setText(position.getCourseCode());
        courseCode.setTextColor(getResources().getColor(R.color.white));

        cursor = databaseHelper.getCourseByCourseCode(position.getCourseCode());
        if (cursor.moveToFirst()) {
            courseName.setText(cursor.getString(2));
        } else {
            courseName.setText(position.getCourseCode());
        }
        courseName.setTextColor(getResources().getColor(R.color.white));

        activityName.setText(position.getActivityName());
        activityName.setTextColor(getResources().getColor(R.color.white));
        note.setText(position.getNote());
        note.setTextColor(getResources().getColor(R.color.white));

        timeStamp = position.getTimeStamp();
        timeStampParts = timeStamp.toString().split(" ");

        if (position.getEndTime() != null) {
            endTimeParts = position.getEndTime().toString().split(" ");
        }

        date.setText((timeStampParts[2]+" "+timeStampParts[0]+" "+timeStampParts[1]+" "+timeStampParts[5]));
        date.setTextColor(getResources().getColor(R.color.white));
        if (position.getEndTime()!=null) {
            time.setText(timeStampParts[3]+" - "+endTimeParts[3]);
        } else {
            time.setText(timeStampParts[3]);
        }
        time.setTextColor(getResources().getColor(R.color.white));

        checkColors();

        switch (position.getCategory()){
            case "LAB":
                view.setBackgroundColor(LAB);
                break;
            case "TMA":
                view.setBackgroundColor(TMA);
                break;
            case "DS":
                view.setBackgroundColor(DS);
                break;
            case "PS":
                view.setBackgroundColor(PS);
                break;
            case "CAT":
                view.setBackgroundColor(CAT);
                break;
            case "FINAL":
                view.setBackgroundColor(FINAL);
                break;
            case "VIVA":
                view.setBackgroundColor(VIVA);
                break;
            case "QUIZ":
                view.setBackgroundColor(QUIZ);
                break;
            default:
                view.setBackgroundColor(getResources().getColor(R.color.DarkGray));
        }

        builder.setView(view);
        return builder.create();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

}
