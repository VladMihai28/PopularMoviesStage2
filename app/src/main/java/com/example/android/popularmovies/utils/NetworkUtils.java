package com.example.android.popularmovies.utils;

import android.net.Uri;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */

public class NetworkUtils {

    /* Strings to be used as components to form the desired Popular Movies DB URL */
    final static String POPULAR_MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/";

    final static String YOUTUBE_BASE_URL = "https://youtu.be/";

    final static String PARAM_POPULAR = "movie/popular";

    final static String PARAM_TOP_RATED = "movie/top_rated";

    final static String PARAM_TRAILERS = "videos";
    final static String PARAM_REVIEWS = "reviews";
    final static String PARAM_MOVIE = "movie";

    final static String API_KEY = "api_key";
    final static String API_KEY_VALUE = BuildConfig.MOVIE_DB_API_KEY;

    final static String SCANNER_DELIMITER = "\\A";


    /**
     * Builds the URL used to query the popular movies.
     * @return The URL to use to query the popular movies.
     */
    public static URL buildUrlForPopularMovies(){
        return buildUrlForMovies(PARAM_POPULAR);
        }

    /**
     * Builds the URL used to query the top rated movies.
     * @return The URL to use to query the top rated movies.
     */

    public static URL buildUrlForTopRatedMovies(){
        return buildUrlForMovies(PARAM_TOP_RATED);
    }

    /**
     * Build a URL for the Popular Movie DB API based on the sort parameter
     * @param sortParam The URL component that defines how the results should be sorted
     */
    public static URL buildUrlForMovies(String sortParam){

        URL url = null;

        if (null != sortParam) {
            Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL + sortParam).buildUpon()
                    .appendQueryParameter(API_KEY, API_KEY_VALUE)
                    .build();

            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * Build URL for requesting trailer information
     * @param id the id of the movie we are requesting trailers for
     * @return the URL to use to query for trailers
     */
    public static URL buildUrlForMovieTrailers(String id){
        URL url = null;
        Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL + PARAM_MOVIE + "/" + id + "/" + PARAM_TRAILERS).buildUpon()
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    /**
     * Build URL for requesting information from a single movie
     * @param id the id of the movie we are requesting information for
     * @return the URL to use to query for the movie
     */
    public static URL buildUrlForMoviesDetail(String id){
        URL url = null;
        Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL + PARAM_MOVIE + "/" + id).buildUpon()
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    /**
     * Build URL for requesting review information
     * @param id the id of the movie we are requesting reviews for
     * @return the URL to use to query for reviews
     */
    public static URL buildUrlForMovieReviews(String id){
        URL url = null;
        Uri builtUri = Uri.parse(POPULAR_MOVIES_BASE_URL + PARAM_MOVIE + "/" + id + "/" + PARAM_REVIEWS).buildUpon()
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    /**
     * Create a Youtube URI based on the key of the video
     * @param key the key of the video
     * @return the URI to Youtube
     */
    public static Uri buildTrailerUri(String key){

        return Uri.parse(YOUTUBE_BASE_URL + key).buildUpon().build();

    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter(SCANNER_DELIMITER);

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
