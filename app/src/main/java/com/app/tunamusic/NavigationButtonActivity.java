package com.app.tunamusic;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.OnSwipe;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
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

    ProgressBar seekBarLecteur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_button);

        lecteurTitle = findViewById(R.id.lecteurTitle);
        btControl = findViewById(R.id.lecteurControl);
        btNext = findViewById(R.id.lecteurBtNext);
        lecteur = findViewById(R.id.lecteurFlottant);
        seekBarLecteur = findViewById(R.id.lecteurSeekBar);

        // par defaut
        setFragment(new Home());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView2);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.accueil) {
                    setFragment(new Home());
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
}