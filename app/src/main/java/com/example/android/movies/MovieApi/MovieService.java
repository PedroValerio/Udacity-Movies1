package com.example.android.movies.MovieApi;

import com.example.android.movies.MovieApi.Models.DiscoverResults;
import com.example.android.movies.MovieApi.Models.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Pedro Valério on 28-01-2017.
 */

public interface MovieService {

    @GET("discover/movie?include_adult=false&include_video=false")
    Call<DiscoverResults> discoverMovies(@Query("api_key") String apiKey,
                                         @Query("language") String lang,
                                         @Query("sort_by") String sortCriteria,
                                         @Query("page") int pageNumber);
}
