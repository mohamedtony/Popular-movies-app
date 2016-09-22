package com.example.android.popularmoviesapp.Fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmoviesapp.BuildConfig;
import com.example.android.popularmoviesapp.DataBase.FavoriteContract;
import com.example.android.popularmoviesapp.DataBase.FavoriteDb;
import com.example.android.popularmoviesapp.MovieShape.MyMovie;
import com.example.android.popularmoviesapp.R;
import com.example.android.popularmoviesapp.adapter.MyMovieAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 * //======================>>>> Mohamed Tony Hammad Basha
 */
public class MovieActivityFragment extends Fragment {


    private MyMovieAdapter myMovieAdapter;


    public MovieActivityFragment() {
    }

    public interface MovieCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void on_MovieSelected(MyMovie movie);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.main_gridview);
        myMovieAdapter = new MyMovieAdapter(getContext(), new ArrayList<MyMovie>());

        ////////////////// this empty view when no favorite movie swlwctw///////////////
        gridView.setEmptyView(view.findViewById(R.id.empty));

        gridView.setAdapter(myMovieAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyMovie movie = myMovieAdapter.getItem(i);
                /*Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(" Movie ",movie);
                startActivity(intent);*/
                ((MovieCallback) getActivity())
                        .on_MovieSelected(movie);
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular));//R.string.pref_sort_by_most_popular
        if (sort_by.equals("favorite")) {
            ArrayList<String> fav_ids = new ArrayList<String>();
            FavoriteDb fDbHelper = new FavoriteDb(getActivity());
            Cursor retCursor = fDbHelper.getReadableDatabase().query(
                    FavoriteContract.FavouritesEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if (retCursor.moveToFirst()) {
                do {
                    fav_ids.add(retCursor.getString(1));
                    Log.i(" ID ---> ", "favourite movie id= " + retCursor.getString(1));
                } while (retCursor.moveToNext());
            }
            retCursor.close();
            FetchMoviesById movieByIdTask = new FetchMoviesById();
            movieByIdTask.execute(fav_ids);
        } else {
            FetchMovies movieTask = new FetchMovies();
            movieTask.execute(sort_by);
        }
    }


    //////////////////=======> Fetch Movies Task <=============////////////////
    public class FetchMovies extends AsyncTask<String, Void, MyMovie[]> {


        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected MyMovie[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String BASE_URL =
                        "http://api.themoviedb.org/3" + params[0] + "?";
                final String APPID_PARAM = "api_key";
                final String SORT_ORDER = "";  //popular?
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_MOVIE_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + url);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                //////////////////////////////////
                return getDataMovieFroJson(forecastJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private MyMovie[] getDataMovieFroJson(String MovieData) {
            try {
                Log.i("Url result", "---------->" + MovieData);
                JSONObject jsonObject = new JSONObject(MovieData);
                String m = jsonObject.getString("results");
                Log.i("m", "---------->" + m);
                // textView.setText(m);
                JSONArray jsonArray = new JSONArray(m);
                Log.i(" lenght ----->", String.valueOf(jsonArray.length()));

                MyMovie[] movies = new MyMovie[jsonArray.length()];


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject arrObjact = jsonArray.getJSONObject(i);
                    String moviePoster = arrObjact.getString("poster_path");
                    String movieTitle = arrObjact.getString("title");
                    //movie.setMovieTitle( movieTitle[i]);release_date
                    String moviebackdrop = arrObjact.getString("backdrop_path");
                    //movie.setMoviebackdrop(moviebackdrop[i]);
                    String moviePreview = arrObjact.getString("overview");
                    //movie.setMoviePreview(moviePreview[i]);
                    String releaseDate = arrObjact.getString("release_date");
                    String movieId = arrObjact.getString("id");
                    String voteAverage = arrObjact.getString("vote_average");
                    MyMovie movie = (new MyMovie(movieId, moviePoster, moviebackdrop, movieTitle, moviePreview, releaseDate, voteAverage));
                    movies[i] = movie;
                }


                return movies;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(MyMovie[] movies) {
            super.onPostExecute(movies);
            if (movies != null) {
                myMovieAdapter.clear();
                for (MyMovie movie : movies) {
                    myMovieAdapter.add(movie);
                }
            }
        }

    }


    //////////////////=======> Fetch Movies By id Task <=============////////////////
    public class FetchMoviesById extends AsyncTask<ArrayList<String>, Void, ArrayList<MyMovie>> {
        private final String LOG_TAG = FetchMoviesById.class.getSimpleName();

        @Override
        protected ArrayList<MyMovie> doInBackground(ArrayList<String>... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            ArrayList<MyMovie> movies = new ArrayList<MyMovie>();
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            final String BASE_URL =
                    "http://api.themoviedb.org/3/movie/";
            final String APPID_PARAM = "api_key";
            for (String id : params[0]) {
                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast
                    final String SORT_ORDER = "";  //popular?
                    Uri builtUri = Uri.parse(BASE_URL + id + "?").buildUpon()
                            .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_MOVIE_API_KEY)
                            .build();
                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, "Built URI " + url);
                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    movieJsonStr = buffer.toString();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try {
                    //////////////////////////////////
                    MyMovie movie1 = getDataMovieFroJson(movieJsonStr);
                    movies.add(movie1);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                // This will only happen if there was an error getting or parsing the forecast.
            }
            return movies;
        }

        private MyMovie getDataMovieFroJson(String MovieData) {
            // ArrayList<MyMovie> movies = new ArrayList<MyMovie>();
            try {
                Log.i("Url result 2", "---------->" + MovieData);
                JSONObject jsonObject = new JSONObject(MovieData);
                // textView.setText(m);
                //JSONObject arrObjact = jsonArray.getJSONObject(i);
                String moviePoster = jsonObject.getString("poster_path");
                String movieTitle = jsonObject.getString("original_title");
                //movie.setMovieTitle( movieTitle[i]);release_date
                String moviebackdrop = jsonObject.getString("backdrop_path");
                //movie.setMoviebackdrop(moviebackdrop[i]);
                String moviePreview = jsonObject.getString("overview");
                //movie.setMoviePreview(moviePreview[i]);
                String releaseDate = jsonObject.getString("release_date");
                String movieId = jsonObject.getString("id");
                String voteAverage = jsonObject.getString("vote_average");
                MyMovie movie = (new MyMovie(movieId, moviePoster, moviebackdrop, movieTitle, moviePreview, releaseDate, voteAverage));
                return movie;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<MyMovie> movies) {
            //super.onPostExecute(movies);
            if (movies != null) {
                myMovieAdapter.clear();
                for (MyMovie movie : movies) {
                    myMovieAdapter.add(movie);
                }
            }
        }
    }

}
