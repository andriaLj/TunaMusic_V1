package com.app.tunamusic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBase extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "TunaMusic.db";
    public final static String TABLE_NAME = "TunaMusicTable";
    public final static String ID_COL = "ID";
    public final static String TITLE_COL = "TITLE";
    public final static String ARTIST_COL = "ARTIST";
    public final static String ALBUM_COL  = "ALBUM";
    public final static String PATH_COL = "PATH";
    private static final String INDEX_COL = "INDEX";


    public MyDataBase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // PROBELEME A REGLER : L'insertion des autres colonnes ne fonctionnent pas
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String script = "CREATE TABLE " + TABLE_NAME + "("
                + ID_COL + " INTEGER PRIMARY KEY," + TITLE_COL + " TEXT UNIQUE)";
        sqLiteDatabase.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    // INSERT
    public boolean insert(Music music) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE_COL, music.getTitle());

        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();

        return result != -1;
    }

    // READ
    public Cursor getAllInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("Select * from " + TABLE_NAME, null);
    }

    // DELETE
    public void deleteInfo(Music music) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, TITLE_COL+"=?", new String[]{music.getTitle()});
        db.close();
    }

    public void deleteAllInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

}
