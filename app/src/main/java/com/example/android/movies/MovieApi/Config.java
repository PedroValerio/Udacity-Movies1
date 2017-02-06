package com.example.android.movies.MovieApi;

import java.util.List;

/**
 * Created by Pedro Valério on 28-01-2017.
 */

public class Config {
    public static final String SORT_POPULARITY_DESC = "popularity.desc";
    public static final String SORT_RATING_DESC = "vote_average.desc";

    private static final String API_KEY = "API KEY HERE";
    private static final String API_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String USER_LANGUAGE = "en-us";

    private static String mImagesBaseUrl;
    private static List<String> mPosterSizes;


    public static String getBaseUrl(){
        return API_BASE_URL;
    }

    public static String getUserLang(){
        return USER_LANGUAGE;
    }

    public static String getApiKey(){
        return API_KEY;
    }

    public static String getImagesBaseUrl(){
        return mImagesBaseUrl;
    }

    public static void setImagesBaseUrl(String imgBaseUrl) {
        mImagesBaseUrl = imgBaseUrl;
    }

    public static void setPosterSizes(List<String> posterSizes){
        mPosterSizes = posterSizes;
    }

    public static List<String> getPosterSizes(){
        return mPosterSizes;
    }

}
