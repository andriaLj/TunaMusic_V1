package com.app.tunamusic;

import static android.content.Intent.getIntent;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    MediaPlayer mediaPlayer; // permet la gestion d'un audio
    String path; // contient le chemin d'acces de l'audio

    private final IBinder binder = new LocalBinder(); // relie la liaison avec une activity


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
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) mediaPlayer.start();
    }

    // met la musique en pause
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
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

    private void workBackground() {

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
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY; // Pour que le service soit redémarré automatiquement s'il est tué par le système
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // arrete la musique
            mediaPlayer.release(); // tue l'instance de l'objet
        }
    }
}
