package com.example.android.popularmoviesapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mohamed tony hammad   on 19-Sep-16.
 */


//======================>>>> Mohamed Tony Hammad Basha
public class Favorite_DB extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 2;
    public Favorite_DB(Context context) {
        super(context,  DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + Favorite_Contract.FavouritesEntry.TABLE_NAME + " (" +
                Favorite_Contract.FavouritesEntry._ID + " INTEGER PRIMARY KEY," +
                Favorite_Contract.FavouritesEntry.COLUMN_FAVOURITE_ID + " INTEGER UNIQUE NOT NULL " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Favorite_Contract.FavouritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}