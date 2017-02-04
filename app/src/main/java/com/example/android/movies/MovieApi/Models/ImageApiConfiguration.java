package com.example.android.movies.MovieApi.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sv01 on 29-01-2017.
 */

public class ImageApiConfiguration {
    @SerializedName("images")
    public ImagePaths imagePaths;

    public class ImagePaths {
        public String base_url;

        public List<String> poster_sizes;
    }
}
