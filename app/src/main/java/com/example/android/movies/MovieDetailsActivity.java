package com.example.android.movies;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.MovieApi.Config;
import com.example.android.movies.MovieApi.Models.Movie;
import com.squareup.picasso.Picasso;


public class MovieDetailsActivity extends AppCompatActivity {

    private TextView mOriginalTitleTv;
    private TextView mSynopsisTv;
    private TextView mReleaseDateTv;
    private TextView mTitleTv;
    private TextView mRatingTv;

    private ImageView mImageViewMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        setUpActionBar();

        mOriginalTitleTv = (TextView) findViewById(R.id.tv_original_title);
        mSynopsisTv = (TextView) findViewById(R.id.tv_synopsis);
        mImageViewMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);
        mReleaseDateTv = (TextView) findViewById(R.id.tv_release_date);
        mTitleTv = (TextView) findViewById(R.id.action_bar_title_left);
        mRatingTv = (TextView) findViewById(R.id.tv_rating);

        if(getIntent() != null && getIntent().hasExtra("movieObj")) {
            Movie movieObject = (Movie) getIntent().getSerializableExtra("movieObj");
            displayMovieInformation(movieObject);
        }
    }

    private void displayMovieInformation(Movie movieObject) {
        if (movieObject != null) {
            setTitleFromMovie(movieObject);
            mOriginalTitleTv.setText(movieObject.getOriginalTitle());
            mSynopsisTv.setText(movieObject.getSynopsis());
            initRatingsSection(movieObject);
            initReleaseDateSection(movieObject);
            loadMoviePoster(movieObject);
        }
    }

    private void loadMoviePoster(Movie movieObject) {
        if (movieObject.getPosterPath() != null) {
            if (Config.getPosterSizes().contains("w500")) {
                String imagePath = Config.getImagesBaseUrl() + "w500" + movieObject.getPosterPath();
                Picasso.with(this).load(imagePath).resize(500, 750).centerCrop().into(mImageViewMoviePoster);
            }
        } else {
            // Since no path was received for movie poster, hide poster image view
            mImageViewMoviePoster.setVisibility(View.GONE);
        }
    }

    private void setTitleFromMovie(Movie movieObject) {
        String titleString;
        try {
            // Sometimes API will return empty strings for the release date
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(movieObject.getReleaseDate());
            String releaseYear = new SimpleDateFormat("yyyy").format(date);
            titleString = String.format("%s (%s)", movieObject.getTitle(), releaseYear);
        } catch (ParseException e) {
            titleString = String.format("%s", movieObject.getTitle());
        }

        mTitleTv.setText(titleString);
    }

    private void initReleaseDateSection(Movie movieObject) {
        String releaseDateString;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(movieObject.getReleaseDate());
            releaseDateString = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(date);
        } catch (Exception e) {
            releaseDateString = "Release date not available.";
        }
        mReleaseDateTv.setText(releaseDateString);
    }

    private void initRatingsSection(Movie movieObject) {
        String ratingsText = String.format("%s/10 (%s %s)",
                movieObject.getAverageRating(),
                movieObject.getVoteCount(),
                getString(R.string.votes_string));
        mRatingTv.setText(ratingsText);
    }

    private void setUpActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
