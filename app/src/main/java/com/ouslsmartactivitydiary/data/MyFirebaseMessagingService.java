package com.ouslsmartactivitydiary.data;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.activity.DashboardActivity;
import com.ouslsmartactivitydiary.data.DatabaseHelper;
import com.ouslsmartactivitydiary.fragment.MyCalendarFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    DatabaseHelper databaseHelper;
    boolean isAdded;
    private DataCallback callback;

    public MyFirebaseMessagingService(DataCallback callback) {
        this.callback = callback;
    }

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if (message.getNotification() != null) {
            databaseHelper = new DatabaseHelper(this);
            Date currentDate = new Date();
            // Format the date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(currentDate);
            // Format the time
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String formattedTime = timeFormat.format(currentDate);
            isAdded = databaseHelper.addNotification(formattedDate, formattedTime, "unread", message.getNotification().getTitle(),
                    (message.getNotification().getBody()));
            if (isAdded) {
                sendNotification(message.getNotification().getTitle(), message.getNotification().getBody());
                notifyActivity();
            }

        }

    }

    public void sendNotification(String title, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            String channelId = "ouslsmartactivitydiary";
            CharSequence channelName = "OUSL SMART ACTIVITY DIARY";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            // Register the channel with the system
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ouslsmartactivitydiary")
                .setSmallIcon(R.drawable.ic_final_2)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Set a large icon for the notification
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.round_logo);
        builder.setLargeIcon(largeIcon);

        int notificationId = 101;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());

    }

    private void notifyActivity() {
        // Check if the callback is not null
        if (callback != null) {
            // Notify the activity with the received data
            callback.onDataReceived();
        }
    }

    // Define a callback interface
    public interface DataCallback {
        void onDataReceived();
    }

}
