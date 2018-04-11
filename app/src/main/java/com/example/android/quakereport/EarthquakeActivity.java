/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<EarthquakeData>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    /**
     * URL to query the USGS dataset maximum 10 recent quakes with minMag = 6
     */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private static int EARTHQUAKE_LOADER_ID = 1;


    ListView earthquakeListView;
    private CustomAdapter customAdapter;
    private TextView EmptyStateTextView;
    ProgressBar loadSpin;
    SwipeRefreshLayout swipeRefreshLayout;

    LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = findViewById(R.id.list);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        loadSpin = findViewById(R.id.loadSpin);

        customAdapter = new CustomAdapter(getApplicationContext(), new ArrayList<EarthquakeData>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(customAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EarthquakeData currentData = customAdapter.getItem(i);

                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                if (currentData != null) {
                    intent.putExtra("url", currentData.getUrl());
                } else {
                    Toast.makeText(EarthquakeActivity.this, "No URL found for requested earthquake!", Toast.LENGTH_SHORT).show();
                }

                startActivity(intent);
            }
        });

        EmptyStateTextView = findViewById(R.id.emptyView);
        earthquakeListView.setEmptyView(EmptyStateTextView);


        loaderManager = getLoaderManager();
        boolean netConnection = checkNet();
        if (netConnection) {
            executeLoader();
        } else {
            EmptyStateTextView.setText(R.string.no_network);
            loadSpin.setVisibility(View.GONE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EmptyStateTextView.setText("");

                EARTHQUAKE_LOADER_ID += 1;
                swipeRefreshLayout.setRefreshing(true);
                executeLoader();

                destroyLoader(EARTHQUAKE_LOADER_ID - 1);

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void executeLoader() {
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
    }

    private void destroyLoader(int id) {
        loaderManager.destroyLoader(id);
    }

    @Override
    public Loader<ArrayList<EarthquakeData>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        // Create a new loader for the given URL
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<EarthquakeData>> loader, ArrayList<EarthquakeData> earthquakes) {
        EmptyStateTextView.setText(R.string.no_earthquakes);

        loadSpin.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        customAdapter.clear();


        if (earthquakes != null && !earthquakes.isEmpty()) {
            loadSpin.setVisibility(View.GONE);
            customAdapter.addAll(earthquakes);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<EarthquakeData>> loader) {
        // Loader reset, so we can clear out our existing data.
        customAdapter.clear();
    }

    public boolean checkNet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
