package com.popmovies.hridley.popularmovies;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.popmovies.hridley.popularmovies.utilities.RecyclerViewScrollListener;
import com.popmovies.hridley.popularmovies.utilities.NetworkUtilities;

import java.util.ArrayList;
import java.util.List;

public class MovieListFragment extends Fragment {

    public static ProgressBar mLoadingIndicator;
    public static TextView mErrorMessageDisplay;
    public static SwipeRefreshLayout mSwipeContainer;
    public static MoviesAdapter mMoviesAdapter;
    public static final String MOVIEDB_LANGUAGE = "en-US";

    private Context mContext;
    private RecyclerViewScrollListener mScrollListener;
    private int mPage;
    private int mSorting;

    private static final int SORTING_POPULAR = 1;
    private static final int SORTING_RATED = 2;
    private static final String BUNDLE_MOVIES_KEY = "movieList";
    private static final String BUNDLE_PAGE_KEY = "currentPage";
    private static final String BUNDLE_SORTING_KEY = "currentSorting";
    private static final String BUNDLE_ERROR_KEY = "errorShown";

    private static final String TAG = MovieListFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Boolean error = false;
        mContext = getContext();
        if (savedInstanceState != null) {
            error = savedInstanceState.getBoolean(BUNDLE_ERROR_KEY);
        }

        if (savedInstanceState != null && !error) {
            mPage = savedInstanceState.getInt(BUNDLE_PAGE_KEY);
            mSorting = savedInstanceState.getInt(BUNDLE_SORTING_KEY);
        } else {
            mPage = 1;
            mSorting = 1;
        }

        View rootView = inflater.inflate(R.layout.movie_list_fragment, container, false);

        final int columns = getResources().getInteger(R.integer.grid_columns);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, columns, GridLayoutManager.VERTICAL, false);
        RecyclerView recyclerView =  rootView.findViewById(R.id.rv_posters);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter();
        recyclerView.setAdapter(mMoviesAdapter);

        mLoadingIndicator =  rootView.findViewById(R.id.pb_loading_indicator);
        mScrollListener = new RecyclerViewScrollListener(gridLayoutManager, mPage) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "Loading page: " + String.valueOf(page));
                mPage = page;
                loadCards(mSorting);
            }
        };
        recyclerView.addOnScrollListener(mScrollListener);

        mSwipeContainer =  rootView.findViewById(R.id.sr_swipe_container);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                clearView();
                loadCards(mSorting);
            }
        });
        mErrorMessageDisplay = rootView.findViewById(R.id.tv_error_message_display);
        mSwipeContainer.setColorSchemeResources(R.color.colorAccent);


        if (savedInstanceState != null && !error) {
            ArrayList<Movie> movieList = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES_KEY);
            mMoviesAdapter.setMoviesData(movieList);
        } else {
            loadCards(mSorting);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<Movie> movieList = mMoviesAdapter.getMoviesData();
        if (movieList != null) {
            ArrayList<Movie> movieArrayList = new ArrayList<>(mMoviesAdapter.getMoviesData());
            outState.putParcelableArrayList(BUNDLE_MOVIES_KEY, movieArrayList);
            outState.putInt(BUNDLE_PAGE_KEY, mPage);
            outState.putInt(BUNDLE_SORTING_KEY, mSorting);
        } else {
            if (mErrorMessageDisplay.isShown()) {
                outState.putBoolean(BUNDLE_ERROR_KEY, true);
            }
        }
    }

    /**
     * A method that invokes the AsyncTask to populate the RecyclerView,
     * it's based on the sorting option selected by the user. Default is "most popular"
     *
     * @param sorting the way of sorting selected by the user
     */
    public void loadCards(int sorting) {
        if (NetworkUtilities.hasConnection(mContext)) {
            String method;
            switch (sorting) {
                case SORTING_POPULAR:
                    method = NetworkUtilities.getMethodPopular();
                    break;
                case SORTING_RATED:
                    method = NetworkUtilities.getMethodRated();
                    break;
                default:
                    method = NetworkUtilities.getMethodPopular();
                    break;
            }
            String[] posters = new String[]{method, String.valueOf(mPage)};
            new FetchMovieTask().execute(posters);
        } else {
            showErrorMessage(R.string.error_no_connectivity);
            if (mSwipeContainer.isRefreshing()) {
                mSwipeContainer.setRefreshing(false);
            }
        }
    }

    /**
     * Reset the GridView properties and adapter
     */
    public void clearView() {
        mScrollListener.resetState();
        mPage = 1;
        mMoviesAdapter.clear();
    }

    /**
     * Display the specific error message in the TextView
     *
     * @param messageId the resource id of the error string
     */
    public static void showErrorMessage(int messageId) {
        mErrorMessageDisplay.setText(Resources.getSystem().getText(messageId));
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        menu.findItem(R.id.sort_popular).setChecked(true);
        if(mSorting == SORTING_RATED){
            menu.findItem(R.id.sort_rated).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_popular || id == R.id.sort_rated) {
            if (!item.isChecked()) {
                mSorting = item.getOrder();
                item.setChecked(true);
                clearView();
                loadCards(mSorting);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
