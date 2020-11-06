package edu.uci.ics.fabflixmobile;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private List<Movie> movies;

    public MovieListViewAdapter(List<Movie> movies, Context context) {
        super(context, R.layout.row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row, parent, false);

        Movie movie = movies.get(position);

        TextView titleView = view.findViewById(R.id.title);
        TextView subtitleView = view.findViewById(R.id.subtitle);
        TextView star = view.findViewById(R.id.star);
        TextView genre = view.findViewById(R.id.genre);
        TextView rating = view.findViewById(R.id.rating);

        titleView.setText(movie.getName());
        subtitleView.setText(movie.getYear() + "");
        star.setText(movie.getThreeStar() + "");// need to cast the year to a string to set the label
        genre.setText(movie.getGenre() + "");
        rating.setText("Rating: " + movie.getRating() + "");
        return view;
    }
}