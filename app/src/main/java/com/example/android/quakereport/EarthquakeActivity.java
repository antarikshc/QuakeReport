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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    /**
     * URL to query the USGS dataset for earthquake information
     */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";


    ListView earthquakeListView;
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = findViewById(R.id.list);

        EarthQuakeTask task = new EarthQuakeTask();
        task.execute(USGS_REQUEST_URL);

    }

    private class EarthQuakeTask extends AsyncTask<String, Void, ArrayList<EarthquakeData>> {

        String LOG_TAG = EarthquakeActivity.class.getSimpleName();

        @Override
        protected ArrayList<EarthquakeData> doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            // Create URL object
            URL url = QueryUtils.createUrl(urls[0]);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = QueryUtils.makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }

            ArrayList<EarthquakeData> fetchedData = QueryUtils.extractEarthquakes(jsonResponse);

            return fetchedData;
        }

        @Override
        protected void onPostExecute(final ArrayList<EarthquakeData> earthquakes) {

            if (earthquakes != null && !earthquakes.isEmpty()) {

                customAdapter = new CustomAdapter(getApplicationContext(), earthquakes);

                // Set the adapter on the {@link ListView}
                // so the list can be populated in the user interface
                earthquakeListView.setAdapter(customAdapter);


                earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        EarthquakeData currentData = earthquakes.get(i);

                        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                        intent.putExtra("url", currentData.getUrl());

                        startActivity(intent);
                    }
                });

            }
        }
    }

}
