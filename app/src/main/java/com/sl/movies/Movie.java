package com.sl.movies;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("id") // Use the movie's ID for generating the URL
    private int movieId;

    @SerializedName("popularity") // Add this field for popularity
    private double popularity;

    // Constructors, getters, and setters for other fields as needed

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public int getMovieId() {
        return movieId;
    }

    public double getPopularity() {
        return popularity;
    }

    public String generateMovieUrl() {
        return "https://www.themoviedb.org/movie/" + movieId;
    }
}
