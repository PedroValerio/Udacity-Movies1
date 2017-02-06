package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.movies.MovieApi.Config;
import com.example.android.movies.MovieApi.Models.DiscoverResults;
import com.example.android.movies.MovieApi.Models.ImageApiConfiguration;
import com.example.android.movies.MovieApi.Models.Movie;
import com.example.android.movies.MovieApi.MovieService;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieGridAdapter.ListItemClickListener {
    private static final String TAG = "MoviesUdacity";

    // Number of columns for grid in landscape mode
    private static final int LANDSCAPE_LAYOUT_SPAN = 3;
    // Number of columns for grid in portrait mode
    private static final int PORTRAIT_LAYOUT_SPAN = 2;

    private static final int SORT_BY_RATING = 1;
    private static final int SORT_BY_POPULARITY = 2;
    private static final int DEFAULT_SORT_CRITERIA = SORT_BY_POPULARITY;

    private int mPageNumber;
    private int mCurrentSortCriteria;
    private List<Movie> mMovieList;

    // For implementing the loading of new movies when reaching the end of the recyclerview
    private boolean loading = true;

    private ProgressBar mLoadingIndicator;
    private GridLayoutManager mGridLayoutManager;
    private MovieGridAdapter mMovieGridAdapter;
    private RecyclerView mRecyclerView;
    private MenuItem mSortByPopularity;
    private MenuItem mSortByRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadDefaultValues();
        // Values saved in the bundle will overwrite as necessary
        if(savedInstanceState != null) {
            loadValuesFromBundle(savedInstanceState);
            // Initialize RecyclerView with previous state if it exists
            Parcelable rvState = savedInstanceState.getParcelable("recyclerViewState");
            if (rvState != null) {
                initRecyclerView(rvState);
            }
        }
        else {
            initRecyclerView();
        }

        loadContent();
    }

    private void initRecyclerView() {
        int activityOrientation = getResources().getConfiguration().orientation;
        mGridLayoutManager = initLayoutManager(activityOrientation);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pastVisibleItems, visibleItemCount, totalItemCount;
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mGridLayoutManager.getChildCount();
                    totalItemCount = mGridLayoutManager.getItemCount();
                    pastVisibleItems = mGridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            loadContent();
                        }
                    }
                }
            }
        });

        mRecyclerView.setAdapter(mMovieGridAdapter);
    }

    private void initRecyclerView(Parcelable rvState) {
        initRecyclerView();
        mRecyclerView.getLayoutManager().onRestoreInstanceState(rvState);
    }

    private GridLayoutManager initLayoutManager(int activityOrientation) {
        /* If the screen is in portrait mode, initialize the GridLayoutManager with each row having 2 columns
        Otherwise initialize it with each row having 3 columns
        Landscape mode has more width so this is to make use of as much of the screen width as possible
        */
        GridLayoutManager gridLayoutManager;
        if (activityOrientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(MainActivity.this,
                    PORTRAIT_LAYOUT_SPAN,
                    GridLayoutManager.VERTICAL,
                    false);
        } else {
            gridLayoutManager = new GridLayoutManager(MainActivity.this,
                    LANDSCAPE_LAYOUT_SPAN,
                    GridLayoutManager.VERTICAL,
                    false);
        }
        return gridLayoutManager;
    }
    private void loadDefaultValues() {
        mPageNumber = 0;
        mCurrentSortCriteria = DEFAULT_SORT_CRITERIA;
        mMovieList = new ArrayList<>();
    }

    private void loadValuesFromBundle(Bundle savedInstanceState) {
        if(savedInstanceState == null)
            return;

        int pageNumber = savedInstanceState.getInt("mPageNumber", -1);
        if (pageNumber != -1) {
            mPageNumber = pageNumber;
        }
        int sortCriteria = savedInstanceState.getInt("mCurrentSortCriteria", -1);
        if (sortCriteria != -1) {
            mCurrentSortCriteria = sortCriteria;
        }

        List<Movie> movieList = (List) savedInstanceState.getSerializable("mMovieList");
        if (movieList != null) {
            mMovieList = movieList;
            mMovieGridAdapter = new MovieGridAdapter(mMovieList, MainActivity.this);
        }
    }


    private void loadContent() {
        mPageNumber += 1;
        LoadMoviesTask loadMoviesTask = new LoadMoviesTask(mCurrentSortCriteria, mPageNumber);
        loadMoviesTask.execute();

    }

    @Override
    public void onListItemClick(int index) {
        Context context = this;
        Class targetActivityClass = MovieDetailsActivity.class;
        Intent intent = new Intent(context, targetActivityClass);
        intent.putExtra("movieObj", mMovieList.get(index));
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Parcelable mRecyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerViewState);
        outState.putInt("mCurrentSortCriteria", mCurrentSortCriteria);
        outState.putInt("mPageNumber", mPageNumber);
        outState.putSerializable("mMovieList", (Serializable) mMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_sort_by_popularity) {
            changeSortCriteria(SORT_BY_POPULARITY);
            return true;
        }
        if (itemId == R.id.action_sort_by_rating) {
            changeSortCriteria(SORT_BY_RATING);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSortCriteria(int sortCriteria) {
        if (mCurrentSortCriteria == sortCriteria) {
            return;
        }
        disableSortCriteria(sortCriteria);
        loadDefaultValues();
        mCurrentSortCriteria = sortCriteria;
        loadContent();

    }

    private void disableSortCriteria(int sortCriteria) {
        if (sortCriteria == SORT_BY_POPULARITY) {
            mSortByRating.setEnabled(true);
            mSortByPopularity.setEnabled(false);
        } else {
            mSortByRating.setEnabled(false);
            mSortByPopularity.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        mSortByPopularity = menu.findItem(R.id.action_sort_by_popularity);
        mSortByRating = menu.findItem(R.id.action_sort_by_rating);
        return true;
    }

    private void displayErrorMessage() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
    }
    private void hideLoadingIndicator(){
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void showLoadingIndicator(){
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public class LoadMoviesTask extends AsyncTask<Void, Void, DiscoverResults> {

        private int mSortCriteria;
        private int mPageNumber;

        public LoadMoviesTask(int sortCriteria, int pageNumber) {
            mSortCriteria = sortCriteria;
            mPageNumber = pageNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mMovieList.isEmpty()){
                showLoadingIndicator();
            }
        }

        @Override
        protected DiscoverResults doInBackground(Void... voids) {

            Retrofit retrofit = new Retrofit.Builder().baseUrl(Config.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieService service = retrofit.create(MovieService.class);

            Call<DiscoverResults> discoverCall;

            // Change 3rd parameter depending on sort criteria
            if (this.mSortCriteria == SORT_BY_POPULARITY)
                discoverCall = service.discoverMovies(Config.getApiKey(), Config.getUserLang(), Config.SORT_POPULARITY_DESC, mPageNumber);
            else
                discoverCall = service.discoverMovies(Config.getApiKey(), Config.getUserLang(), Config.SORT_RATING_DESC, mPageNumber);

            // Get available image resources in the API
            Call<ImageApiConfiguration> configurationCall = service.getConfiguration(Config.getApiKey());

            try {
                DiscoverResults discoverResults = discoverCall.execute().body();
                ImageApiConfiguration configuration = configurationCall.execute().body();
                if (configuration != null) {
                    Config.setImagesBaseUrl(configuration.imagePaths.base_url);
                    Config.setPosterSizes(configuration.imagePaths.poster_sizes);
                }
                return discoverResults;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DiscoverResults results) {
            hideLoadingIndicator();

            // No results were returned, some error in the connection or the API
            if (results == null || results.movieList == null) {
                displayErrorMessage();
                return;
            }

            if (mMovieList.isEmpty()) {
                mMovieList = results.movieList;
                mMovieGridAdapter = new MovieGridAdapter(mMovieList, MainActivity.this);
                mRecyclerView.setAdapter(mMovieGridAdapter);
            } else {
                mMovieList.addAll(results.movieList);
            }

            disableSortCriteria(mCurrentSortCriteria);
            mMovieGridAdapter.notifyDataSetChanged();
            loading = true;
        }
    }
}
