package com.ouslsmartactivitydiary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

    Context context;
    List<ItemNotification> notificationList;
    OnNotificationClickListener onNotificationClickListener;
    DatabaseHelper databaseHelper;
    Cursor cursor;
    AlertDialog.Builder alertDialog;
    int accountID;

    public NotificationAdapter(Context context, List<ItemNotification> notificationList, OnNotificationClickListener onNotificationClickListener) {
        this.context = context;
        this.notificationList = notificationList;
        this.onNotificationClickListener = onNotificationClickListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, @SuppressLint("RecyclerView") int position) {

        databaseHelper = new DatabaseHelper(context);

        holder.notificationTopic.setText(notificationList.get(position).getNotificationTopic());
        holder.notificationDetails.setText(notificationList.get(position).getNotificationDetails());
        holder.notificationDate.setText(notificationList.get(position).getNotificationDate());
        holder.notificationTime.setText(notificationList.get(position).getNotificationTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                notificationList.get(position).setNotificationColor(ContextCompat.getColor(context, R.color.white));

                //set the visibility of clicked notification
                if (holder.notificationDetails.getVisibility() == View.GONE) {
                    holder.notificationDetails.setVisibility(View.VISIBLE);
                } else {
                    holder.notificationDetails.setVisibility(View.GONE);
                }

                // set the color of notification
                databaseHelper.updateNotificationState(notificationList.get(position).getNotificationID(), "read");
                holder.layoutColor.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

                onNotificationClickListener.onItemClick(notificationList.get(position), position, "itemView");
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteNotification(notificationList.get(position).getNotificationID());
                onNotificationClickListener.onItemClick(notificationList.get(position), position, "delete");
            }
        });

        cursor = databaseHelper.getNotificationByID(notificationList.get(position).getNotificationID());
        while (cursor.moveToNext()) {
            if (cursor.getString(3).equals("read")) {
                holder.layoutColor.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        }

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public interface OnNotificationClickListener {
        void onItemClick(ItemNotification position, int index, String clickType);
    }
}
