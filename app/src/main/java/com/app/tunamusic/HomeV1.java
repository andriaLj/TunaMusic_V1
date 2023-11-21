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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class HomeV1 extends Fragment {

    ListView listV;
    ArrayList<Music> musicArrayList;
    NavigationButtonActivity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_v1, container, false);

        activity  = (NavigationButtonActivity) getActivity();
        listV = view.findViewById(R.id.listVArtist);

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
        musicArrayList = activity.getMusicArrayList();
        CustumAdapter adapter = new CustumAdapter(getContext(), activity.getMusicArrayList()); // ajout des musiques sur un ListView
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

            TextView txtArtistAlb = view.findViewById(R.id.txtTitleForArtist);
            txtArtistAlb.setText("Artiste : " + musicArray.get(i).getArtist() + " | Album : " + musicArray.get(i).getAlbum());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Music myMusic = new Music(musicArrayList.get(i).getTitle(), musicArrayList.get(i).getArtist(), musicArrayList.get(i).getAlbum(), musicArrayList.get(i).getPath(), i);

                    Intent intent = new Intent(getContext(), LecteurActivity.class);
                    intent.putExtra("MUSIC", myMusic);
                    intent.putParcelableArrayListExtra("MUSIC_ARRAY", musicArrayList);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}