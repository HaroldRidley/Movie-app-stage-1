package com.popmovies.hridley.popularmovies;

import android.os.AsyncTask;
import android.view.View;

import com.popmovies.hridley.popularmovies.utilities.JsonUtilities;
import com.popmovies.hridley.popularmovies.utilities.NetworkUtilities;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The background worker that executes the calls to the MovieDB service
 */
public class FetchMovieTask extends AsyncTask<String[], Void, List<Movie>> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MovieListFragment.mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<Movie> doInBackground(String[]... params) {
        String method = params[0][0];
        String page = params[0][1];
        Map<String, String> mapping = new HashMap<>();

        mapping.put(NetworkUtilities.getLanguage(), MovieListFragment.MOVIEDB_LANGUAGE);
        mapping.put(NetworkUtilities.getPage(), String.valueOf(page));

        URL url = NetworkUtilities.buildUrl(method, mapping);

        try {
            String response = NetworkUtilities.getResponse(url);
            JSONObject responseJson = new JSONObject(response);

            return JsonUtilities.getPopularMoviesList(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movieList) {
        MovieListFragment.mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movieList != null) {
            MovieListFragment.mMoviesAdapter.setMoviesData(movieList);
            MovieListFragment.mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        } else {

            MovieListFragment.showErrorMessage(R.string.error_moviedb_list);
        }
        MovieListFragment.mSwipeContainer.setRefreshing(false);
    }
}