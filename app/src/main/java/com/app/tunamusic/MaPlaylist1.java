package com.app.tunamusic;

import static android.content.Context.BIND_AUTO_CREATE;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MaPlaylist1 extends Fragment {

    ListView listV; // listView
    TextView nbTitle;
    TextView duree;
    ArrayAdapter<String> adapter;
    ArrayList<File> listMusic;

    ArrayList<String> path;
    ArrayList<String> audioAlbum;
    ArrayList<String> audioArtist;
    ArrayList<String> audioTitle;
    ArrayList<String> id = new ArrayList<String>();

    Music myMusic;
    ArrayList<Music> musicArrayList;

    MyService mservice;
    Boolean isBound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ma_playlist1, container, false);

        listMusic = new ArrayList<File>();
        path = new ArrayList<String>();
        audioAlbum = new ArrayList<String>();
        audioArtist = new ArrayList<String>();
        audioTitle = new ArrayList<String>();
        musicArrayList = new ArrayList<Music>();


        nbTitle = view.findViewById(R.id.nbTitle);
        duree = view.findViewById(R.id.duree);

        // Lit une musique selectionnee dans la listView
        listV = view.findViewById(R.id.listVMP1);
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

                        Intent intent = new Intent(getContext(), LecteurActivity.class);
                        intent.putExtra("MUSIC", myMusic);
                        intent.putParcelableArrayListExtra("MUSIC_ARRAY", musicArrayList);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    }
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



        Intent intentService = new Intent(getContext(), MyService.class);
        requireActivity().bindService(intentService, connection, BIND_AUTO_CREATE);

        runtimePermission();

        MaterialToolbar materialToolbar = view.findViewById(R.id.materialToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(materialToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return view;
    }

    /*
       Recuperation des permissions
    */
    public  void runtimePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permission accordée", Toast.LENGTH_SHORT).show();

        } else {
            requestPermissions(new String[]
                    { Manifest.permission.READ_MEDIA_AUDIO }, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                afficheMusic();
                nbTitle.setText("Nombre de titres : " + musicArrayList.size());
            }
            else {
                Toast.makeText(getContext(), "Permission non accordée", Toast.LENGTH_SHORT).show();
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
        Cursor audioCursor = requireContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
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
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, audioList);
        listV.setAdapter(adapter);
    }


    // conversion Milliseconde a minute:seconde
    public String millisecondToMinuteSeconde(int millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        String str = seconds < 10 ? minutes + "mn 0" + seconds + "s": minutes + "mn " + seconds + "s";
        return str;
    }

    private void setFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    // Appui sur bouton retour
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setFragment(new Recherche());
            Toast.makeText(getContext(), "toto", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


}








