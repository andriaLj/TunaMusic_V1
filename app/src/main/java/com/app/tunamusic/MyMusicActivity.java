package com.app.tunamusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyMusicActivity extends AppCompatActivity {
    ListView listV; // listView
    ArrayList<File> listMusic;

    ArrayList<String> path;
    ArrayList<String> audioAlbum;
    ArrayList<String> audioArtist;

    public MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music);


        listMusic = new ArrayList<File>();
        path = new ArrayList<String>();
        audioAlbum = new ArrayList<String>();
        audioArtist = new ArrayList<String>();



        mediaPlayer = new MediaPlayer();


        // Lit une musique selectionnee dans la listView
        listV = findViewById(R.id.listViewSong);
        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), path.get(i), Toast.LENGTH_SHORT).show();

                try {
                    if(mediaPlayer != null) {
                        mediaPlayer.reset();
                    }
                    mediaPlayer.setDataSource(path.get(i));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        runtimePermission();


    }


    /*
        Recuperation des permissions
     */
    public  void runtimePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MyMusicActivity.this, "Permission allowed", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(MyMusicActivity.this, "Permission not allowed", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MyMusicActivity.this, new String[]
                    { Manifest.permission.READ_MEDIA_AUDIO }, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                afficheMusic();
            }
            else {
                Toast.makeText(this, "Pas de permission", Toast.LENGTH_SHORT).show();
            }

        }


    }


    /*
        Liste les musiques dans une listView
     */
    void afficheMusic() {
        ArrayList<String> audioList = new ArrayList<>();  // list des audios

        // les donnees a recuperer pour une musique
        String[] proj = { MediaStore.Audio.Media._ID,
                          MediaStore.Audio.Media.DISPLAY_NAME,
                          MediaStore.Audio.Media.DATA,
                          MediaStore.Audio.Media.ALBUM,
                          MediaStore.Audio.Media.ARTIST };

        // recuperation du fichier
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        if(audioCursor != null){
            if(audioCursor.moveToFirst()){
                do{
                    int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    path.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    audioAlbum.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                    audioArtist.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                    audioList.add(audioCursor.getString(audioIndex));
                }while(audioCursor.moveToNext());
            }
        }
        audioCursor.close();

        // ajout des musiques sur un ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, audioList);
        listV.setAdapter(adapter);


    }
}