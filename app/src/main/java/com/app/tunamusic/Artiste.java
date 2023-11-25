package com.app.tunamusic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Artiste extends Fragment {

    ListView listVArtist;
    ArrayList<Music> musicArrayList;
    NavigationButtonActivity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artiste, container, false);
        activity  = (NavigationButtonActivity) getActivity();

        listVArtist = view.findViewById(R.id.listVArtist);

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
        renvoi la liste des musiques triees par ordre alphabetique par artiste
    */
    ArrayList<Music> getMusicArtist(ArrayList<Music> list) {
        HashMap<String, ArrayList<Music>> map = new HashMap<>();

        int size = list.size();
        for (int i = 0; i < size; ++i) {
            Music music = list.get(i);
            String artistLowerCase = music.getArtist().toLowerCase();
            if (map.containsKey(artistLowerCase)) {
                ArrayList<Music> musicArray = map.get(artistLowerCase);
                if (musicArray == null) {
                    musicArray = new ArrayList<Music>();
                }
                musicArray.add(music);
                map.put(artistLowerCase, musicArray);
            } else {
                ArrayList<Music> listMusic = new ArrayList<>();
                listMusic.add(music);
                map.put(artistLowerCase, listMusic);
            }
        }


        Set<String> keys = map.keySet();

        ArrayList<String> sortedKeys = new ArrayList<>(keys);
        Collections.sort(sortedKeys);

        ArrayList<Music> ret = new ArrayList<>();
        for (String item : sortedKeys) {
            ArrayList<Music> sortedValue = map.get(item);
            if (sortedValue != null) {
                Collections.sort(sortedValue, new Comparator<Music>() {
                    @Override
                    public int compare(Music music, Music t1) {
                        return music.getTitle().compareToIgnoreCase(t1.getTitle());
                    }
                });
            }
            ret.addAll(sortedValue);
        }
        return ret;
    }

    /*
     Liste les musiques dans une listView
  */
    void afficheMusic() {
        musicArrayList = activity.getMusicArrayList();
        Artiste.CustumAdapter adapter = new Artiste.CustumAdapter(getContext(), getMusicArtist(musicArrayList)); // ajout des musiques sur un ListView
        listVArtist.setAdapter(adapter);
    }


    public class CustumAdapter extends BaseAdapter {
        Context context;
        ArrayList<Music> musicArray;

        public CustumAdapter(Context context, ArrayList<Music> musicArray) {
            this.context = context;
            this.musicArray = musicArray;
        }

        @Override
        public int getCount() {
            return musicArray.size();
        }

        @Override
        public Object getItem(int i) {
            return musicArray.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.list_item, null);

            TextView txtTitle = view.findViewById(R.id.txtNameArtist);
            txtTitle.setText(musicArray.get(i).getTitle());

            TextView txtArtistAlb = view.findViewById(R.id.txtTitleForArtist);
            txtArtistAlb.setText(musicArray.get(i).getArtist());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Music> arr = getMusicArtist(musicArrayList);
                    Music myMusic = new Music(arr.get(i).getTitle(), arr.get(i).getArtist(), arr.get(i).getAlbum(), arr.get(i).getPath(), i, false);
                    Intent intent = new Intent(getContext(), LecteurActivity.class);
                    intent.putExtra("MUSIC", myMusic);
                    intent.putParcelableArrayListExtra("MUSIC_ARRAY", arr);
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}