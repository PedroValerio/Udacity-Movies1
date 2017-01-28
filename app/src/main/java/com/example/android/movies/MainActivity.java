package com.example.android.movies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.movies.MovieApi.Config;
import com.example.android.movies.MovieApi.Models.DiscoverResults;
import com.example.android.movies.MovieApi.Models.Movie;
import com.example.android.movies.MovieApi.MovieService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.text_view);

        rv = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        gridLayoutManager.setSpanCount(4);
        rv.setLayoutManager(gridLayoutManager);

        loadContent();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int actionId = item.getItemId();
        if(actionId == R.id.action_refresh){
            loadContent();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadContent() {
        DiscoverMoviesTask discoverMoviesTask = new DiscoverMoviesTask();
        discoverMoviesTask.execute();

    }

    public class DiscoverMoviesTask extends AsyncTask<Void, Void, DiscoverResults> {
        @Override
        protected DiscoverResults doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Config.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieService service = retrofit.create(MovieService.class);

            Call<DiscoverResults> discoverCall = service.discoverMovies(Config.API_KEY, Config.USERLANG, Config.SORT_POPULARITY_DESC, 1);
            try {
                DiscoverResults discoverResults = discoverCall.execute().body();
                return discoverResults;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DiscoverResults results) {
            MovieGridAdapter movieGridAdapter = new MovieGridAdapter(results.movieList);
            rv.setAdapter(movieGridAdapter);
        }
    }
}
