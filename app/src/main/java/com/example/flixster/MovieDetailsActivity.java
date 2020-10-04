package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.flixster.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // unwrap the movie passed in via the intent
        Movie movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        if (movie != null) {
            Log.d(this.getClass().getSimpleName(), String.format("Movie name is '%s", movie.getTitle()));
        }
    }
}