package com.app.tunamusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.*;
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener;

import java.util.Timer;
import java.util.TimerTask;

public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.billie_jean);
        ImageView btPlay = findViewById(R.id.lecteurPlayPause);
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });

        ImageView btPause = findViewById(R.id.lecteurControl);
        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(mediaPlayer.getDuration());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progressBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }, 0, 2000);

        MaterialToolbar materialToolbar = findViewById(R.id.toolbarPlaylist);
        setSupportActionBar(materialToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BottomNavigationView bottomNavi = findViewById(R.id.bottomNavigationView2);
        bottomNavi.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.accueil) {
                    intent = new Intent(getApplicationContext(), AccueilActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recherche) {
                    intent = new Intent(getApplicationContext(), PlaylistActivity.class);
                    startActivity(intent);
                }
//                else if (item.getItemId() == R.id.menu) {
////                    intent = new Intent(getApplicationContext(), PlaylistActivity.class);
////                    startActivity(intent);
//                }

                return false;
            }
        });

    }

    // Appui sur bouton retour
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}