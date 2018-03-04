package com.example.android.popularmovies.utils;

import com.example.android.popularmovies.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to parse JSON information
 */

public class JsonUtils {

    public static List<Movie> parseMovieDBJson(String json) throws JSONException {

        /* The name of the relevant fields from the JSON */
        final String RESULTS = "results";
        final String VOTE_AVERAGE = "vote_average";
        final String TITLE = "title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";

        final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w185";

        /* Parse JSON */

        JSONObject moviePageJson = new JSONObject(json);

        List<Movie> movieList = new ArrayList<>();

        if (moviePageJson.has(RESULTS)){
            JSONArray moviesInJson = moviePageJson.getJSONArray(RESULTS);
            for (int i = 0; i< moviesInJson.length(); i++){

                JSONObject currentMovieJson = moviesInJson.getJSONObject(i);
                Movie currentMovie = new Movie();

                if (currentMovieJson.has(VOTE_AVERAGE)) {
                    currentMovie.setVoteAverage(Float.parseFloat(currentMovieJson.getString(VOTE_AVERAGE)));
                }
                if (currentMovieJson.has(TITLE)) {
                    currentMovie.setTitle(currentMovieJson.getString(TITLE));
                }
                if (currentMovieJson.has(POSTER_PATH)) {
                    String posterPath = BASE_POSTER_PATH + currentMovieJson.getString(POSTER_PATH);
                    currentMovie.setPosterPath(posterPath);
                }
                if (currentMovieJson.has(OVERVIEW)) {
                    currentMovie.setOverview(currentMovieJson.getString(OVERVIEW));
                }
                if (currentMovieJson.has(RELEASE_DATE)) {
                    currentMovie.setReleaseDate(currentMovieJson.getString(RELEASE_DATE));
                }

                if (!currentMovie.getTitle().isEmpty() && !currentMovie.getPosterPath().isEmpty()) {
                    movieList.add(currentMovie);
                }

            }
        }
        return movieList;
    }
}
