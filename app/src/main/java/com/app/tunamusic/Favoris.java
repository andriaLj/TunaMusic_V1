package com.app.tunamusic;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;


public class Favoris extends Fragment {
    ArrayList<Music> musicArrayList;
    NavigationButtonActivity activity;
    CustumAdapter adapter;
    int index;
    ListView listV;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favoris, container, false);

        activity  = (NavigationButtonActivity) getActivity();
        listV = view.findViewById(R.id.listV);

        // alert dialog si appui sur la corbeille
        MaterialToolbar materialToolbar = view.findViewById(R.id.toolbarFavoris);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alerBuilder = new AlertDialog.Builder(getContext());

                // titre
                alerBuilder.setTitle("Alerte");
                // dialog message
                alerBuilder.setMessage("Voulez-vous supprimer tous les favoris ?")
                                .setCancelable(false)
                                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                activity.deleteAllMusicInFavoris();
                                                afficheMusic();
                                            }
                                        }).setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = alerBuilder.create();
                alertDialog.show();
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
        ArrayList<Music> favoris = activity.getFavoriArrayList();
        if (favoris != null) {
            adapter = new CustumAdapter(getContext(), favoris); // ajout des musiques sur un ListView
            listV.setAdapter(adapter);
        }
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
                    Music myMusic = new Music(musicArray.get(i).getTitle(), musicArray.get(i).getArtist(), musicArray.get(i).getAlbum(), musicArray.get(i).getPath(), i, true);
                    Intent intent = new Intent(getContext(), LecteurActivity.class);
                    intent.putExtra("MUSIC", myMusic);
                    intent.putParcelableArrayListExtra("MUSIC_ARRAY", activity.getFavoriArrayList());
                    startActivity(intent);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    index = i;
                    registerForContextMenu(listV);
                    return false;
                }
            });

            return view;
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_delete, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.supprimer) {
            activity.removeMusicTOFavoris(index);
            afficheMusic();
            Toast.makeText(getContext(), "Supprimée", Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);
    }


    // Appui sur la corbeille
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(getContext(), "toto", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}