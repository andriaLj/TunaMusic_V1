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

    Music myMusic;
    ArrayList<Music> musicArrayList;

    MyService mservice;
    Boolean isBound;


    NavigationButtonActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ma_playlist1, container, false);

        activity = (NavigationButtonActivity) getActivity();
        musicArrayList = activity.getMusicArrayList();

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
                        myMusic = new Music(musicArrayList.get(j).getTitle(),
                                musicArrayList.get(j).getArtist(), musicArrayList.get(j).getAlbum(),
                                musicArrayList.get(j).getPath(), j);

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
        ArrayList<String> audioList = activity.getAudioList();  // list des audios
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, audioList); // ajout des musiques sur un ListView
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








