package com.example.android.popularmovies.utils;

import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.Model.Review;
import com.example.android.popularmovies.Model.Trailer;

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
        final String ID = "id";

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

                if (currentMovieJson.has(ID)) {
                    currentMovie.setId(currentMovieJson.getString(ID));
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

    public static List<Trailer> parseMovieTrailerDBJson(String json) throws JSONException {

        /* The name of the relevant fields from the JSON */
        final String NAME = "name";
        final String TYPE = "type";
        final String SITE = "site";
        final String KEY = "key";
        final String RESULTS = "results";

        JSONObject movieTrailerPageJson = new JSONObject(json);

        List<Trailer> movieTrailerList = new ArrayList<>();

        if (movieTrailerPageJson.has(RESULTS)) {
            JSONArray movieTrailersInJson = movieTrailerPageJson.getJSONArray(RESULTS);
            for (int i = 0; i < movieTrailersInJson.length(); i++) {

                JSONObject currentMovieTrailerJson = movieTrailersInJson.getJSONObject(i);
                Trailer currenTrailer = new Trailer();

                if (currentMovieTrailerJson.has(NAME)) {
                    currenTrailer.setName(currentMovieTrailerJson.getString(NAME));
                }
                if (currentMovieTrailerJson.has(TYPE)) {
                    currenTrailer.setType(currentMovieTrailerJson.getString(TYPE));
                }
                if (currentMovieTrailerJson.has(SITE)) {
                    currenTrailer.setSite(currentMovieTrailerJson.getString(SITE));
                }
                if (currentMovieTrailerJson.has(KEY)) {
                    currenTrailer.setKey(currentMovieTrailerJson.getString(KEY));
                }

                if (!currenTrailer.getKey().isEmpty() && !currenTrailer.getSite().isEmpty()){
                    movieTrailerList.add(currenTrailer);
                }
            }

        }
        return movieTrailerList;
    }


    public static List<Review> parseMovieReviewDBJson(String json) throws JSONException {

        /* The name of the relevant fields from the JSON */
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";
        final String RESULTS = "results";

        JSONObject movieReviewPageJson = new JSONObject(json);

        List<Review> movieReviewList = new ArrayList<>();

        if (movieReviewPageJson.has(RESULTS)) {
            JSONArray movieReviewsInJson = movieReviewPageJson.getJSONArray(RESULTS);
            for (int i = 0; i < movieReviewsInJson.length(); i++) {

                JSONObject currentMovieReviewJson = movieReviewsInJson.getJSONObject(i);
                Review currenReview = new Review();

                if (currentMovieReviewJson.has(AUTHOR)) {
                    currenReview.setAuthor(currentMovieReviewJson.getString(AUTHOR));
                }
                if (currentMovieReviewJson.has(CONTENT)) {
                    currenReview.setContent(currentMovieReviewJson.getString(CONTENT));
                }
                if (currentMovieReviewJson.has(URL)) {
                    currenReview.setUrl(currentMovieReviewJson.getString(URL));
                }

                if (!currenReview.getContent().isEmpty() && !currenReview.getAuthor().isEmpty()){
                    movieReviewList.add(currenReview);
                }
            }

        }
        return movieReviewList;
    }

}
