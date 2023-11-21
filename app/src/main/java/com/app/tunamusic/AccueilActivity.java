package com.app.tunamusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AccueilActivity extends AppCompatActivity {

    private boolean isPlaying = false;
    private int lastScrollPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);


        CardView cardPlaylist2 = findViewById(R.id.playlist2);
        cardPlaylist2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyMusicActivity.class);
                startActivity(intent);
            }
        });

        CardView cardPlaylist1 = findViewById(R.id.playlist1);
        cardPlaylist1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
                startActivity(intent);
            }
        });

        registerForContextMenu(cardPlaylist1); // Option sur les cardView

        BottomNavigationView bottomNavi = findViewById(R.id.bottomNavigationView2);
        bottomNavi.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                if (item.getItemId() == R.id.accueil) {
                    intent = new Intent(getApplicationContext(), AccueilActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.recherche) {
                    intent = new Intent(getApplicationContext(), MyMusicActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
//                } else if (item.getItemId() == R.id.menu) {
////                    intent = new Intent(getApplicationContext(), PlaylistActivity.class);
////                    startActivity(intent);
//                }

                return false;
            }
        });
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
            TextView textView = findViewById(R.id.playlist1_title);
            textView.setText("Lanja");
        }

        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
    }




}