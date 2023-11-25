package com.app.tunamusic;
import static androidx.core.app.ServiceCompat.stopForeground;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class App extends Application{
    public static final String CHANNEL_1_ID = "channel1";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createnotificationChannel();
    }

    private void createnotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription("Notification de contrôle du lecteur de musique");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    //-----------------------------------------------------------------------------------------
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Media Player")
                .setContentText("Playing audio")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);

        Notification notification = builder.build();

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    private void hideNotification() {
        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
