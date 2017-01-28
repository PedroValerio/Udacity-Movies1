package com.example.android.movies.MovieApi.Models;

import java.util.Date;

/**
 * Created by sv01 on 28-01-2017.
 */

public class Movie {
    String posterPath;
    String overview;
    Date releaseDate;
    int[] genreIds;
    int id;
    String originalTitle;
    String originalLanguage;
    String title;
    String backdropPath;
    Double popularity;
    int voteCount;
    Double voteAverage;
    boolean adult;

    public String getTitle(){
        return title;
    }
}
