package com.app.tunamusic;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.OnSwipe;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.MenuItem;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NavigationButtonActivity extends AppCompatActivity {

    MyService mservice;
    Boolean isBound;
    Boolean serviceIsConnected = false;
    TextView lecteurTitle;
    ImageView btControl;
    ImageView btNext;
    CardView lecteur;


    // Gestion music
    ArrayList<String> path;
    ArrayList<String> audioAlbum;
    ArrayList<String> audioArtist;
    ArrayList<String> audioTitle;
    ArrayList<String> id = new ArrayList<String>();
    ArrayList<Music> musicArrayList;
    ArrayList<String> audioList;


    ProgressBar seekBarLecteur;

    // GETTERS
    public ArrayList<Music> getMusicArrayList() {
        if (musicArrayList == null || musicArrayList.size() == 0) {
            ReadMusic();
        }
        return musicArrayList;
    }

    public ArrayList<String> getAudioList() {
        return audioList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_button);



        lecteurTitle = findViewById(R.id.lecteurTitle);
        btControl = findViewById(R.id.lecteurControl);
        btNext = findViewById(R.id.lecteurBtNext);
        lecteur = findViewById(R.id.lecteurFlottant);
        seekBarLecteur = findViewById(R.id.lecteurSeekBar);

        path = new ArrayList<String>();
        audioAlbum = new ArrayList<String>();
        audioArtist = new ArrayList<String>();
        audioTitle = new ArrayList<String>();
        musicArrayList = new ArrayList<Music>();

        // par defaut
        setFragment(new HomeV1());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView2);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.accueil) {
                    setFragment(new HomeV1());
                } else if (id == R.id.artiste) {
                    setFragment(new Artiste());
                } else if (id == R.id.album) {
                    setFragment(new Album());
                } else if (id == R.id.recherche) {
                    setFragment(new Recherche());
                }
                return true;
            }
        });


        // Connexion au service
        final ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                MyService.LocalBinder binder = (MyService.LocalBinder) service;
                mservice = binder.getService();
                isBound = true;
                serviceIsConnected = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                mservice = null;
                isBound = false;
                serviceIsConnected = false;
            }
        };

        Intent intentService = new Intent(getApplicationContext(), MyService.class);
        bindService(intentService, connection, BIND_AUTO_CREATE);


        runtimePermission();

    }



    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (!serviceIsConnected) {
            lecteur.setVisibility(View.GONE);
        } else {
            if (mservice.isPlayingMusic()) {
                lecteur.setVisibility(View.VISIBLE);
                lecteurTitle.setText(mservice.getTitleArtist());
                if (mservice.isPlayingMusic()) {
                    btControl.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                }
                btControl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mservice.isPlayingMusic()) {
                            btControl.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                            mservice.pauseMusic();
                        } else {
                            btControl.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                            mservice.playMusic();
                        }
                    }
                });

                btNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mservice.setPath(mservice.getNextPathMusic());
                        Music music = mservice.getMusic();
                        lecteurTitle.setText(music.getTitle() + " - " + music.getArtist());
                        if (mservice.isPlayingMusic()) {
                            btControl.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                        }
                    }
                });


                Handler handler = new Handler();
                // evolution du seekbar et du temps
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                seekBarLecteur.setMax(mservice.getMusicDuration());
                                seekBarLecteur.setProgress(mservice.getMusicCursor());
                            }
                        });

                    }
                }, 2000, 2000);

            }
        }
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }


    public  void runtimePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission accordée", Toast.LENGTH_SHORT).show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]
                        { Manifest.permission.READ_MEDIA_AUDIO }, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                ReadMusic();
            }
            else {
                Toast.makeText(getApplicationContext(), "Permission non accordée", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*
      Liste les musiques dans une listView
   */
    void ReadMusic() {
        audioList = new ArrayList<>();  // list des audios

        // les donnees a recuperer pour une musique
        String[] proj = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE};


        int i = 0;
        // recuperation du fichier
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);


                    id.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                    path.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    audioAlbum.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                    audioArtist.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                    audioTitle.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));

                    // prend uniquement le titre + l'artiste + album sur l'affichage de la listView
                    String item = audioTitle.get(i) + " - " + audioArtist.get(i) + " - " + audioAlbum.get(i);
                    audioList.add(item);


                    musicArrayList.add(new Music(audioTitle.get(i),
                            audioArtist.get(i),
                            audioAlbum.get(i),
                            path.get(i),
                            i));
                    ++i;

                } while (audioCursor.moveToNext());
            }
        }
        audioCursor.close();
    }

}