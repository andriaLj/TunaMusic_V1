package com.app.tunamusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LecteurActivity extends AppCompatActivity {

    ImageView btPlay;
    ImageView btNext;
    ImageView btPrevious;
    SeekBar seekBar; // bar glissante
    TextView infoMusic;
    TextView minSec;
    String infoToPrint;
    Music music;
    ArrayList<Music> musicArrayList;
    MyService mservice;
    Boolean isBound;
    BroadcastReceiver dataReceiver;
    Boolean isPlaying = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecteur);

        seekBar = findViewById(R.id.seekBar);

        infoMusic = findViewById(R.id.txt_info);
        minSec = findViewById(R.id.minSectxt);

        btPlay = findViewById(R.id.lecteurPlayPause);
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBound && mservice != null && isPlaying) {
                    mservice.pauseMusic();
                    btPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play));
                } else if (isBound && mservice != null && !isPlaying) {
                    mservice.playMusic();
                    btPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_pause_circle_filled_24));
                }
                isPlaying = mservice.isPlayingMusic();
            }
        });

        btPrevious = findViewById(R.id.bt_previous);
        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBound && mservice != null) {
                    if (seekBar.getProgress() > 2550) {
                        mservice.setCursorPosition(0);
                    } else {
                        // evite tout depassement
                        if (music.getIndex() > 0) {
                            mservice.setPath(musicArrayList.get(music.getIndex() - 1).getPath());
                            music = musicArrayList.get(music.getIndex() - 1);
                            infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                        } else {
                            mservice.setPath(musicArrayList.get(musicArrayList.size() - 1).getPath());
                            music = musicArrayList.get(musicArrayList.size() - 1);
                            infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                        }
                    }
                    if (mservice.isPlayingMusic()) {
                        isPlaying = true;
                        btPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_pause_circle_filled_24));
                    } else
                        btPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play));
                }
            }
        });

        btNext = findViewById(R.id.bt_next);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isBound && mservice != null) {
                    // evite tout depassement
                    if (music.getIndex() >= musicArrayList.size() - 1) {
                        mservice.setPath(musicArrayList.get(0).getPath());
                        music = musicArrayList.get(0);
                        infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                    } else {
//                        mservice.setPath(musicArrayList.get(music.getIndex() + 1).getPath());
                        mservice.setPath(mservice.getNextPathMusic());
                        music = mservice.getMusic();
//                        music = musicArrayList.get(music.getIndex() + 1);
                        infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                    }
                    if (mservice.isPlayingMusic()) {
                        isPlaying = true;
                        btPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_pause_circle_filled_24));
                    } else
                        btPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play));
                }
            }
        });


        // Connexion au service
        final ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                MyService.LocalBinder binder = (MyService.LocalBinder) service;
                mservice = binder.getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                mservice = null;
                isBound = false;
            }
        };

        //--------- METTRE CODE NOTIFICATION ICI -----------


        //---------------------- END NOTIF -----------------


        Intent intent = getIntent();
        music = intent.getParcelableExtra("MUSIC");
        musicArrayList = intent.getParcelableArrayListExtra("MUSIC_ARRAY");
        infoToPrint = music.getTitle() + " - " + music.getArtist();
        infoMusic.setText(infoToPrint);

        Intent intentService = new Intent(getApplicationContext(), MyService.class);
        intentService.putExtra("MUSIC_TITLE", music.getTitle() + " " + music.getArtist());
        intentService.putExtra("MusicPath", music.getPath());
        intentService.putParcelableArrayListExtra("LIST_MUSIC", musicArrayList);
        intentService.putExtra("MUSIC_INDEX", music.getIndex());
        startService(intentService);
        bindService(intentService, connection, BIND_AUTO_CREATE);


        MaterialToolbar materialToolbar = findViewById(R.id.toolbarLecteur);
        setSupportActionBar(materialToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Handler handler = new Handler();
        // evolution du seekbar et du temps
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isBound && mservice != null) {
                            seekBar.setMax(mservice.getMusicDuration());
                            seekBar.setProgress(mservice.getMusicCursor());
                            // passage a la musique suivante si on arrive a la fin
                            if (mservice.getMusicCursor() + 500 >= mservice.getMusicDuration()) {
                                // identique au bouton next
                                if (music.getIndex() == musicArrayList.size() - 1) {
                                    mservice.setPath(musicArrayList.get(0).getPath());
                                    music = musicArrayList.get(0);
                                    infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                                } else {
//                                    mservice.setPath(musicArrayList.get(music.getIndex() + 1).getPath());
                                    mservice.setPath(mservice.getNextPathMusic());
                                    music = /*musicArrayList.get(music.getIndex() + 1);*/ mservice.getMusic();
                                    infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                                }
                            }
                            // ecoute changement d'etat de la bar de progression
                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                    String temps = millisecondToMinuteSeconde(/*mservice.getMusicCursor()*/ seekBar.getProgress());
                                    minSec.setText(temps);
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                    mservice.setCursorPosition(seekBar.getProgress());  // avancer ou reculer la musique
                                }
                            });
                        }
                    }
                });
            }

        }, 2500, 1000);

        // reception de donnee depuis le service
        dataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };


    }

    // conversion Milliseconde a minute:seconde
    public String millisecondToMinuteSeconde(int millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        String str = seconds < 10 ? minutes + ":0" + seconds : minutes + ":" + seconds;
        return str;
    }


    // Appui sur bouton retour
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}