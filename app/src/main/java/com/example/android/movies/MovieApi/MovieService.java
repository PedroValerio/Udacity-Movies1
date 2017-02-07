package com.example.android.movies.MovieApi;

import com.example.android.movies.MovieApi.Models.DiscoverResults;
import com.example.android.movies.MovieApi.Models.ImageApiConfiguration;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Pedro Valério on 28-01-2017.
 */

public interface MovieService {

    @GET("configuration")
    Call<ImageApiConfiguration> getConfiguration(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<DiscoverResults> getPopularMovies(@Query("api_key") String apiKey,
                                           @Query("language") String lang,
                                           @Query("page") int pageNumber);

    @GET("movie/top_rated")
    Call<DiscoverResults> getTopRatedMovies(@Query("api_key") String apiKey,
                                           @Query("language") String lang,
                                           @Query("page") int pageNumber);
}
