package com.app.tunamusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyMusicActivity extends AppCompatActivity {
    ListView listV; // listView
    SearchView searchView;
    ArrayAdapter<String> adapter;
    ArrayList<File> listMusic;

    ArrayList<String> path;
    ArrayList<String> audioAlbum;
    ArrayList<String> audioArtist;
    ArrayList<String> audioTitle;
    ArrayList<String> id = new ArrayList<String>();

    Music myMusic;

    ArrayList<Music> musicArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music);


        listMusic = new ArrayList<File>();
        path = new ArrayList<String>();
        audioAlbum = new ArrayList<String>();
        audioArtist = new ArrayList<String>();
        audioTitle = new ArrayList<String>();
        musicArrayList = new ArrayList<Music>();


        // Lit une musique selectionnee dans la listView
        listV = findViewById(R.id.listViewSong);
        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            boolean isTitle = true;
            boolean isArtist = true;
            boolean isAlbum = true;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int j = 0; j < musicArrayList.size(); ++j) {
                    isTitle = listV.getItemAtPosition(i).toString().contains(musicArrayList.get(j).getTitle());
                    isArtist = listV.getItemAtPosition(i).toString().contains(musicArrayList.get(j).getArtist());
                    isAlbum = listV.getItemAtPosition(i).toString().contains(musicArrayList.get(j).getAlbum());

                    if (isTitle && isArtist && isAlbum) {
                        myMusic = new Music(audioTitle.get(j), audioArtist.get(j), audioAlbum.get(j), path.get(j), j);

                        Intent intent = new Intent(getApplicationContext(), LecteurActivity.class);
                        intent.putExtra("MUSIC", myMusic);
                        intent.putParcelableArrayListExtra("MUSIC_ARRAY", musicArrayList);
                        startActivity(intent);
                        break;
                    }
                }
            }


        });


        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
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
            Toast.makeText(MyMusicActivity.this, "Permission accordée", Toast.LENGTH_SHORT).show();

        } else {
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
                Toast.makeText(this, "Permission non accordée", Toast.LENGTH_SHORT).show();
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
                          MediaStore.Audio.Media.ARTIST,
                          MediaStore.Audio.Media.TITLE };

        int i = 0;
        // recuperation du fichier
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        if(audioCursor != null){
            if(audioCursor.moveToFirst()){
                do{
                    int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);


                    id.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                    path.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    audioAlbum.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                    audioArtist.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                    audioTitle.add(audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));

                    // prend uniquement le titre + l'artiste + album sur l'affichage de la listView
                    String item = audioTitle.get(i) + " - " + audioArtist.get(i) + " - " +audioAlbum.get(i);
                    audioList.add(item);
//                    audioList.add(audioCursor.getString(audioIndex)
//                            .replace(".mp3", "")
//                            .replace(".wma", "")
//                            .replace("MP3", ""));


                    musicArrayList.add(new Music(audioTitle.get(i),
                            audioArtist.get(i),
                            audioAlbum.get(i),
                            path.get(i),
                            i));
                    ++i;

                } while(audioCursor.moveToNext());
            }
        }
        audioCursor.close();

        // ajout des musiques sur un ListView
        /*ArrayAdapter<String>*/ adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, audioList);
        listV.setAdapter(adapter);

    }
}