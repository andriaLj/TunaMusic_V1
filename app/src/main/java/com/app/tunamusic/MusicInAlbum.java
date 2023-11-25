package com.app.tunamusic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class MusicInAlbum extends Fragment {

    ListView listV;
    MaterialToolbar toolbar;
    String album;
    ArrayList<Music> musicArrayList;
    ArrayList<Music> musicInAlbum;
    NavigationButtonActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_in_album, container, false);
        activity  = (NavigationButtonActivity) getActivity();
        listV = view.findViewById(R.id.listV);

        // recuperation info album depuis album.java
        musicArrayList = getArguments().getParcelableArrayList("MUSIC_ARRAY");
        album = getArguments().getString("ALBUM").toLowerCase();

        toolbar = view.findViewById(R.id.toolbarAlbum);
        toolbar.setTitle(album.toUpperCase());

        musicInAlbum = new ArrayList<Music>();
        int index = 0;
        for (int i = 0; i < musicArrayList.size(); ++i) {
            if (album.equals(musicArrayList.get(i).getAlbum().toLowerCase())) {
                musicArrayList.get(i).setIndex(index++);
                musicInAlbum.add(musicArrayList.get(i));
            }
        }

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
        MusicInAlbum.CustumAdapter adapter = new MusicInAlbum.CustumAdapter(getContext(), musicInAlbum); // ajout des musiques sur un ListView
        listV.setAdapter(adapter);
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

            TextView txtArtist = view.findViewById(R.id.txtTitleForArtist);
            txtArtist.setText(musicArray.get(i).getArtist());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Music myMusic = new Music(musicArray.get(i).getTitle(), musicArray.get(i).getArtist(), musicArray.get(i).getAlbum(), musicArray.get(i).getPath(), i, false);
                    Intent intent = new Intent(getContext(), LecteurActivity.class);
                    intent.putExtra("MUSIC", myMusic);
                    intent.putParcelableArrayListExtra("MUSIC_ARRAY", musicInAlbum);
                    startActivity(intent);
                }
            });
            return view;
        }
    }

}