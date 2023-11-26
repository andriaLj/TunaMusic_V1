package com.app.tunamusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
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
    ImageView addTofavoris;
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

    MyDataBase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecteur);


//        createNotificationChannel();

        db = new MyDataBase(getApplicationContext());

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
                    }
                    else
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
        intentService.putExtra("MUSIC_TITLE", music.getTitle() + " - " + music.getArtist());
        intentService.putExtra("MusicPath", music.getPath());
        intentService.putParcelableArrayListExtra("LIST_MUSIC", musicArrayList);
        intentService.putExtra("MUSIC_INDEX", music.getIndex());
        startService(intentService);
        bindService(intentService, connection, BIND_AUTO_CREATE);


        MaterialToolbar materialToolbar = findViewById(R.id.toolbarLecteur);
        setSupportActionBar(materialToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        addTofavoris = findViewById(R.id.addToFavori);
        addTofavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!music.isMusicInFavoris()) {
                    addMusicToFavoris(music);
                    addTofavoris.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.coeur));
                } else {
                    removeMusicTOFavoris(music);
                    addTofavoris.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.coeurvide));
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
                        if ( music != null) {
                            mservice.showNotification(mservice.getMusic());
                            if (music.isMusicInFavoris()) {
                                addTofavoris.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.coeur));
                            } else {
                                addTofavoris.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.coeurvide));
                            }
                        }

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
                                    mservice.setPath(musicArrayList.get(music.getIndex() + 1).getPath());
                                    music = musicArrayList.get(music.getIndex() + 1);
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

        }, 500, 1000); // mettre delay : 2500 si crash

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
            Intent intent = new Intent(getApplicationContext(), NavigationButtonActivity.class);
            moveTaskToBack(true);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), NavigationButtonActivity.class);
        moveTaskToBack(true);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mservice != null && isBound) {
            infoMusic.setText(mservice.getTitleArtist());
            isPlaying = mservice.isPlayingMusic();
            if (!isPlaying) {
                btPlay.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play));
            }
        }
    }


    public void addMusicToFavoris(Music music) {
        music.setMusicInFavoris(true);
        boolean res = db.insert(music);
        if (res) {
            Toast.makeText(getApplicationContext(), "Ajoutée aux favoris", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Echec", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeMusicTOFavoris(Music music) {
        if (music == null) return;
        music.setMusicInFavoris(false);
        db.deleteInfo(music);
    }


    //------------------------------------- NOTIFICATION ----------------------------------------
//    private static final String CHANNEL_ID = "my_channel";
//    private static final int NOTIFICATION_ID = 1;
//    private static int notificationRequestCode = 0;
//
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "My Channel";
//            String description = "Channel for my notifications";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    // appel quand une musique en cours de lecture
//    private void showNotification(Music music) {
//        Intent intent = new Intent(this, LecteurActivity.class); // Replace YourTargetActivity with the desired activity
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this,
//                notificationRequestCode++,
//                intent,
//                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//
//
//
//        Intent actionIntentPlay = new Intent(this, ActionReceiver.class);
//        actionIntentPlay.setAction("ACTION_PLAY");
//        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(
//                this,
//                0,
//                actionIntentPlay,
//                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        Intent actionIntentPause = new Intent(this, ActionReceiver.class);
//        actionIntentPause.setAction("ACTION_PAUSE");
//        PendingIntent actionPendingIntentPause = PendingIntent.getBroadcast(
//                this,
//                0,
//                actionIntentPause,
//                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        if (pendingIntent != null) {
//            String title = "Title";
//            String artist = "Artist";
//            if (music != null) {
//                title = music.getTitle();
//                artist = music.getArtist();
//            }
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(title)
//                    .setContentText(artist)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setContentIntent(pendingIntent)
//                    .addAction(R.drawable.ic_play, "Play", actionPendingIntent)
//                    .addAction(R.drawable.ic_action_pause, "Pause", actionPendingIntentPause)
//                    .setAutoCancel(true);
//
//
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                return;
//            }
//            notificationManager.notify(NOTIFICATION_ID, builder.build());
//        } else {
//            Toast.makeText(this, "utu", Toast.LENGTH_SHORT).show();
//        }
//    }
}