package com.app.tunamusic;

import android.Manifest;
import android.R.layout;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class Recherche extends Fragment {

    ListView listV; // listView
    SearchView searchView;
    CardView cardView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recherche, container, false);


        listMusic = new ArrayList<File>();
        path = new ArrayList<String>();
        audioAlbum = new ArrayList<String>();
        audioArtist = new ArrayList<String>();
        audioTitle = new ArrayList<String>();
        musicArrayList = new ArrayList<Music>();

        cardView = view.findViewById(R.id.cardView);

        // Lit une musique selectionnee dans la listView
        listV = view.findViewById(R.id.listViewSong);
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
                        startActivity(intent);
                        break;
                    }
                }
            }
        });

        listV.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
//               Toast.makeText(getApplicationContext(), "" + absListView., Toast.LENGTH_SHORT).show();
                int firstVisiblePosition = listV.getFirstVisiblePosition();

                // Accédez à la vue du premier élément visible
                View firstVisibleView = listV.getChildAt(0);
                if (firstVisiblePosition == 0 && firstVisibleView != null && firstVisibleView.getTop() == 0) {
                    cardView.setVisibility(View.VISIBLE); // La liste est defilee vers le haut
                } else {
                    cardView.setVisibility(View.GONE); // La liste est défilée vers le bas
                }

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });


        searchView = view.findViewById(R.id.searchView);
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
        /*ArrayAdapter<String>*/ adapter = new ArrayAdapter<>(requireContext(), layout.simple_list_item_1, audioList);
        listV.setAdapter(adapter);

        registerForContextMenu(listV);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.favori) {
            Toast.makeText(getContext(), "Ajouté aux favoris", Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);
    }
}