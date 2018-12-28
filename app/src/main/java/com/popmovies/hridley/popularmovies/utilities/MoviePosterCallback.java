package com.popmovies.hridley.popularmovies.utilities;

import android.view.View;

import com.popmovies.hridley.popularmovies.MoviesAdapter;
import com.squareup.picasso.Callback;

public class MoviePosterCallback extends Callback.EmptyCallback {

    private MoviesAdapter.MoviesAdapterViewHolder mViewHolder;


    public MoviePosterCallback(MoviesAdapter.MoviesAdapterViewHolder viewHolder) {
        this.mViewHolder = viewHolder;
    }

    @Override
    public void onSuccess() {
        mViewHolder.mMoviePosterProgressBar.setVisibility(View.GONE);
        mViewHolder.mMoviePosterErrorTextView.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        mViewHolder.mMoviePosterProgressBar.setVisibility(View.GONE);
        mViewHolder.mMoviePosterErrorTextView.setRotation(-20);
        mViewHolder.mMoviePosterErrorTextView.setVisibility(View.VISIBLE);
    }
}