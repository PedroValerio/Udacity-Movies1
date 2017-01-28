package com.example.android.movies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movies.MovieApi.Models.Movie;

import java.util.List;

/**
 * Created by sv01 on 28-01-2017.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieGridViewHolder> {

    List<Movie> mMovieList;

    public MovieGridAdapter(List<Movie> movieList){
        super();
        mMovieList = movieList;
    }
    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TextView tv = (TextView) inflater.inflate(R.layout.movie_item_grid_view, parent, false);

        return new MovieGridViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(MovieGridViewHolder holder, int position) {
        holder.bind(mMovieList.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public class MovieGridViewHolder extends RecyclerView.ViewHolder {
        private View mView;

        public MovieGridViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void bind(Movie m){
            ((TextView) mView).setText(m.getTitle());
        }

    }
}
