package com.app.tunamusic;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

public class SeekbarParcelable implements Parcelable {

    private SeekBar seekBar;


    public SeekbarParcelable(SeekBar seekBar) {
        this.seekBar = seekBar;
    }

    protected SeekbarParcelable(Parcel in) {
        seekBar = new SeekBar(in.readParcelable(SeekBar.class.getClassLoader()));
    }

    public static final Creator<SeekbarParcelable> CREATOR = new Creator<SeekbarParcelable>() {
        @Override
        public SeekbarParcelable createFromParcel(Parcel in) {
            return new SeekbarParcelable(in);
        }

        @Override
        public SeekbarParcelable[] newArray(int size) {
            return new SeekbarParcelable[size];
        }
    };

    public void setProgressPos(int progressPos) {
        seekBar.setProgress(progressPos);
    }

    public void setMaxPos(int duration) {
        seekBar.setMax(duration);
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(seekBar.onSaveInstanceState(), i);
    }
}
