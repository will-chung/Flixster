package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.service.autofill.TextValueSanitizer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityDetailBinding;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {

    private String YOUTUBE_API_KEY;
    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String GENRE_URL = "https://api.themoviedb.org/3/genre/movie/list?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US";

    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        YOUTUBE_API_KEY = getString(R.string.youtube_api_key);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

        binding.setMovie(movie);

        binding.tvCount.setText(movie.getRating() + " stars over " + movie.getVoteCount() + " reviews");

        binding.tvRelease.setText("Released on " + movie.getReleaseDate());

        Map<Integer,String> genres = new HashMap<>();

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEO_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if(results.length() == 0)
                        return;
                    String youtubeKey = results.getJSONObject(0).getString("key");
                    Log.d("DetailActivity", youtubeKey);
                    initializeYoutube(youtubeKey, binding.ratingBar.getRating());
                } catch (JSONException e) {
                    Log.e("DetailActivity", "Failed to parse JSON", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d("DetailActivity", "onFailure");
            }
        });

        client.get(GENRE_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d("DetailActivity", "onSuccess");
                try {
                    JSONArray results = json.jsonObject.getJSONArray("genres");
                    JSONObject genre;
                    for(int index = 0; index < results.length(); ++index) {
                        genre = results.getJSONObject(index);
                        genres.put(genre.getInt("id"), genre.getString("name"));
                        Log.d("DetailActivity", "id: " + genre.getInt("id"));
                        Log.d("DetailActivity", "name: " + genre.getString("name"));
                        setGenres(movie, genres);
                    }
                    Log.d("DetailActivity", "CHECK (should be Comedy): " + genres.get(35));
                } catch (JSONException e) {
                    Log.e("DetailActivity", "Failed to parse JSON", e);
                }

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d("DetailActivity", "onFailure");
            }
        });
    }

    private void setGenres(Movie movie, Map<Integer,String> genres) {
        String genreList = "";
        int[] genreIds = movie.getGenreIds();
        for(int i = 0; i < genreIds.length; ++i) {
            if(i != genreIds.length-1)
                genreList += genres.get(genreIds[i]) + ", ";
            else
                genreList += genres.get(genreIds[i]);
        }
        binding.tvGenres.setText("Genres: " + genreList);
    }

    private void initializeYoutube(String youtubeKey, double rating) {
        binding.player.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("DetailActivity", "onInitializationSuccess");
                youTubePlayer.cueVideo(youtubeKey);
                youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {
                        if(rating > 5.0) {
                            Log.d("DetailActivity", "rating: " + rating);
                            youTubePlayer.play();
                        }
                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {

                    }

                    @Override
                    public void onVideoEnded() {

                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {

                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity", "onInitializationFailure");
            }
        });
    }
}