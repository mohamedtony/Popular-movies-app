package com.example.android.popularmoviesapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.Objects;

/**
 * A placeholder fragment containing a simple view.
 *
 * ======================>>>> Mohamed Tony Hammad Basha
 */
public class DetailActivityFragment extends Fragment {

    ImageView star_favorite;
    public static final String ARG_ID = "movie";
    public String THE_FIRST_TRAILER = "";
    ArrayList<String> myTrailerList = new ArrayList<>();
    ArrayList<String> myReviewList = new ArrayList<>();
    MyMovie movie;



    /////////////////////////// constructor //////////////
    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);


//// =============> getting  movie from movie acitvity fragment <==================== /////
        try {
            Bundle arguments = getArguments();
            movie = (MyMovie) arguments.getParcelable(DetailActivityFragment.ARG_ID);

        } catch (Exception e) {
            Intent intent = getActivity().getIntent();
            movie = (MyMovie) intent.getParcelableExtra(DetailActivityFragment.ARG_ID);
        }


        if (movie != null) {
            //----------------------------------------------------------------------------
            ImageView imageView = (ImageView) view.findViewById(R.id.detail_image);
            TextView text_title = (TextView) view.findViewById(R.id.text_title);
            TextView text_overview = (TextView) view.findViewById(R.id.text_overview);
            TextView release_date = (TextView) view.findViewById(R.id.release_date);
            TextView vote_average = (TextView) view.findViewById(R.id.text_voteaverage);
            //-----------------------------------------------------------------------------
            Log.i(" movietitle ---- ?>>>",movie.movie_Title);
            text_title.setText(movie.movie_Title);
            text_overview.setText(movie.movie_Preview);
            release_date.setText(movie.release_date.substring(0, 4));
            // vote_average.setText(String.format("%.1f",Double.parseDouble(movie.vote_average)));
            vote_average.setText((Float.toString((float) (Math.round(Float.parseFloat(movie.vote_average) * 10.0) / 10.0))) + "/10");
            //-------------------------------------------------------------------------------------------------------------------------------


            Picasso.with(getContext())
                    .load("http://image.tmdb.org/t/p/w342/" + movie.movie_Poster).fit().into(imageView);
            TrailerTask trailerTask = new TrailerTask();
            trailerTask.execute(movie.movie_id);
            ReviewTask reviewTask = new ReviewTask();
            reviewTask.execute(movie.movie_id);


            //--------------------------------------- when the user clik on the star image the movie id is sadved-------------------------------------------------------------
            star_favorite = (ImageView) view.findViewById(R.id.star_image);
            star_favorite.setImageResource(R.drawable.star);
            star_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Favorite_DB fDbHelper = new Favorite_DB(getActivity().getApplicationContext());
                    final SQLiteDatabase db = fDbHelper.getWritableDatabase();
                    String[] tableColumns = new String[]{Favorite_Contract.FavouritesEntry.COLUMN_FAVOURITE_ID};
                    String whereClause = Favorite_Contract.FavouritesEntry.COLUMN_FAVOURITE_ID + " = ?";
                    String[] whereArgs = new String[]{movie.movie_id};
                    Cursor c = fDbHelper.getReadableDatabase().query(
                            Favorite_Contract.FavouritesEntry.TABLE_NAME,
                            tableColumns,
                            whereClause,
                            whereArgs,
                            null,
                            null,
                            null
                    );
                    if (c.moveToFirst()) {
                        Snackbar.make(view, movie.movie_Title + " removed from Favourites!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        whereClause = Favorite_Contract.FavouritesEntry.COLUMN_FAVOURITE_ID + " = ?";
                        whereArgs = new String[]{movie.movie_id};
                        db.delete(Favorite_Contract.FavouritesEntry.TABLE_NAME, whereClause, whereArgs);
                        star_favorite.setImageResource(R.drawable.star);
                    } else {
                        Snackbar.make(view, movie.movie_Title + " added to Favourites!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        ContentValues favValues = new ContentValues();
                        favValues.put(Favorite_Contract.FavouritesEntry.COLUMN_FAVOURITE_ID, movie.movie_id);
                        long _id = db.insert(Favorite_Contract.FavouritesEntry.TABLE_NAME, null, favValues);
                        star_favorite.setImageResource(R.drawable.fav_star);
                    }
                    c.close();
                    db.close();
                }
            });
        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /////// ==============> to share the first trailer <==============/////////////

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragmenu, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        } else {
            Log.d(" share ", "Share Action Provider is null?");
        }
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //shareIntent.addFlags(Intent.);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "https://www.youtube.com/watch?v=" + THE_FIRST_TRAILER);
        return shareIntent;
    }


    /////////////////////  =======> Fetch Trailer Task <=========  ///////////////////////////////////
    public class TrailerTask extends AsyncTask<String, Void, ArrayList<String>> {
        private final String LOG_TAG = TrailerTask.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
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
                        "http://api.themoviedb.org/3/movie/" + params[0] + "/videos" + "?";
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
                return getData(forecastJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @SuppressLint("NewApi")
        private ArrayList<String> getData(String fore) {
            try {
                Log.i("Url result", "---------->" + fore);
                JSONObject jsonObject = new JSONObject(fore);
                String m = jsonObject.getString("results");
                Log.i("m", "---------->" + m);
                // textView.setText(m);
                JSONArray jsonArray = new JSONArray(m);
                Log.i(" lenght ----->", String.valueOf(jsonArray.length()));
                String mykey = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject arrObjact = jsonArray.getJSONObject(i);
                    if (Objects.equals(arrObjact.getString("type"), "Trailer")) {
                        mykey = arrObjact.getString("key");
                        myTrailerList.add(mykey);
                    }
                }
                return myTrailerList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<String> trailers) {
            super.onPostExecute(trailers);

            if (trailers != null) {
                int numOfTrailer = 0;


                /// intialize an linear layout //////////////////////////////////////////
                LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.Linear_Layout);
                for (final String trailer : trailers) {
                    if (trailer != null) {
                        LinearLayout layout2 = new LinearLayout(getContext());
                        layout2.setOrientation(LinearLayout.HORIZONTAL);
                        TextView mynumTrailer = new TextView(getContext());
                        ImageView imageView = new ImageView(getContext());
                        imageView.setPadding(55, 0, 2, 0);
                        imageView.setImageResource(R.drawable.play_icon);//@android:drawable/ic_media_play
                        mynumTrailer.setText(" Trailer " + (++numOfTrailer));


                        ///// adding trailer at run time  (image and text )////////////
                        layout2.addView(imageView);
                        layout2.addView(mynumTrailer);
                        layout2.setPadding(30, 20, 0, 20);
                        mynumTrailer.setPadding(30, 40, 0, 0);
                        mynumTrailer.setTextSize(20);
                        mynumTrailer.setTextColor(Color.BLACK);
                        layout.addView(layout2);
                        //layout.setPadding(30, 40, 0, 0);
                        mynumTrailer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + trailer));
                                startActivity(browserIntent);
                            }
                        });
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + trailer));
                                startActivity(browserIntent);
                            }
                        });
                        if (numOfTrailer == 1) {
                            THE_FIRST_TRAILER = trailer;
                        }
                    }
                }
            }
        }
    }




    /////////////////////  =======> Fetch Trailer Task <=========  ///////////////////////////////////
    public class ReviewTask extends AsyncTask<String, Void, ArrayList<String>> {
        private final String LOG_TAG = TrailerTask.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
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
                        "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews" + "?";
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
                return getData(forecastJsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @SuppressLint("NewApi")
        private ArrayList<String> getData(String fore) {
            try {
                Log.i("Url result", "---------->" + fore);
                JSONObject jsonObject = new JSONObject(fore);
                String m = jsonObject.getString("results");
                Log.i("m", "---------->" + m);
                // textView.setText(m);
                JSONArray jsonArray = new JSONArray(m);
                Log.i(" lenght ----->", String.valueOf(jsonArray.length()));
                String auther = "";
                String review = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject arrObjact = jsonArray.getJSONObject(i);
                    auther = arrObjact.getString("author");
                    review = arrObjact.getString("content");
                    myReviewList.add(auther + "\n" + review + "\n\n");
                }
                return myReviewList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<String> reviews) {
            super.onPostExecute(reviews);

            if (reviews != null) {

                LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.Linear_Layout);
                LinearLayout layout3 = new LinearLayout(getContext());
                layout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layout3.setOrientation(LinearLayout.VERTICAL);
                ImageView imageView = new ImageView(getContext());


                // LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200,5);
                // layout.setLayoutParams(layoutParams);
                //  imageView.setLayoutParams(layoutParams);
                //imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));


                /////////// adding image at run time /////////////
                imageView.setPadding(20, 10, 20, 0);
                imageView.setImageResource(R.drawable.line_icon);
                // imageView.setMaxWidth(100);
                layout3.addView(imageView);


                //////////////////// adding reviews text  at run time ////////////
                TextView textView = new TextView(getActivity());
                textView.setText(" Reviews : ");
                textView.setTextSize(35);
                textView.setTextColor(Color.BLACK);
                textView.setPadding(20, 10, 0, 40);
                layout3.addView(textView);
                layout.addView(layout3);


                for (final String review : reviews) {
                    LinearLayout layout2 = new LinearLayout(getContext());
                    layout2.setOrientation(LinearLayout.VERTICAL);
                    layout2.setPadding(30,0,15,0);
                    TextView mytextreview = new TextView(getContext());
                    mytextreview.setText(review);
                    mytextreview.setTextSize(22);
                    mytextreview.setPadding(30,40,0,0);
                    layout2.addView(mytextreview);
                    layout.addView(layout2);
                }
            }
        }
    }
}
//}
