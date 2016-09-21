package com.example.android.popularmoviesapp;

import android.provider.BaseColumns;

/**
 * Created by mohamed tony hammad  on 1-ِSep-16.
 */

//======================>>>> Mohamed Tony Hammad Basha
public final class FavoriteContract {

    //// the constructor ////////////////////////////////////////////
    private FavoriteContract(){}

    public static final class FavouritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "favourite";

        public static final String COLUMN_FAVOURITE_ID = "favourite_id";
    }
}
