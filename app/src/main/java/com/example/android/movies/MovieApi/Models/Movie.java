package com.example.android.movies.MovieApi.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sv01 on 28-01-2017.
 */

public class Movie implements Serializable {
    String poster_path;
    @SerializedName("overview")
    String synopsis;
    @SerializedName("release_date")
    String releaseDate;
    int[] genreIds;
    int id;
    @SerializedName("original_title")
    String originalTitle;
    String originalLanguage;
    String title;
    String backdropPath;
    Double popularity;
    int voteCount;
    @SerializedName("vote_average")
    Double voteAverage;
    boolean adult;

    public String getTitle(){
        return title;
    }

    public String getOriginalTitle(){
        return originalTitle;
    }

    public String getPosterPath(){
        return poster_path;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getReleaseDate(){
        return releaseDate;
    }

    public Double getAverageRating(){
        return voteAverage;
    }
}
