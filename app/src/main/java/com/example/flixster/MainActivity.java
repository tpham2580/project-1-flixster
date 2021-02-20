package com.example.flixster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";

    List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        movies = new ArrayList<>();
        final int[] pageNumber = {2};

        // Create the adapter
        MovieAdapter movieAdapter = new MovieAdapter(this, movies);

        // Set the adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);

        // Set a Layout Manager on the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        rvMovies.addItemDecoration(new DividerItemDecoration(rvMovies.getContext(), DividerItemDecoration.VERTICAL));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies: " + movies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        rvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    client.get(NOW_PLAYING_URL + "&page=" + pageNumber[0], new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Headers headers, JSON json) {
                            Log.d(TAG, "onSuccess");
                            JSONObject jsonObject = json.jsonObject;
                            try {
                                JSONArray results = jsonObject.getJSONArray("results");
                                Log.i(TAG, "Results: " + results.toString());
                                movies.addAll(Movie.fromJsonArray(results));
                                movieAdapter.notifyDataSetChanged();
                                pageNumber[0]++;
                                Log.i(TAG, "Movies: " + movies.size());
                            } catch (JSONException e) {
                                Log.e(TAG, "Hit json exception", e);
                            }
                        }

                        @Override
                        public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                            Log.d(TAG, "onFailure");
                        }
                    });
            }
        }});

    }
}