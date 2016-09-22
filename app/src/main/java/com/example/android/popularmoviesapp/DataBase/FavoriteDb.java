package com.example.android.popularmoviesapp.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mohamed tony hammad   on 3-Sep-16.
 */


//======================>>>> Mohamed Tony Hammad Basha
public class FavoriteDb extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 2;
    public FavoriteDb(Context context) {
        super(context,  DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + FavoriteContract.FavouritesEntry.TABLE_NAME + " (" +
                FavoriteContract.FavouritesEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteContract.FavouritesEntry.COLUMN_FAVOURITE_ID + " INTEGER UNIQUE NOT NULL " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavouritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}