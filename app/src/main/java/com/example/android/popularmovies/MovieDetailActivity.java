package com.example.android.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.Model.Trailer;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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

        URL movieTrailersQuery = NetworkUtils.buildUrlForMovieTrailers(currentMovie.getId());
        loadMovieTrailers(movieTrailersQuery);
    }

    private void loadMovieTrailers(URL apiCallURL){
        new TrailerDBQueryTask().execute(apiCallURL);
    }

    /**
     * Async task used to fetch trailer data from the Popular Movies DB
     */
    public class TrailerDBQueryTask extends AsyncTask<URL, Void, List<Trailer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Trailer> doInBackground(URL... urls) {

            URL targetUrl = urls[0];

            List<Trailer> movieTrailerListResult;

            try {
                String movieTrailerDBResult =  NetworkUtils.getResponseFromHttpUrl(targetUrl);
                movieTrailerListResult = JsonUtils.parseMovieTrailerDBJson(movieTrailerDBResult);

            } catch (IOException | JSONException e) {
                return null;
            }

            return movieTrailerListResult;
        }

        @Override
        protected void onPostExecute(List<Trailer> movieTrailerListResult) {

            if (movieTrailerListResult != null){
                createTrailerButtons(movieTrailerListResult);
            }
        }
    }

    private void createTrailerButtons(List<Trailer> movieTrailerListResult){
        LinearLayout layout = (LinearLayout) findViewById(R.id.ll_trailers);
        layout.setGravity(Gravity.CENTER);
        for (final Trailer trailer: movieTrailerListResult) {
            Button button = new Button(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText("Play Trailer " + trailer.getName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playTrailer(NetworkUtils.buildTrailerUri(trailer.getKey()));
                }
            });
            layout.addView(button);
        }
    }

    private void playTrailer(Uri trailerUri){

        Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
        if (playTrailerIntent.resolveActivity(getPackageManager()) != null){
            startActivity(playTrailerIntent);
        }

    }
}
