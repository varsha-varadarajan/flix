package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.flixster.models.Movie;

import org.parceler.Parcels;
import org.w3c.dom.Text;

public class MovieDetailsActivity extends AppCompatActivity {

    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);

        // unwrap the movie passed in via the intent
        Movie movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        if (movie != null) {
            Log.d(this.getClass().getSimpleName(), String.format("Movie name is '%s", movie.getTitle()));

            // set the movie title and overview
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());

            // vote average is 0..10, convert to 0..5 by dividing by 2
            float averageVote = movie.getAverageVote().floatValue();
            rbVoteAverage.setRating(averageVote > 0 ? averageVote / 2.0f : averageVote);
        }
    }
}