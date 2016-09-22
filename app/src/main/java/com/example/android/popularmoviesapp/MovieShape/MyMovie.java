package com.example.android.popularmoviesapp.MovieShape;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mphamed tony hamad bsha on 19-Sep-16.
 */


//======================>>>> Mohamed Tony Hammad Basha
public class MyMovie implements Parcelable {
    public String movie_Title;
    public String movie_backdrop;
    public String movie_Poster;
    public String movie_Preview;
    public String release_date;
    public String movie_id;
    public String vote_average;

    public MyMovie() {
    }

    public MyMovie(String id, String moviePoster, String moviebackdrop, String movieTitle, String moviePreview, String releasedate, String voteAverage) {
        movie_Title = movieTitle;
        movie_backdrop = moviebackdrop;
        movie_Poster = moviePoster;
        movie_Preview = moviePreview;
        release_date = releasedate;
        movie_id = id;
        vote_average = voteAverage;
    }

    protected MyMovie(Parcel in) {
        movie_Title = in.readString();
        movie_backdrop = in.readString();
        movie_Poster = in.readString();
        movie_Preview = in.readString();
        release_date = in.readString();
        movie_id = in.readString();
        vote_average = in.readString();
    }

    public static final Creator<MyMovie> CREATOR = new Creator<MyMovie>() {
        @Override
        public MyMovie createFromParcel(Parcel in) {
            return new MyMovie(in);
        }

        @Override
        public MyMovie[] newArray(int size) {
            return new MyMovie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return movie_id + "--" + movie_Poster + "--" + movie_Title + "--" + release_date + "--" + vote_average + "--" + movie_Preview;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movie_Title);
        parcel.writeString(movie_backdrop);
        parcel.writeString(movie_Poster);
        parcel.writeString(movie_Preview);
        parcel.writeString(release_date);
        parcel.writeString(movie_id);
        parcel.writeString(vote_average);
    }
}