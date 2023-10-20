package com.app.tunamusic;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Music implements Parcelable {

    // ATTRIBUT
    private String title;
    private String artist;
    private String album;
    private String path;
    private int index; // indice de la musique dansl la liste

    // permet de caster l'objet Music (passage d'une activity a une autre avec putExtra
    protected Music(Parcel in) {
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        path = in.readString();
        index = in.readInt();
    }


    // permet de caster l'objet Music (passage d'une activity a une autre avec putExtra
    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    // CONSTRUCTEUR
    public Music(String audioTitle, String audioArtist, String audioAlbum, String path, int index) {
        title = audioTitle;
        artist = audioArtist;
        album = audioAlbum;
        this.path = path;
        this.index = index;
    }

    // GETTERS
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getPath() {
        return path;
    }

    public int getIndex() {
        return index;
    }



    // SETTERS
    public void setIndex(int index) {
        this.index = index;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    // methode pour caster l'objet
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(album);
        parcel.writeString(path);
        parcel.writeInt(index);
    }
}
