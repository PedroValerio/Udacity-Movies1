package com.example.android.movies.MovieApi.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Pedro Valério on 28-01-2017.
 */

public class DiscoverResults {
    @SerializedName("results")
    public List<Movie> movieList;
}
