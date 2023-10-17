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

        registerForContextMenu(cardPlaylist1);
        // Option sur les cardView


/*        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        ScrollView scrollView = findViewById(R.id.srcreenScroll);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                int offset = 10;
                if((lastScrollPos + offset) > scrollY) {
                    toolbar.setVisibility(View.VISIBLE);
                    lastScrollPos = scrollY;
                } else {
                    toolbar.setVisibility(View.GONE);
                    lastScrollPos = scrollY;
                }
            }
        });
*/



   /*     final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.riche);

        Button btPlay = findViewById(R.i.playMusic);
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying) {
                    mediaPlayer.start();
                }
                isPlaying = true;
            }
        });

        Button btPause = findViewById(R.id.pauseMusic);
        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying) {
                    mediaPlayer.pause();
                }
                isPlaying = false;
            }
        });

*/

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