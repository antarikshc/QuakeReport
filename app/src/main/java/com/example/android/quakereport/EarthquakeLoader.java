package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<EarthquakeData>> {

    String LOG_TAG = EarthquakeLoader.class.getSimpleName();

    private String mUrl;

    EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<EarthquakeData> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        ArrayList<EarthquakeData> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;

    }
}
