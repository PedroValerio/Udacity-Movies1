package com.example.android.movies.MovieApi.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Pedro Valério on 28-01-2017.
 */

public class Movie implements Serializable {
    String poster_path;
    @SerializedName("overview")
    String synopsis;
    @SerializedName("release_date")
    String releaseDate;
    @SerializedName("original_title")
    String originalTitle;
    String title;
    @SerializedName("vote_count")
    int voteCount;
    @SerializedName("vote_average")
    Double voteAverage;

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

    public int getVoteCount(){
        return voteCount;
    }
}
