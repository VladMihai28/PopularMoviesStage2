package com.example.android.popularmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that defines Movie information
 */

public class Movie implements Parcelable {

    /* Movie information as described in the JSON response from the Movie DB API
     */

    private float voteAverage;
    private String title;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private String id;

    public Movie(){}

    /* Constructor that can get data from a Parcel */
    public Movie(Parcel parcel){
        title = parcel.readString();
        releaseDate = parcel.readString();
        posterPath = parcel.readString();
        voteAverage = parcel.readFloat();
        overview = parcel.readString();
        id = parcel.readString();
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /* Write some of the fields of the Movie into a parcel */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
        parcel.writeFloat(voteAverage);
        parcel.writeString(overview);
        parcel.writeString(id);

    }

    /* Parcel creator */
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
