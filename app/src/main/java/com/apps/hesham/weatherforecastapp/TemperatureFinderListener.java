package com.apps.hesham.weatherforecastapp;

/**
 * Created by Hesham on 22/06/2017.
 */

public interface TemperatureFinderListener {
    void onSuccess(String locationName);
    void onFailure();
}
