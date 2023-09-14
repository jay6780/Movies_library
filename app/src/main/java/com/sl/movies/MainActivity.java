package com.sl.movies;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private Spinner filterSpinner;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private String apiKey = "62084d5eabf14a56f9ae4a8283f3611d";
    private ApiService apiService;
    private List<Movie> allMovies = new ArrayList<>();
    private List<Movie> thisWeekMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setTitle("Movieflix");
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#352F44")));

        filterSpinner = findViewById(R.id.filterSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(this);

        recyclerView = findViewById(R.id.recycler_movies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        fetchThisWeekMovies(apiKey, "en");
        fetchMovieData();
    }

    private void fetchThisWeekMovies(String apiKey, String languageCode) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        Call<MovieResponse> call = apiService.getThisWeekMovies(apiKey, languageCode, 1, "US", currentWeek);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    thisWeekMovies = response.body().getMovies();
                    // Only update adapter if "Movies This Week" is selected
                    if (filterSpinner.getSelectedItemPosition() == 0) {
                        movieAdapter.setMovies(thisWeekMovies);
                    }
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void fetchMovieData() {
        Call<MovieResponse> call = apiService.getPopularMovies(apiKey);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    allMovies = response.body().getMovies();
                    // Only update adapter if "Popular Movies" is selected
                    if (filterSpinner.getSelectedItemPosition() == 1) {
                        filterPopularMovies();
                    } else if (filterSpinner.getSelectedItemPosition() == 0) {
                        movieAdapter.setMovies(thisWeekMovies);
                    } else {
                        movieAdapter.setMovies(allMovies);
                    }
                    recyclerView.setAdapter(movieAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Movie movie) {
        Log.d("MainActivity", "Clicked movie: " + movie.getTitle());
        if (movie != null) {
            String movieUrl = movie.generateMovieUrl();

            Intent webViewIntent = new Intent(this, WebViewActivity.class);
            webViewIntent.putExtra("movieUrl", movieUrl);
            overridePendingTransition(0,0);
            startActivity(webViewIntent);
        } else {
            Toast.makeText(this, "Invalid movie", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCategory = parent.getItemAtPosition(position).toString();
        if (selectedCategory.equals("Movies This Week")) {
            fetchThisWeekMovies(apiKey, "en");
        } else if (selectedCategory.equals("Popular Movies")) {
            filterPopularMovies();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing here
    }


    private void filterPopularMovies() {
        List<Movie> filteredMovies = new ArrayList<>();

        for (Movie movie : allMovies) {
            if (movie.getPopularity() > 5) {
                filteredMovies.add(movie);
            }
        }

        movieAdapter.setMovies(filteredMovies);
    }
}