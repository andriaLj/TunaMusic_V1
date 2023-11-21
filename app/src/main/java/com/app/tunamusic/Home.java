package com.app.tunamusic;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;


public class Home extends Fragment {

    private boolean isPlaying = false;
    private int lastScrollPos = 0;

    TextView textView;
    TextView textAlb1;
    TextView textAlb2;
    TextView textAlb3;
    TextView textAlb4;

    // Traitement de la musique
    SortMusic sortMusic;
    NavigationButtonActivity activity;
    ArrayList<Music> musicArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_home, container, false);

        activity = (NavigationButtonActivity) getActivity();
        musicArrayList = activity.getMusicArrayList();

        Toast.makeText(getContext(), "Lanja"+musicArrayList, Toast.LENGTH_SHORT).show();
//        sortMusic = new SortMusic(musicArrayList);

        textView = view.findViewById(R.id.playlist1_title);
        textAlb1 = view.findViewById(R.id.textAlb1);
//        textAlb1.setText(sortMusic.getSortedArtist(sortMusic.getListArtist()).get(0));





        CardView cardPlaylist2 = view.findViewById(R.id.playlist2);
        cardPlaylist2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyMusicActivity.class);
                startActivity(intent);
            }
        });

        CardView cardPlaylist1 = view.findViewById(R.id.playlist1);
        cardPlaylist1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment nextFrag= new MaPlaylist1();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, nextFrag)
                        .addToBackStack(null)
                        .commit();
            }
        });







        registerForContextMenu(cardPlaylist1); // Option sur les cardView
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,v.getId(), 0, "Renommer");
        menu.add(0,v.getId(), 0, "Supprimer");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Renommer")) {
//            TextView textView = item.findViewById(R.id.playlist1_title);
            textView.setText("Lanja");
        }

        Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
    }

}