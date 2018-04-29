package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

public class MovieDetailActivity extends AppCompatActivity
        {

    private ImageView moviePoster;
    private TextView movieTitleTextView;
    private TextView movieReleaseDateTextView;
    private TextView movieVoteAverageTextView;
    private TextView movieSynopsisTextView;
    private Button addToFavoritesButton;


    private static final int ID_MOVIE_TRAILERS_LOADER = 81;
    private static final int ID_MOVIE_REVIEWS_LOADER = 82;
    private static final String MOVIE_ID_KEY = "movieID";
    private String movieID;


    public static final String[] FAVORITE_MOVIES_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
    };
    public String selection;
    public String[] selectionArgs;

    private boolean movieIsInFavorites;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        moviePoster = findViewById(R.id.imageview_detail_movie_poster);
        movieTitleTextView = findViewById(R.id.tv_detail_movie_name);
        movieReleaseDateTextView = findViewById(R.id.tv_detail_release_date);
        movieVoteAverageTextView = findViewById(R.id.tv_detail_vote_average);
        movieSynopsisTextView = findViewById(R.id.tv_detail_plot_synopsis);
        addToFavoritesButton = findViewById(R.id.b_add_to_favorites);

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
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                width = width/2;
                height = height/2;
            }
            else {
                width = width/3;
                height = height/2;
            }

            Picasso.with(this)
                    .load(currentMovie.getPosterPath())
                    .resize(width, height)
                    .into(moviePoster);
            movieTitleTextView.setText(currentMovie.getTitle());
            movieReleaseDateTextView.setText(currentMovie.getReleaseDate());
            movieVoteAverageTextView.setText(String.valueOf(currentMovie.getVoteAverage()));
            movieSynopsisTextView.setText(currentMovie.getOverview());
            movieID = currentMovie.getId();

            selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
            selectionArgs = new String[]{movieID};
            Cursor cursor = getFavoriteMovieById(selection, selectionArgs);
            if (cursor.getCount() > 0) {
                movieIsInFavorites = true;
                markAsFavorite();
            }
            else {
                unmarkAsFavorite();
            }

            Bundle bundleForLoader = new Bundle();
            bundleForLoader.putString(MOVIE_ID_KEY, movieID);
            getSupportLoaderManager().initLoader(ID_MOVIE_TRAILERS_LOADER, bundleForLoader, trailersLoaderCallback);
            getSupportLoaderManager().initLoader(ID_MOVIE_REVIEWS_LOADER, bundleForLoader, reviewsLoaderCallback);
        }

    }

    private LoaderManager.LoaderCallbacks<List<Trailer>> trailersLoaderCallback = new LoaderManager.LoaderCallbacks<List<Trailer>>(){

        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<Trailer>>(MovieDetailActivity.this) {
                List<Trailer> movieTrailers = null;

                @Override
                protected void onStartLoading() {
                    if (movieTrailers != null) {
                        deliverResult(movieTrailers);
                    }
                    else {
                        forceLoad();
                    }
                }

                @Override
                public List<Trailer> loadInBackground() {

                    URL targetUrl = NetworkUtils.buildUrlForMovieTrailers(movieID);

                    List<Trailer> movieTrailerListResult;

                    try {
                        String movieTrailerDBResult =  NetworkUtils.getResponseFromHttpUrl(targetUrl);
                        movieTrailerListResult = JsonUtils.parseMovieTrailerDBJson(movieTrailerDBResult);

                    } catch (IOException | JSONException e) {
                        return null;
                    }

                    return movieTrailerListResult;
                }

                public void deliverResult(List<Trailer> data) {
                    movieTrailers = data;
                    super.deliverResult(movieTrailers);
                }

            };

        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
            if (data != null){
                createTrailerButtons(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {

        }
    };


    private LoaderManager.LoaderCallbacks<List<Review>> reviewsLoaderCallback = new LoaderManager.LoaderCallbacks<List<Review>>(){

        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<Review>>(MovieDetailActivity.this) {
                List<Review> movieReviews = null;

                @Override
                protected void onStartLoading() {
                    if (movieReviews != null) {
                        deliverResult(movieReviews);
                    }
                    else {
                        forceLoad();
                    }
                }

                @Override
                public List<Review> loadInBackground() {

                    URL targetUrl = NetworkUtils.buildUrlForMovieReviews(movieID);

                    List<Review> movieReviewsListResult;

                    try {
                        String movieReviewsDBResult =  NetworkUtils.getResponseFromHttpUrl(targetUrl);
                        movieReviewsListResult = JsonUtils.parseMovieReviewDBJson(movieReviewsDBResult);

                    } catch (IOException | JSONException e) {
                        return null;
                    }

                    return movieReviewsListResult;
                }

                public void deliverResult(List<Review> data) {
                    movieReviews = data;
                    super.deliverResult(movieReviews);
                }

            };

        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> movieReviewListResult) {
            if (movieReviewListResult != null){
                createReviewTextBoxes(movieReviewListResult);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {

        }
    };


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
    public void updateFavorites(View v){
        if (movieIsInFavorites){
            unmarkAsFavorite();
            removeFavoriteMovie(movieID);
        } else {
            markAsFavorite();
            addNewMovie(movieTitleTextView.getText().toString(), movieID);
        }
    }

    private void removeFavoriteMovie(String movieId) {
        int result = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, selection , selectionArgs);
        if (result > 0){
            Toast.makeText(getBaseContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewMovie(String name, String movieId) {

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, name);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
        if(uri != null) {
            Toast.makeText(getBaseContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private Cursor getFavoriteMovieById(String selection, String[] selectionArgs){
        return getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                FAVORITE_MOVIES_PROJECTION,
                selection,
                selectionArgs,
                null);
    }

    private void markAsFavorite(){
        addToFavoritesButton.setText("Favorite");
        addToFavoritesButton.setBackgroundColor(Color.parseColor("#FFFF00"));
        movieIsInFavorites = true;
    }

    private void unmarkAsFavorite(){
        addToFavoritesButton.setText("Add To Favorites");
        addToFavoritesButton.setBackgroundResource(android.R.drawable.btn_default);
        movieIsInFavorites = false;
    }


}
