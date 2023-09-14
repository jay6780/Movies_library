package com.sl.movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Call<MovieResponse> getThisWeekMovies(
            @Query("api_key") String apiKey,
            @Query("with_original_language") String languageCode,
            @Query("page") int page,
            @Query("region") String region,
            @Query("primary_release_week") int primaryReleaseWeek
    );

    // New API endpoint to get movie details by ID
    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    // Add more API endpoints here as needed
}
