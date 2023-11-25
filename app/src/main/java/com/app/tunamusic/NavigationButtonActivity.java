package com.app.tunamusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.view.GestureDetector;
import android.view.MenuItem;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.HashMap;
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

    ArrayList<Music> favoriArrayList;


    ProgressBar seekBarLecteur;

    private GestureDetector gestureDetector;

    MyDataBase db;


    public MyDataBase getDb() {
        return db;
    }

    public void deleteAllMusicInFavoris() {
        db.deleteAllInfo();
    }
    public void removeMusicTOFavoris(int index) {
        if (favoriArrayList == null) return;
        db.deleteInfo(favoriArrayList.get(index));
        favoriArrayList.remove(index);
    }

    public void addMusicToFavoris(Music music) {
        boolean res = db.insert(music);
        if (res) {
            Toast.makeText(getApplicationContext(), "Ajoutée aux favoris", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Echec", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<Music> getFavoriArrayList() {
        /*if (favoriArrayList == null)*/favoriArrayList = new ArrayList<>();
        Cursor cursor = db.getAllInfo();
        int i = 0;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                favoriArrayList.add(new Music(cursor.getString(1), // titre
                        "", // artiste
                        "", // album
                        "", // path
                        i++)); // index
            }

            int size = musicArrayList.size();
            HashMap<String, Music> map = new HashMap<>();
            for (int j = 0; j < size; ++j) {
                map.put(musicArrayList.get(j).getTitle(), musicArrayList.get(j));
            }


            size = favoriArrayList.size();
            for (int j = 0; j < size; ++j) {
                if (map.containsKey(favoriArrayList.get(j).getTitle())) {
                    Music music = map.get(favoriArrayList.get(j).getTitle());
                    favoriArrayList.get(j).setAlbum(music.getAlbum());
                    favoriArrayList.get(j).setPath(music.getPath());
                    favoriArrayList.get(j).setArtist(music.getArtist());
                }
            }
        }
        return favoriArrayList;
    }

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

        db = new MyDataBase(getApplicationContext());

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
                } else if (id == R.id.maFavori) {
                    setFragment(new Favoris());
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

        // Initialize GestureDetector
        gestureDetector = new GestureDetector(this, new MyGestureListener());
        lecteur.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

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
                            Music music;
                            @Override
                            public void run() {
                                if (mservice.getMusicCursor() + 500 >= mservice.getMusicDuration()) {
                                    mservice.setPath(mservice.getNextPathMusic());
                                    music = mservice.getMusic();
                                    mservice.setTitleArtist(music.getTitle() + " - " + music.getArtist());
                                    lecteurTitle.setText(mservice.getTitleArtist());
                                }
                                seekBarLecteur.setMax(mservice.getMusicDuration());
                                seekBarLecteur.setProgress(mservice.getMusicCursor());
                                music = mservice.getMusic();
                                mservice.setTitleArtist(music.getTitle() + " - " + music.getArtist());
                                lecteurTitle.setText(mservice.getTitleArtist());
                                if (mservice.isPlayingMusic()) {
                                    btControl.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                                } else {
                                    btControl.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                                }


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

    // Custom GestureListener to handle swipe gestures
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY) &&
                    Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                // Right swipe
                if (diffX > 0) {
                    mservice.setPath(mservice.getPreviousPathMusic()); // go to previous music
                    return true;
                }
                // Left swipe
                else {
                    mservice.setPath(mservice.getNextPathMusic()); // go to next music
                    return true;
                }
            }

            if (e1.getY() > e2.getY()) {
                // MOVE UP
                Intent intent = new Intent(getApplicationContext(), LecteurActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            } else {
                // MOVE DOWN
                mservice.pauseMusic();
                lecteur.setVisibility(View.GONE);
            }

            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}