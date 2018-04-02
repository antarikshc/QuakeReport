package com.example.android.quakereport;

/**
 * Created by antariksh on 26/3/18.
 */

public class EarthquakeData {

    private Double mag;
    private String loc;
    private Long timeInMs;
    private String url;


    public EarthquakeData(Double mag, String loc, Long timeInMs, String url) {
        this.mag = mag;
        this.loc = loc;
        this.timeInMs = timeInMs;
        this.url = url;
    }

    public Double getMag() {
        return mag;
    }

    public String getLoc() {
        return loc;
    }

    public Long getTime() {
        return timeInMs;
    }

    public String getUrl() { return url; }

}
