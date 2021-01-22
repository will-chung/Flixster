package com.example.flixster.models;

import android.util.Log;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Movie {

    int[] genreIds;
    int movieId;
    int voteCount;
    double rating;
    String backdropPath;
    String posterPath;
    String title;
    String overview;
    String releaseDate;

    // empty constructor needed by the Parceler library
    public Movie() {}

    public Movie(JSONObject jsonObject) throws JSONException {
        initializeGenres(jsonObject);
        movieId = jsonObject.getInt("id");
        voteCount = jsonObject.getInt("vote_count");
        rating = jsonObject.getDouble("vote_average");
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        initializeReleaseDate(jsonObject);
    }

    private void initializeGenres(JSONObject jsonObject) throws JSONException {
        JSONArray genreJSONArray = jsonObject.getJSONArray("genre_ids");
        genreIds = new int[genreJSONArray.length()];
        for(int i = 0; i < genreIds.length; ++i) {
            genreIds[i] = genreJSONArray.getInt(i);
        }
    }

    private void initializeReleaseDate(JSONObject jsonObject) throws JSONException {
        String date = jsonObject.getString("release_date");
        String year = date.substring(0,4);
        String month = date.substring(5,7);
        String day = date.substring(8);
        switch(month) {
            case "1" : month = "January";
            case "2" : month = "February";
            case "3" : month = "March";
            case "4" : month = "April";
            case "5" : month = "May";
            case "6" : month = "June";
            case "7" : month = "July";
            case "8" : month = "August";
            case "9" : month = "September";
            case "10" : month = "October";
            case "11" : month = "November";
            case "12" : month = "December";
        }
        date = month + " " + day + ", " + year;
        releaseDate = date;
    }

    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for(int i = 0; i < movieJsonArray.length(); ++i) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    public int[] getGenreIds() { return genreIds; }

    public int getMovieId() { return movieId; }

    public int getVoteCount() { return voteCount; }

    public double getRating() { return rating; }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public String getTitle() { return title; }

    public String getOverview() { return overview; }

    public String getReleaseDate() { return releaseDate; }
}
