package com.ouslsmartactivitydiary;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Date;

public class CalendarDialog extends AppCompatDialogFragment {

    CalendarItem position;
    TextView courseCode, courseName, activityName, date, time, note;
    Context context;
    Date timeStamp;
    String[] timeStampParts, endTimeParts;

    public CalendarDialog(CalendarItem position){
        this.position = position;
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        context = getContext();

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
        courseName.setText(position.getCourseCode());
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

        switch (position.getCategory()){
            case "LAB":
                view.setBackgroundColor(getResources().getColor(R.color.LAB));
                break;
            case "TMA":
                view.setBackgroundColor(getResources().getColor(R.color.TMA));
                break;
            case "DS":
                view.setBackgroundColor(getResources().getColor(R.color.DS));
                break;
            case "PS":
                view.setBackgroundColor(getResources().getColor(R.color.PS));
                break;
            case "CAT":
                view.setBackgroundColor(getResources().getColor(R.color.CAT));
                break;
            case "FINAL":
                view.setBackgroundColor(getResources().getColor(R.color.FINAL));
                break;
            case "VIVA":
                view.setBackgroundColor(getResources().getColor(R.color.VIVA));
                break;
            case "QUIZ":
                view.setBackgroundColor(getResources().getColor(R.color.QUIZ));
                break;
            default:
                view.setBackgroundColor(getResources().getColor(R.color.DarkGray));
        }

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

}
