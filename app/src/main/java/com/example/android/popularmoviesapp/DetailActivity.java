package com.example.android.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//======================>>>> Mohamed Tony Hammad Basha
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/


        /*if (savedInstanceState == null) {
           *//* Intent i = getIntent();
            MyMovie movie=i.getParcelableExtra(" Movie ");
            Bundle bundle = new Bundle();
            bundle.putParcelable("My Movie",movie);
            // set Fragmentclass Arguments
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            detailActivityFragment.setArguments(bundle);*//*
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, new DetailActivityFragment())
                    .commit();
        }*/
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailActivityFragment.ARG_ID, getIntent().getExtras().getParcelable(DetailActivityFragment.ARG_ID));

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

        }
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
