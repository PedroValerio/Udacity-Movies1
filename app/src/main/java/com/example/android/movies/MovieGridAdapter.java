package com.example.android.movies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.MovieApi.Config;
import com.example.android.movies.MovieApi.Models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sv01 on 28-01-2017.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieGridViewHolder> {

    List<Movie> mMovieList;
    private ListItemClickListener mListener;

    public MovieGridAdapter(List<Movie> movieList, ListItemClickListener listener){
        super();
        mMovieList = movieList;
        mListener = listener;
    }

    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View tv = inflater.inflate(R.layout.movie_item_grid_view, parent, false);

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

    public interface ListItemClickListener {
        void onListItemClick(int index);
    }

    public class MovieGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mView;

        public MovieGridViewHolder(View itemView) {
            super(itemView);
            mView = (ImageView) itemView;
            itemView.setOnClickListener(this);
        }

        public void bind(Movie m){
            if (m.getPosterPath() != null) {
                if(Config.getPosterSizes().contains("w500")) {
                    String imagePath = Config.getImagesBaseUrl() + "w500" + m.getPosterPath();
                    Picasso.with(mView.getContext()).load(imagePath).resize(300, 500).centerCrop().into(mView);
                }
            }
            //((TextView) mView).setText(m.getTitle());
        }

        @Override
        public void onClick(View view) {
            mListener.onListItemClick(getAdapterPosition());
        }
    }
}
