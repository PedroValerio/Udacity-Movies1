package com.example.android.movies.MovieApi.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sv01 on 28-01-2017.
 */

public class DiscoverResults {
    public int page;
    @SerializedName("results")
    public List<Movie> movieList;
}
