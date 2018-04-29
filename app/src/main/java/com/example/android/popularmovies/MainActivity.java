package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler{

    private MovieAdapter movieAdapter;

    private static final int ID_MOVIES_QUERY_LOADER = 74;
    private static final int ID_FAVORITE_MOVIES_LOADER = 75;

    public static final String[] FAVORITE_MOVIES_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
    };

    private final static int INDEX_MOVIE_ID = 1;

    private final static String QUERY_URL_KEY = "uriForQuery";

    /* Loading indicator displayed before data is retrieved */
    private ProgressBar loadingIndicator;

    private RecyclerView recyclerView;

    private TextView errorMessageDisplay;

    private List<Movie> movieList;

    private final int NUMBER_OF_COLUMNS_IN_GRID = 2;

    private SQLiteDatabase mDb;

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
            Bundle bundleForLoader = new Bundle();
            bundleForLoader.putString(QUERY_URL_KEY, popularMoviesQuery.toString());
            getSupportLoaderManager().initLoader(ID_MOVIES_QUERY_LOADER, bundleForLoader, moviesLoaderCallback);
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

        /* Perform data retrieval based on the sort method selected by the user */
        switch (menuItemThatWasSelected) {
            case R.id.popular: {
                URL moviesQuery = NetworkUtils.buildUrlForPopularMovies();
                Bundle bundleForLoader = new Bundle();
                bundleForLoader.putString(QUERY_URL_KEY, moviesQuery.toString());
                getSupportLoaderManager().restartLoader(ID_MOVIES_QUERY_LOADER, bundleForLoader, moviesLoaderCallback);
                break;
            }
            case R.id.topRated:
                URL moviesQuery = NetworkUtils.buildUrlForTopRatedMovies();
                Bundle bundleForLoader = new Bundle();
                bundleForLoader.putString(QUERY_URL_KEY, moviesQuery.toString());
                getSupportLoaderManager().restartLoader(ID_MOVIES_QUERY_LOADER, bundleForLoader, moviesLoaderCallback);
                break;
            case R.id.favorites:
                getSupportLoaderManager().initLoader(ID_FAVORITE_MOVIES_LOADER, null, favoriteMoviesLoaderCallback);
        }

        return super.onOptionsItemSelected(item);
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

    private LoaderManager.LoaderCallbacks<List<Movie>> moviesLoaderCallback = new LoaderManager.LoaderCallbacks<List<Movie>>() {
        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<Movie>>(MainActivity.this) {
                List<Movie> movieData = null;
                URL targetUrl = null;

                @Override
                protected void onStartLoading() {

                    try {
                        targetUrl = new URL(args.get(QUERY_URL_KEY).toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (movieData != null) {
                        deliverResult(movieData);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public List<Movie> loadInBackground() {

                    List<Movie> movieListResult;

                    try {
                        String movieDBResult = NetworkUtils.getResponseFromHttpUrl(targetUrl);
                        movieListResult = JsonUtils.parseMovieDBJson(movieDBResult);

                    } catch (IOException | JSONException e) {
                        return null;
                    }

                    return movieListResult;
                }

                public void deliverResult(List<Movie> data) {
                    movieData = data;
                    super.deliverResult(movieData);
                }

            };
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieListResult) {
            loadingIndicator.setVisibility(View.INVISIBLE);

            if (movieListResult != null) {
                showMovieDataView();
                movieAdapter.setMovieData(movieListResult);
                movieList = movieListResult;
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<List<Movie>> favoriteMoviesLoaderCallback = new LoaderManager.LoaderCallbacks<List<Movie>>() {
        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<Movie>>(MainActivity.this) {
                List<Movie> movieData = null;

                @Override
                protected void onStartLoading() {

                    if (movieData != null) {
                        deliverResult(movieData);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public List<Movie> loadInBackground() {

                    List<Movie> movieListResult = new ArrayList<>();
                    Cursor favoriteMoviesCursor = getFavoriteMovies();
                    try {
                        while (favoriteMoviesCursor.moveToNext()) {
                            String movieId = favoriteMoviesCursor.getString(INDEX_MOVIE_ID);

                            try {
                                String movieDBResult = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrlForMoviesDetail(movieId));
                                movieListResult.add(JsonUtils.parseSingleMovieDBJson(movieDBResult));

                            } catch (IOException | JSONException e) {
                                return null;
                            }
                        }
                    } finally {
                        favoriteMoviesCursor.close();
                    }
                    return movieListResult;
                }

                public void deliverResult(List<Movie> data) {
                    movieData = data;
                    super.deliverResult(movieData);
                }

            };

        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieListResult) {
            loadingIndicator.setVisibility(View.INVISIBLE);

            if (movieListResult != null) {
                showMovieDataView();
                movieAdapter.setMovieData(movieListResult);
                movieList = movieListResult;
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {

        }
    };

    private void showMovieDataView(){
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private Cursor getFavoriteMovies(){
        return getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                FAVORITE_MOVIES_PROJECTION,
                null,
                null,
                null);
    }

}
