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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    MediaPlayer mediaPlayer; // permet la gestion d'un audio
    String path; // contient le chemin d'acces de l'audio
    String titleArtist; // contient le titre et le l'artiste de l'audio
    int musicIndex;
    ArrayList<Music> musicArrayList;

    private final IBinder binder = new LocalBinder(); // relie la liaison avec une activity


    public Music getMusic() { return musicArrayList.get(musicIndex); }
    public String getNextPathMusic() {
        return getMusic().getIndex() < musicArrayList.size() ?
                musicArrayList.get(++musicIndex).getPath() : musicArrayList.get(0).getPath();
    }
    public int getMusicIndex() { return musicIndex; }
    public String getPath() {
        return path;
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
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop(); // arrete la musique
            mediaPlayer.release(); // tue l'instance de l'objet
        }
    }
}
