package com.app.tunamusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.util.ArrayList;

public class MyService extends Service {
    MediaPlayer mediaPlayer; // permet la gestion d'un audio
    String path; // contient le chemin d'acces de l'audio
    String titleArtist; // contient le titre et le l'artiste de l'audio
    int musicIndex;
    ArrayList<Music> musicArrayList;

    private static MyService instance;

    public static MyService getInstance() {
        return instance;
    }


    private final IBinder binder = new LocalBinder(); // relie la liaison avec une activity

    public Music getMusic() { return musicArrayList.get(musicIndex); }
    public String getPreviousPathMusic() {
        return musicIndex == 0 ? musicArrayList.get(musicIndex = (musicArrayList.size() - 1)).getPath() :
                                musicArrayList.get(--musicIndex).getPath();
    }
    public String getNextPathMusic() {
        return musicIndex >= musicArrayList.size() - 1 ?
                musicArrayList.get(musicIndex = 0).getPath() : musicArrayList.get(++musicIndex).getPath();
    }
    public int getMusicIndex() { return musicIndex; }
    public String getPath() {
        return path;
    }
    public void setTitleArtist(String titleArtist) {
        this.titleArtist = titleArtist;
    }
    public String getTitleArtist() {
        return titleArtist;
    }
    public boolean isPlayingMusic() {
        return mediaPlayer.isPlaying();
    }

    public void setCursorPosition(int seek) {
        mediaPlayer.seekTo(seek);
    }

    public int getMusicCursor() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getMusicDuration() {
        return mediaPlayer.getDuration();
    }


    // lance la lecture de la musique
    public void playMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()){ mediaPlayer.start(); }
//        startForeground(1,buildNotification());}//Créer la notification de Foregroundservice ou la met à jour.
    }

    // met la musique en pause
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()){ mediaPlayer.pause(); }
//        startForeground(1,buildNotification());} //Créer la notification de Foregroundservice ou la met à jour.
    }

    public void setPath(String path) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); // reinitialise mediaplayer
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path); // initialisation du chemin dans mediaplayer
            mediaPlayer.prepare();
            mediaPlayer.start(); // lance la musique
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Lecture impossible", Toast.LENGTH_SHORT).show();
        }
    }


    // gestion de liaison du service avec d'autre activity
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        createNotificationChannel();
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        path = intent.getStringExtra("MusicPath"); // initialise le chemin d'acces
        titleArtist = intent.getStringExtra("MUSIC_TITLE");
        musicIndex = intent.getIntExtra("MUSIC_INDEX", -1);
        musicArrayList = intent.getParcelableArrayListExtra("LIST_MUSIC");

//        startForeground(1,buildNotification()); //Créer la notification de Foregroundservice ou la met à jour.


        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); // reinitialise mediaplayer
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path); // initialisation du chemin dans mediaplayer
            mediaPlayer.prepare();
            mediaPlayer.start(); // lance la musique
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Lecture impossible", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY; // Pour que le service soit redémarré automatiquement s'il est tué par le système
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            stopForeground(Service.STOP_FOREGROUND_REMOVE);
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // arrete la musique
            mediaPlayer.release(); // tue l'instance de l'objet
        }
    }


    // ------------------------------------ NOTIFICATION -------------------------------------------
    private static final String CHANNEL_ID = "my_channel";
    private static final int NOTIFICATION_ID = 1;
    private static int notificationRequestCode = 0;


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel for my notifications";
            int importance = NotificationManager.IMPORTANCE_LOW;
//            int importance = Notification.FOREGROUND_SERVICE_IMMEDIATE;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // appel quand une musique en cours de lecture
    public void showNotification(Music music) {
        Intent intent = new Intent(this, MyService.class); // Replace YourTargetActivity with the desired activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                notificationRequestCode++,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );



        Intent actionIntentNext = new Intent(this, ActionReceiver.class);
        actionIntentNext.setAction("ACTION_NEXT");
        PendingIntent actionPendingIntentNext = PendingIntent.getBroadcast(
                this,
                0,
                actionIntentNext,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent actionIntentPrevious = new Intent(this, ActionReceiver.class);
        actionIntentPrevious.setAction("ACTION_PREV");
        PendingIntent actionPendingIntentPrevious = PendingIntent.getBroadcast(
                this,
                0,
                actionIntentPrevious,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );


        if (pendingIntent != null) {
            String title = "Title";
            String artist = "Artist";
            if (music != null) {
                title = music.getTitle();
                artist = music.getArtist();
            }
            NotificationCompat.Builder builder;
            if(isPlayingMusic()){
                Intent actionIntentPause = new Intent(this, ActionReceiver.class);
                actionIntentPause.setAction("ACTION_PAUSE");
                PendingIntent actionPendingIntentPause = PendingIntent.getBroadcast(
                        this,
                        0,
                        actionIntentPause,
                        PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                );
                builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(artist)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.ic_action_previous, "Previous", actionPendingIntentPrevious)
                        .addAction(R.drawable.ic_action_pause, "Pause", actionPendingIntentPause)
                        .addAction(R.drawable.ic_action_next, "Next", actionPendingIntentNext)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2))
                        .setVibrate(new long[]{0})  // Empty array disables vibration
                        .setAutoCancel(true);
            }else{
                Intent actionIntentPlay = new Intent(this, ActionReceiver.class);
                actionIntentPlay.setAction("ACTION_PLAY");
                PendingIntent actionPendingIntentPlay = PendingIntent.getBroadcast(
                        this,
                        0,
                        actionIntentPlay,
                        PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                );
                
                builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(artist)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.ic_action_previous, "Previous", actionPendingIntentPrevious)
                        .addAction(R.drawable.ic_action_play, "Play", actionPendingIntentPlay)
                        .addAction(R.drawable.ic_action_next, "Next", actionPendingIntentNext)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2))
                        .setVibrate(new long[]{0})  // Empty array disables vibration
                        .setAutoCancel(true);
            }



            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return;
            }
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
        }
    }
}
