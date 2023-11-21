package com.ouslsmartactivitydiary;

import static com.ouslsmartactivitydiary.item.CourseItem.COURSE;
import static com.ouslsmartactivitydiary.item.CourseItem.DIALOG_COURSE;
import static com.ouslsmartactivitydiary.item.CourseItem.PROGRAMME;
import static com.ouslsmartactivitydiary.item.CourseItem.LEVEL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ouslsmartactivitydiary.item.CourseItem;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter {

    Context context;
    List<CourseItem> courseItemList;
    CourseAdapter.OnItemClickListener onItemClickListener;
    CourseAdapter.OnDialogItemClickListener onDialogItemClickListener;

    public CourseAdapter(Context context, List<CourseItem> courseItemList) {
        this.context = context;
        this.courseItemList = courseItemList;
    }

    public CourseAdapter(Context context, List<CourseItem> courseItemList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.courseItemList = courseItemList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        switch (courseItemList.get(position).getViewType()) {
            case 3:
                return COURSE;
            case 4:
                return PROGRAMME;
            case 5:
                return LEVEL;
            case 6:
                return DIALOG_COURSE;
            default:
                return -1;

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case COURSE:
                return new CourseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_course, parent, false));
            case DIALOG_COURSE:
                return new CourseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_course, parent, false));
            case PROGRAMME:
                return new CourseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_programme, parent, false));
            case LEVEL:
                return new CourseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_programme, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        switch (courseItemList.get(position).getViewType()) {
            case COURSE:
                ((CourseViewHolder) holder).courseCodeTextView.setText(courseItemList.get(position).getCourseCode());
                ((CourseViewHolder) holder).courseNameTextView.setText(courseItemList.get(position).getCourseName());
                ((CourseViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Long press to remove course", Toast.LENGTH_SHORT).show();
                    }
                });
                ((CourseViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemClickListener.onItemClick(courseItemList.get(position));
                        return false;
                    }
                });
                break;
            case PROGRAMME:
                ((CourseViewHolder) holder).programmeName.setText(courseItemList.get(position).getProgramme());
                ((CourseViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(courseItemList.get(position));
                    }
                });
                break;
            case LEVEL:
                ((CourseViewHolder) holder).level.setText(courseItemList.get(position).getLevel());
                ((CourseViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(courseItemList.get(position));
                    }
                });
                break;
            case DIALOG_COURSE:
                ((CourseViewHolder) holder).courseCodeTextView.setText(courseItemList.get(position).getCourseCode());
                ((CourseViewHolder) holder).courseNameTextView.setText(courseItemList.get(position).getCourseName());
                ((CourseViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(courseItemList.get(position));
                    }
                });
        }
    }

//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//                String query = charSequence.toString().toLowerCase();
//                filteredList.clear();
//
//                if (query.isEmpty()) {
//                    filteredList.addAll(courseItemList);
//                    Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
//                } else {
//                    for (CourseItem item : courseItemList) {
//                        if (item.getCourseCode().toLowerCase().contains(query) || item.getCourseName().toLowerCase().contains(query)) {
//                            filteredList.add(item);
//                        }
//                    }
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = filteredList;
//                return filterResults;
//            }
//
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                notifyDataSetChanged();
//            }
//        };
//    }

    @Override
    public int getItemCount() {
        return courseItemList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(CourseItem position);
    }

    public interface OnDialogItemClickListener {
        void onItemClick(CourseItem position);
    }
}
