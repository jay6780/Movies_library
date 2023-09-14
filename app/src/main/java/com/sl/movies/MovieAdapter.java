package com.sl.movies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }


    public MovieAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private ImageView posterImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            posterImageView = itemView.findViewById(R.id.moviePosterImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Movie clickedMovie = movies.get(position);
                        if (listener != null && clickedMovie != null) {
                            listener.onItemClick(clickedMovie); // Pass the Movie object
                        }
                    }
                }
            });
        }

        public void bind(Movie movie) {
            titleTextView.setText(movie.getTitle());

            // Load and display the movie poster using Picasso
            Picasso.get()
                    .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath()) // Replace with the actual base URL and image size
                    .placeholder(R.drawable.ic_baseline_personal_video_24) // Placeholder image while loading
                    .error(R.drawable.ic_baseline_personal_video_24) // Error image if loading fails
                    .fit()
                    .into(posterImageView);
        }
    }
}