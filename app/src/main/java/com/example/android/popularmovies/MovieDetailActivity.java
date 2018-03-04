package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Model.Movie;
import com.squareup.picasso.Picasso;

/**
 * Activity used for displaying movie details
 */

public class MovieDetailActivity extends AppCompatActivity{

    private ImageView moviePoster;
    private TextView movieTitleTextView;
    private TextView movieReleaseDateTextView;
    private TextView movieVoteAverageTextView;
    private TextView movieSynopsisTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        moviePoster = findViewById(R.id.imageview_detail_movie_poster);
        movieTitleTextView = findViewById(R.id.tv_detail_movie_name);
        movieReleaseDateTextView = findViewById(R.id.tv_detail_release_date);
        movieVoteAverageTextView = findViewById(R.id.tv_detail_vote_average);
        movieSynopsisTextView = findViewById(R.id.tv_detail_plot_synopsis);

        /* Get the intent that started the Activity */
        Intent intent = getIntent();
        Movie currentMovie = null;
        if (null != intent){
            currentMovie = intent.getParcelableExtra(getString(R.string.intentExtraParcelableKey));
        }

        /* Update the views with relevant information */
        if (null != currentMovie){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            int height = Resources.getSystem().getDisplayMetrics().heightPixels;
            Picasso.with(this)
                    .load(currentMovie.getPosterPath())
                    .resize(width/2, height/2)
                    .into(moviePoster);
            movieTitleTextView.setText(currentMovie.getTitle());
            movieReleaseDateTextView.setText(currentMovie.getReleaseDate());
            movieVoteAverageTextView.setText(String.valueOf(currentMovie.getVoteAverage()));
            movieSynopsisTextView.setText(currentMovie.getOverview());
        }
    }
}
