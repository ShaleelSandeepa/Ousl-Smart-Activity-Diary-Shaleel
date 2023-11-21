package com.ouslsmartactivitydiary;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ouslsmartactivitydiary.data.DatabaseHelper;
import com.ouslsmartactivitydiary.item.NotificationItem;
import com.ouslsmartactivitydiary.item.StaffItem;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffViewHolder> {

    Context context;
    List<StaffItem> staffItemList;
    OnStaffItemClickListener onStaffItemClickListener;
    DatabaseHelper databaseHelper;
    Cursor cursor;

    public StaffAdapter(Context context, List<StaffItem> staffItemList, OnStaffItemClickListener onStaffItemClickListener) {
        this.context = context;
        this.staffItemList = staffItemList;
        this.onStaffItemClickListener = onStaffItemClickListener;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StaffViewHolder(LayoutInflater.from(context).inflate(R.layout.item_staff, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, @SuppressLint("RecyclerView") int position) {

        databaseHelper = new DatabaseHelper(context);

        holder.txtStaffName.setText(staffItemList.get(position).getStaffName());
        holder.txtStaffEmail.setText(staffItemList.get(position).getStaffEmail());
        holder.txtStaffDetails.setText(staffItemList.get(position).getStaffCourses());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //set the visibility of clicked item
                if (holder.linearStaffDetails.getVisibility() == View.GONE) {
                    startRotationAnimation(holder.staffDropDown, 0f, 90f);
                    holder.linearStaffDetails.setVisibility(View.VISIBLE);
                } else {
                    startRotationAnimation(holder.staffDropDown,90f, 0f);
                    holder.linearStaffDetails.setVisibility(View.GONE);
                }

                onStaffItemClickListener.onItemClick(staffItemList.get(position), position);
            }
        });

    }

    private void startRotationAnimation(ImageView staffDropDown, float from, float to) {
        // Create an ObjectAnimator for the rotation property
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(staffDropDown, "rotation", from, to);
        // Set the duration of the animation in milliseconds
        rotateAnimator.setDuration(500); // 1000 milliseconds = 1 second
        // Set the interpolator for smooth acceleration and deceleration
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        // Start the rotation animation
        rotateAnimator.start();
    }

    @Override
    public int getItemCount() {
        return staffItemList.size();
    }

    public interface OnStaffItemClickListener {
        void onItemClick(StaffItem position, int index);
    }
}
