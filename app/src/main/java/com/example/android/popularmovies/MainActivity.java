package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private MovieAdapter movieAdapter;

    /* Loading indicator displayed before data is retrieved */
    private ProgressBar loadingIndicator;

    private RecyclerView recyclerView;

    private TextView errorMessageDisplay;

    private List<Movie> movieList;

    private final int NUMBER_OF_COLUMNS_IN_GRID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingIndicator = findViewById(R.id.pb_loading_indicator);
        errorMessageDisplay = findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS_IN_GRID);

        recyclerView = findViewById(R.id.recyclerview_movie);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);

        /* If we return to the main activity display movies based on the sorting category last chosen
         * If not, display movies by popularity
         */
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.outStateMovieParcelableKey))) {
            URL popularMoviesQuery = NetworkUtils.buildUrlForPopularMovies();
            loadMovieData(popularMoviesQuery);
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList(getString(R.string.outStateMovieParcelableKey));
            showMovieDataView();
            movieAdapter.setMovieData(movieList);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.outStateMovieParcelableKey), (ArrayList)movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemThatWasSelected = item.getItemId();
        URL moviesQuery = null;

        /* Perform data retrieval based on the sort method selected by the user */
        switch (menuItemThatWasSelected) {
            case R.id.popular: {
                moviesQuery = NetworkUtils.buildUrlForPopularMovies();
                break;
            }
            case R.id.topRated:
                moviesQuery = NetworkUtils.buildUrlForTopRatedMovies();
                break;
        }

        if (null != moviesQuery) {
            loadMovieData(moviesQuery);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Execute the async task with the received URL
     * @param apiCallURL the URL that should be used to retrieve information
     */
    private void loadMovieData(URL apiCallURL){
        new MovieDBQUeryTask().execute(apiCallURL);
    }

    /**
     * On click launch an intent to the MovieDetailActivity containing details about the movie
     * selected
     * @param currentMovie the movie that was selected
     */
    @Override
    public void onClick(Movie currentMovie) {

        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(getString(R.string.intentExtraParcelableKey), currentMovie);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * Async task used to fetch movie data from the Popular Movies DB
     */
    public class MovieDBQUeryTask extends AsyncTask<URL, Void, List<Movie>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorMessageDisplay.setVisibility(View.INVISIBLE);
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(URL... urls) {

            URL targetUrl = urls[0];

            List<Movie> movieListResult;

            try {
                String movieDBResult =  NetworkUtils.getResponseFromHttpUrl(targetUrl);
                movieListResult = JsonUtils.parseMovieDBJson(movieDBResult);

            } catch (IOException | JSONException e) {
                return null;
            }

            return movieListResult;
        }

        @Override
        protected void onPostExecute(List<Movie> movieListResult) {
            loadingIndicator.setVisibility(View.INVISIBLE);

            if (movieListResult != null){
                showMovieDataView();
                movieAdapter.setMovieData(movieListResult);
                movieList = movieListResult;
            }
            else {
                showErrorMessage();
            }
        }
    }

    private void showMovieDataView(){
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

}
