package com.example.android.popularmoviesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by medo on 19-Sep-16.
 */


//======================>>>> Mohamed Tony Hammad Basha
public class MyMovieAdapter extends ArrayAdapter<MyMovie> {
    private LayoutInflater inflater;
    public MyMovieAdapter(Context context, ArrayList<MyMovie> myMovies) {
        super(context,0,myMovies);
        inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyMovie myyymovie=getItem(position);
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.one_movie_item, parent, false);
        }
        ImageView imageView=(ImageView) convertView.findViewById(R.id.grid_image);
        Picasso
                .with(getContext())
                .load("http://image.tmdb.org/t/p/w342/"+myyymovie.movie_Poster)
                .fit()
                .into(imageView);
        Log.i(" -----<> ",myyymovie.movie_Poster);
        return convertView;
    }
}