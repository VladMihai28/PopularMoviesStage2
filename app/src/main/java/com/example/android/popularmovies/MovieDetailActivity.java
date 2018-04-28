package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.Model.Review;
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

    private String movieID;

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
            movieID = currentMovie.getId();
        }

        URL movieTrailersQuery = NetworkUtils.buildUrlForMovieTrailers(movieID);
        loadMovieTrailers(movieTrailersQuery);
        URL movieReviewsQuery = NetworkUtils.buildUrlForMovieReviews(movieID);
        loadMovieReviews(movieReviewsQuery);
    }

    private void loadMovieTrailers(URL apiCallURL){
        new TrailerDBQueryTask().execute(apiCallURL);
    }

    private void loadMovieReviews(URL apiCallURL){
        new ReviewDBQueryTask().execute(apiCallURL);
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
        LinearLayout layout = findViewById(R.id.ll_trailers);
        int index = 1;
        for (final Trailer trailer: movieTrailerListResult) {
            Button button = new Button(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setText("Play Trailer " + Integer.toString(index));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playTrailer(NetworkUtils.buildTrailerUri(trailer.getKey()));
                }
            });
            layout.addView(button);
            index++;
        }
    }

    private void playTrailer(Uri trailerUri){

        Intent playTrailerIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
        if (playTrailerIntent.resolveActivity(getPackageManager()) != null){
            startActivity(playTrailerIntent);
        }

    }


    /**
     * Async task used to fetch trailer data from the Popular Movies DB
     */
    public class ReviewDBQueryTask extends AsyncTask<URL, Void, List<Review>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Review> doInBackground(URL... urls) {

            URL targetUrl = urls[0];

            List<Review> movieReviewsListResult;

            try {
                String movieReviewsDBResult =  NetworkUtils.getResponseFromHttpUrl(targetUrl);
                movieReviewsListResult = JsonUtils.parseMovieReviewDBJson(movieReviewsDBResult);

            } catch (IOException | JSONException e) {
                return null;
            }

            return movieReviewsListResult;
        }

        @Override
        protected void onPostExecute(List<Review> movieReviewListResult) {

            if (movieReviewListResult != null){
                createReviewTextBoxes(movieReviewListResult);
            }
        }
    }

    private void createReviewTextBoxes(List<Review> movieReviewListResult){
        LinearLayout layout = findViewById(R.id.ll_reviews);
        boolean passedFirstReview = false;
        for (Review review: movieReviewListResult) {
            if (passedFirstReview) {
                View viewDivider = new View(this);
                int dividerHeight = (int) getResources().getDisplayMetrics().density * 1;
                viewDivider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dividerHeight));
                viewDivider.setBackgroundColor(Color.parseColor("#000000"));
                layout.addView(viewDivider);
            }

            TextView reviewContent = new TextView(this);
            reviewContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            reviewContent.setText(review.getContent());
            reviewContent.setPadding(0,16,0,16);
            layout.addView(reviewContent);
            passedFirstReview = true;

        }

    }

    /**
     * Favorite a movie
     */
    public void addToFavorites(View v){
        addNewMovie(movieTitleTextView.getText().toString(), movieID);
    }

    private void addNewMovie(String name, String movieId) {

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, name);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
