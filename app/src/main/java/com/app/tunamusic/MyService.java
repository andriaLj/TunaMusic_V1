package com.app.tunamusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.util.ArrayList;

public class MyService extends Service {
    MediaPlayer mediaPlayer; // permet la gestion d'un audio
    String path; // contient le chemin d'acces de l'audio
    String titleArtist; // contient le titre et le l'artiste de l'audio
    int musicIndex;
    ArrayList<Music> musicArrayList;

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
        if (mediaPlayer != null && !mediaPlayer.isPlaying()){ mediaPlayer.start();
        startForeground(1,buildNotification());}//Créer la notification de Foregroundservice ou la met à jour.
    }

    // met la musique en pause
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()){ mediaPlayer.pause();
        startForeground(1,buildNotification());} //Créer la notification de Foregroundservice ou la met à jour.
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        path = intent.getStringExtra("MusicPath"); // initialise le chemin d'acces
        titleArtist = intent.getStringExtra("MUSIC_TITLE");
        musicIndex = intent.getIntExtra("MUSIC_INDEX", -1);
        musicArrayList = intent.getParcelableArrayListExtra("LIST_MUSIC");

        startForeground(1,buildNotification()); //Créer la notification de Foregroundservice ou la met à jour.


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
            stopForeground(Service.STOP_FOREGROUND_REMOVE);
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // arrete la musique
            mediaPlayer.release(); // tue l'instance de l'objet
        }
    }
    public Notification buildNotification() {
        Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.drawable.album);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_IMMUTABLE);

        NotificationReceiver receiver = new NotificationReceiver();
        receiver.onCreate();

        Intent intentService = new Intent(this, receiver.getClass());
        PendingIntent pendingService = PendingIntent.getBroadcast(this,0,intentService,PendingIntent.FLAG_MUTABLE);

        return  new NotificationCompat.Builder(this, "music_player")
                .setContentTitle(musicArrayList.get(musicIndex).getTitle())
                .setContentText(musicArrayList.get(musicIndex).getArtist())
                .setContentIntent(pendingIntent)
                .setLargeIcon(artwork)
                .setSmallIcon(R.drawable.logo__2_)
                .addAction(R.drawable.ic_action_previous,"Previous",pendingService)
                .addAction(R.drawable.ic_action_play, "Play", pendingService)
                .addAction(R.drawable.ic_action_next, "Next", pendingService)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .build();

    }
}
