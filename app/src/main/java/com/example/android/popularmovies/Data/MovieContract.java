package com.example.android.popularmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Vlad on 4/28/2018.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "favoriteMovies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "favoriteMovies";
        public static final String COLUMN_MOVIE_NAME = "movieName";
        public static final String COLUMN_MOVIE_ID = "movieId";
    }
}
