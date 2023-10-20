package com.app.tunamusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.tunamusic.MyService;
import com.google.android.material.appbar.MaterialToolbar;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
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

//    SeekbarParcelable seekbarPar;

    SeekBar seekBar; // bar glissante
    TextView infoMusic;
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

        seekBar= findViewById(R.id.seekBar);

        infoMusic = findViewById(R.id.txt_info);


        btPlay = findViewById(R.id.btPlay);
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
                isPlaying = !isPlaying;
            }
        });

        btPrevious = findViewById(R.id.bt_previous);
        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBound && mservice != null) {
                    // evite tout depassement
                    if(music.getIndex() > 0) {
                        mservice.setPath(musicArrayList.get(music.getIndex() - 1).getPath());
                        music = musicArrayList.get(music.getIndex() - 1);
                        infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                    } else {
                        mservice.setPath(musicArrayList.get(musicArrayList.size() - 1).getPath());
                        music = musicArrayList.get(musicArrayList.size() - 1);
                        infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                    }
                }
            }
        });

        btNext = findViewById(R.id.bt_next);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBound && mservice != null) {
                    // evite tout depassement
                    if(music.getIndex() == musicArrayList.size() - 1) {
                        mservice.setPath(musicArrayList.get(0).getPath());
                        music = musicArrayList.get(0);
                        infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                    } else {
                        mservice.setPath(musicArrayList.get(music.getIndex() + 1).getPath());
                        music = musicArrayList.get(music.getIndex() + 1);
                        infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                    }
                }
            }
        });



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


        Intent intent = getIntent();
        music = intent.getParcelableExtra("MUSIC");
        musicArrayList = intent.getParcelableArrayListExtra("MUSIC_ARRAY");


        infoToPrint = music.getTitle() + " - " + music.getArtist();
        infoMusic.setText(infoToPrint);

        Intent intentService = new Intent(getApplicationContext(), MyService.class);
        intentService.putExtra("MusicPath", music.getPath());
        startService(intentService);
        bindService(intentService, connection, BIND_AUTO_CREATE);




        MaterialToolbar materialToolbar = findViewById(R.id.toolbarLecteur);
        setSupportActionBar(materialToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // evolution du seekbar
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isBound && mservice != null) {
                    seekBar.setMax(mservice.getMusicDuration());
                    seekBar.setProgress(mservice.getMusicCursor());
                    // passage a la musique suivante si on arrive a la fin
                    if (mservice.getMusicCursor() + 500 >= mservice.getMusicDuration()) {
                        // identique au bouton next
                        if(music.getIndex() == musicArrayList.size() - 1) {
                            mservice.setPath(musicArrayList.get(0).getPath());
                            music = musicArrayList.get(0);
                            infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                        } else {
                            mservice.setPath(musicArrayList.get(music.getIndex() + 1).getPath());
                            music = musicArrayList.get(music.getIndex() + 1);
                            infoMusic.setText(music.getTitle() + " - " + music.getArtist());
                        }
                    }
                    // ecoute changement d'etat de la bar de progression
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            // avancer ou reculer la musique
                            mservice.setCursorPosition(seekBar.getProgress());
                        }
                    });
                }
            }

        }, 2000, 1000);


        // reception de donnee depuis le service
        dataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                seekBar = findViewById(R.id.seekBar);
//                seekBar.setMax(Integer.parseInt(intent.getStringExtra("MAX_CURSEUR")));
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        seekBar.setProgress(Integer.parseInt(intent.getStringExtra("CURSEUR")));
//                    }
//                }, 0, 2000);
            }

        };

    }
}