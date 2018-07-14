/*
 *  Copyright 2018 Soojeong Shin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.popularmovies.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.CastAdapter;
import com.example.android.popularmovies.databinding.FragmentCastBinding;
import com.example.android.popularmovies.model.Cast;
import com.example.android.popularmovies.model.Credits;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieDetails;
import com.example.android.popularmovies.utilities.InjectorUtils;
import com.example.android.popularmovies.viewmodel.InfoViewModel;
import com.example.android.popularmovies.viewmodel.InfoViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.utilities.Constant.EXTRA_MOVIE;

/**
 * The CastFragment displays all of the cast members for the selected movie.
 */
public class CastFragment extends Fragment {

    /** Tag for a log message */
    public static final String TAG = CastFragment.class.getSimpleName();

    /** Member variable for the list of casts */
    private List<Cast> mCastList;

    /** Member variable for CastAdapter */
    private CastAdapter mCastAdapter;

    /** This field is used for data binding */
    private FragmentCastBinding mCastBinding;

    /** Member variable for the Movie object */
    private Movie mMovie;

    /**
     *  ViewModel for InformationFragment.
     *  MovieDetails data contains the cast data of the movie, and get casts data from the getDetails
     *  method in the InfoViewModel
     */
    private InfoViewModel mInfoViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public CastFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Instantiate mCastBinding using DataBindingUtil
        mCastBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_cast, container, false);
        View rootView = mCastBinding.getRoot();

        // A LinearLayoutManager is responsible for measuring and positioning item views within a
        // RecyclerView into a linear list.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mCastBinding.rvCast.setLayoutManager(layoutManager);
        mCastBinding.rvCast.setHasFixedSize(true);

        // Create an empty ArrayList
        mCastList = new ArrayList<>();

        // The CastAdapter is responsible for displaying each item in the list.
        mCastAdapter = new CastAdapter(mCastList);
        // Set Adapter on RecyclerView
        mCastBinding.rvCast.setAdapter(mCastAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get movie data from the MainActivity
        mMovie = getMovieData();

        // Observe the data and update the UI
        setupViewModel(this.getActivity(), mMovie.getId());
    }

    private void setupViewModel(Context context, int movieId) {
        // Get the ViewModel from the factory
        InfoViewModelFactory factory = InjectorUtils.provideInfoViewModelFactory(context, movieId);
        mInfoViewModel = ViewModelProviders.of(this, factory).get(InfoViewModel.class);

        // Retrieve live data object using the getMovieDetails() method from the ViewModel
        mInfoViewModel.getMovieDetails().observe(this, new Observer<MovieDetails>() {
            @Override
            public void onChanged(@Nullable MovieDetails movieDetails) {
                if (movieDetails != null) {
                    // Display cast of the movie
                    loadCast(movieDetails);
                }
            }
        });
    }

    /**
     * Gets movie data from the MainActivity.
     */
    private Movie getMovieData() {
        // Store the Intent
        Intent intent = getActivity().getIntent();
        // Check if the Intent is not null, and has the extra we passed from MainActivity
        if (intent != null) {
            if (intent.hasExtra(EXTRA_MOVIE)) {
                // Receive the Movie object which contains information, such as ID, original title,
                // poster path, overview, vote average, release date, backdrop path.
                Bundle b = intent.getBundleExtra(EXTRA_MOVIE);
                mMovie = b.getParcelable(EXTRA_MOVIE);
            }
        }
        return mMovie;
    }

    /**
     * Display the cast of the movie
     */
    private void loadCast(MovieDetails movieDetails) {
        // Get Credits from the MovieDetails
        Credits credits = movieDetails.getCredits();
        // Get the list of casts
        mCastList = credits.getCast();
        // Set the list of casts
        credits.setCast(mCastList);
        // Add a list of casts to CastAdapter
        mCastAdapter.addAll(mCastList);
    }

}
