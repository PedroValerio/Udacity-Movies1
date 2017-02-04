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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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


    private static final int LANDSCAPE_LAYOUT_SPAN = 3;
    private static final int PORTRAIT_LAYOUT_SPAN = 2;

    private static final int SORT_BY_RATING = 1;
    private static final int SORT_BY_POPULARITY = 2;

    // For implementing the loading of new movies when reaching the end of the recyclerview
    private boolean loading = true;

    private GridLayoutManager gridLayoutManager;
    private MovieGridAdapter mMovieGridAdapter;
    private RecyclerView rv;
    private List<Movie> mMovieList;
    private int mPageNumber = 0;
    private int mCurrentSortCriteria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.rv_movies);

        initRecyclerView(savedInstanceState);

    }

    private void initRecyclerView(Bundle savedInstanceState) {
        int activityOrientation = getResources().getConfiguration().orientation;

        /* If the screen is in portrait mode, initialize the GridLayoutManager with each row having 2 columns
        Otherwise initialize it with each row having 3 columns
        Landscape mode has more width so this is to make use of as much of the screen width as possible
        */
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

        rv.setLayoutManager(gridLayoutManager);

        rv.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int pastVisibleItems, visibleItemCount, totalItemCount;
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisibleItems) >= totalItemCount)
                        {
                            loading = false;
                            loadContent();
                        }
                    }
                }
            }
        });

        if(savedInstanceState != null){
            int pageNumber = savedInstanceState.getInt("mPageNumber", -1);
            if(pageNumber == -1){
                Log.d(TAG, "mPageNumber is not defined in savedInstanceState?");
            }
            else {
                mPageNumber = pageNumber;
            }
            int sortCriteria = savedInstanceState.getInt("mCurrentSortCriteria", -1);
            if(sortCriteria == -1){
                Log.d(TAG, "mCurrentSortCriteria is not defined in savedInstanceState?");
            }
            else {
                mCurrentSortCriteria = sortCriteria;
            }
            Parcelable rvState = savedInstanceState.getParcelable("recyclerViewState");
            if(rvState == null) {
                Log.d(TAG, "recyclerViewState is not defined in savedInstanceState?");
            }
            else
                rv.getLayoutManager().onRestoreInstanceState(rvState);


            List<Movie> movieList = (List) savedInstanceState.getSerializable("mMovieList");
            if(movieList == null){
                Log.d(TAG, "movieList is not defined in savedInstanceState?");
            }
            else {
                mMovieList = movieList;
                mMovieGridAdapter = new MovieGridAdapter(mMovieList, MainActivity.this);
                rv.setAdapter(mMovieGridAdapter);
            }

        }
        else {
            mCurrentSortCriteria = SORT_BY_POPULARITY;
            loadContent();
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

    public class LoadMoviesTask extends AsyncTask<Void, Void, DiscoverResults> {

        private int mSortCriteria;
        private int mPageNumber;

        public LoadMoviesTask(int sortCriteria, int pageNumber) {
            mSortCriteria = sortCriteria;
            mPageNumber = pageNumber;
        }

        @Override
        protected DiscoverResults doInBackground(Void... voids) {

            Retrofit retrofit = new Retrofit.Builder().baseUrl(Config.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieService service = retrofit.create(MovieService.class);

            Call<DiscoverResults> discoverCall;
            if(this.mSortCriteria == SORT_BY_POPULARITY)
                discoverCall = service.discoverMovies(Config.getApiKey(), Config.getUserLang(), Config.SORT_POPULARITY_DESC, mPageNumber);
            else
                discoverCall = service.discoverMovies(Config.getApiKey(), Config.getUserLang(), Config.SORT_RATING_DESC, mPageNumber);
            Call<ImageApiConfiguration> configurationCall = service.getConfiguration(Config.getApiKey());
            try {
                DiscoverResults discoverResults = discoverCall.execute().body();
                ImageApiConfiguration configuration = configurationCall.execute().body();
                Config.setImagesBaseUrl(configuration.imagePaths.base_url);
                Config.setPosterSizes(configuration.imagePaths.poster_sizes);
                return discoverResults;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DiscoverResults results) {
            if(mMovieList == null || mMovieList.size() == 0) {
                mMovieList = results.movieList;
                mMovieGridAdapter = new MovieGridAdapter(mMovieList, MainActivity.this);
                rv.setAdapter(mMovieGridAdapter);
            }
            else {
                mMovieList.addAll(results.movieList);
            }

            mMovieGridAdapter.notifyDataSetChanged();
            loading = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Parcelable mRecyclerViewState  = rv.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("recyclerViewState", mRecyclerViewState);
        outState.putInt("mCurrentSortCriteria", mCurrentSortCriteria);
        outState.putInt("mPageNumber", mPageNumber);
        outState.putSerializable("mMovieList", (Serializable) mMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_sort_by_popularity) {
            Toast.makeText(this, "Sort by popularity", Toast.LENGTH_LONG).show();
            changeSortCriteria(SORT_BY_POPULARITY);
            return true;
        }
        if(itemId == R.id.action_sort_by_rating){
            Toast.makeText(this, "Sort by rating", Toast.LENGTH_LONG).show();
            changeSortCriteria(SORT_BY_RATING);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSortCriteria(int sortCriteria) {
        if(mCurrentSortCriteria == sortCriteria){
            return;
        }
        mCurrentSortCriteria = sortCriteria;
        mPageNumber = 0;
        mMovieList = new ArrayList<Movie>();
        mMovieGridAdapter = new MovieGridAdapter(mMovieList, MainActivity.this);
        rv.setAdapter(mMovieGridAdapter);
        loadContent();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }
}
