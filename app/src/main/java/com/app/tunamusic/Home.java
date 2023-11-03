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


public class Home extends Fragment {

    private boolean isPlaying = false;
    private int lastScrollPos = 0;

    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_home, container, false);
        textView = view.findViewById(R.id.playlist1_title);

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