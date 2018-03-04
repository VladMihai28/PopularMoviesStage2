package com.example.android.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.Model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter used for feeding Movie data to the RecyclerView
  */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{


    private List<Movie> movieList;

    private final MovieAdapterOnClickHandler clickHandler;

    public MovieAdapter (MovieAdapterOnClickHandler clickHandler){
        this.clickHandler= clickHandler;
    }

    /**
     * The interface that receives onClick messages
     */
    public interface MovieAdapterOnClickHandler{
        void onClick(Movie currentMovie);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView movieThumbnail;

        public MovieAdapterViewHolder(View view){
            super(view);
            movieThumbnail = view.findViewById(R.id.imageview_movie_thumbnail);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie currentMovie = movieList.get(adapterPosition);
            clickHandler.onClick(currentMovie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        Movie currentMovie = movieList.get(position);
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        Picasso.with(holder.movieThumbnail.getContext())
                .load(currentMovie.getPosterPath())
                .resize(width/2, height/2)
                .into(holder.movieThumbnail);
    }

    @Override
    public int getItemCount() {
        if (null == movieList) return 0;
        return movieList.size();
    }

    /**
     * Set movie data on the MovieAdapter
     * @param newMovieList the new data that needs to be set
     */
    public void setMovieData(List<Movie> newMovieList){
        this.movieList = newMovieList;
        notifyDataSetChanged();
    }

}
