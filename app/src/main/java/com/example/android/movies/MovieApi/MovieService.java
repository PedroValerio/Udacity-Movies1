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

    @GET("discover/movie?include_adult=false&include_video=false&vote_count.gte=100")
    Call<DiscoverResults> discoverMovies(@Query("api_key") String apiKey,
                                         @Query("language") String lang,
                                         @Query("sort_by") String sortCriteria,
                                         @Query("page") int pageNumber);
}
