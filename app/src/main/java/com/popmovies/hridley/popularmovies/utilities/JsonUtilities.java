package com.popmovies.hridley.popularmovies.utilities;

import android.util.Log;

import com.popmovies.hridley.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JsonUtilities {

    private static final String STATUS_CODE = "status_code";
    private static final String STATUS_MESSAGE = "status_message";
    private static final int STATUS_INVALID_API_KEY = 7;
    private static final int STATUS_INVALID_RESOUCE = 34;
    private static final String SUCCESS = "success";

    private static final String TAG = JsonUtilities.class.getSimpleName();

    public static List<Movie> getPopularMoviesList(JSONObject popularMovies)
            throws JSONException {

        // root keys
        final String TMDB_RESULTS = "results";

        // "results" keys
        final String TMDB_R_POSTER_PATH = "poster_path";
        final String TMDB_R_ID = "id";
        final String TMDB_R_OVERVIEW = "overview";
        final String TMDB_R_ORIGINAL_TITLE = "original_title";
        final String TMDB_R_RELEASE_DATE = "release_date";
        final String TMDB_R_VOTE_AVERAGE = "vote_average";

        List<Movie> parsedMoviesData;

        if (popularMovies.has(SUCCESS)) {
            int errorCode = popularMovies.getInt(STATUS_CODE);
            String message = popularMovies.getString(STATUS_MESSAGE);

            if (errorCode == STATUS_INVALID_API_KEY || errorCode == STATUS_INVALID_RESOUCE) {
                Log.d(TAG, message);

            }
            return null;

        }

        JSONArray resultsArray = popularMovies.getJSONArray(TMDB_RESULTS);

        parsedMoviesData = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            int id;
            String posterPath;
            String overview;
            String originalTitle;
            String popularity;
            String voteAverage;

            // Get the JSON object representing one movie result
            JSONObject result = resultsArray.getJSONObject(i);

            id = result.getInt(TMDB_R_ID);
            posterPath = result.getString(TMDB_R_POSTER_PATH);
            overview = result.getString(TMDB_R_OVERVIEW);
            originalTitle = result.getString(TMDB_R_ORIGINAL_TITLE);
            popularity = result.getString(TMDB_R_RELEASE_DATE);
            voteAverage = result.getString(TMDB_R_VOTE_AVERAGE);

            Movie movie = new Movie(id, posterPath, overview, originalTitle, popularity, voteAverage);
            parsedMoviesData.add(movie);
        }

        return parsedMoviesData;
    }
}
