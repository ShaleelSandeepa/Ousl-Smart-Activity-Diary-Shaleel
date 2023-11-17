package com.ouslsmartactivitydiary;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;

public class CourseViewHolder extends RecyclerView.ViewHolder {

    TextView courseCodeTextView, courseNameTextView, programmeName, level;

    @SuppressLint("CutPasteId")
    public CourseViewHolder(@NonNull View itemView) {
        super(itemView);
        courseCodeTextView = itemView.findViewById(R.id.courseCodeTextView);
        courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
        programmeName = itemView.findViewById(R.id.programmeName);
        level = itemView.findViewById(R.id.programmeName);
    }
}
