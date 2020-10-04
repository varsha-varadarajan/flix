package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends YouTubeBaseActivity {

    private final String YOUTUBE_API_KEY = "AIzaSyA7KWlrAeWsFcHnAiOBfG06uhObaRw9VK4";
    private final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    private final String TAG = this.getClass().getSimpleName();
    private String youtubeKey = null;

    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    YouTubePlayerView youTubePlayerView;
    ImageView bdImage;
    ImageView playIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.ytPlayer);
        bdImage = (ImageView) findViewById(R.id.bdImage);
        playIcon = (ImageView) findViewById(R.id.playIcon);

        // unwrap the movie passed in via the intent
        final Movie movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        if (movie != null) {
            Log.d(this.getClass().getSimpleName(), String.format("Movie name is '%s", movie.getTitle()));

            // set the movie title and overview
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());

            // vote average is 0..10, convert to 0..5 by dividing by 2
            float averageVote = movie.getAverageVote().floatValue();
            rbVoteAverage.setRating(averageVote > 0 ? averageVote / 2.0f : averageVote);
        }

        // Get Movie Youtube video URL
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(Locale.US, VIDEOS_URL, movie.getId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() == 0) {
                        return;
                    }
                    youtubeKey = results.getJSONObject(0).getString("key");
                    Log.d(TAG,  String.format("YouTube key is %s", youtubeKey));
                    if (movie.getAverageVote() > 6) {
                        initializeYoutube(movie.getAverageVote());
                    } else {
                        initializeImage(movie.getBackdropPath());
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Error in parsing JSON");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onSuccess");
            }
        });
    }

    public void onClickPlayIcon(View view) {
        Log.d(TAG, "On click called");

        if (youtubeKey != null) {
            Intent i = new Intent(this, MovieTrailerActivity.class);
            i.putExtra("Youtube_Key", youtubeKey);
            this.startActivity(i);
        } else {
            Toast toast = Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void initializeImage(String backdropPath) {
        youTubePlayerView.setVisibility(View.GONE);
        Glide.with(this)
                .load(backdropPath)
                .transform(new RoundedCornersTransformation(30, 10))
                .into(bdImage);
    }

    // parameter marked final as String is used inside Listener
    private void initializeYoutube(final Double rating) {
        bdImage.setVisibility(View.GONE);
        playIcon.setVisibility(View.GONE);
        // Youtube player initialization
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "onInitializationSuccess");
                // do any work here to cue video, play video, etc.
                if (rating > 7) {
                    youTubePlayer.loadVideo(youtubeKey);
                } else {
                    youTubePlayer.cueVideo(youtubeKey);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure");
            }
        });
    }
}