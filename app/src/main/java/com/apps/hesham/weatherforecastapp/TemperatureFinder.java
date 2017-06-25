package com.apps.hesham.weatherforecastapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.round;

/**
 * Created by Hesham on 22/06/2017.
 */

public class TemperatureFinder {

    private static final String TEMPERATURE_API_CALL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final String TEMPERATURE_API_KEY = "f2525ff0aaf52dba274bef9351f0f466";
    private static final String iconURL = "http://openweathermap.org/img/w/";

    public static Map<Integer, Pair<String, String>> codeColors = new HashMap<>();

    public static List<TemperatureData> sTemperatureData = new ArrayList<>();
    TemperatureFinderListener listener;

    private double Lattitude;
    private double Longitude;

    TemperatureFinder(Context c, double Lat, double Lng) {
        listener = (TemperatureFinderListener) c;
        Lattitude = Lat;
        Longitude = Lng;
        codeColors.put(2, new Pair<String, String>("#CE93D8", "#BA68C8"));
        codeColors.put(3, new Pair<String, String>("#F48FB1", "#F06292"));
        codeColors.put(1, new Pair<String, String>("#A1887F", "#795548"));
        codeColors.put(5, new Pair<String, String>("#78909C", "#546E7A"));
        codeColors.put(6, new Pair<String, String>("#66BB6A", "#388E3C"));
        codeColors.put(7, new Pair<String, String>("#4DB6AC", "#00897B"));
        codeColors.put(8, new Pair<String, String>("#26C6DA", "#0097A7"));
        codeColors.put(9, new Pair<String, String>("#388E3C", "#66BB6A"));

    }

    public void Execute() {
        new DownloadData().execute(createUrl());
    }

    private String createUrl() {

        return TEMPERATURE_API_CALL +
                "lat=" +
                Lattitude +
                "&lon=" +
                Longitude +
                "&units=metric&cnt=7" +
                "&appid=" +
                TEMPERATURE_API_KEY;

    }

    private class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];

            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();

            } catch (Exception e) {
                listener.onFailure();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                parseJSON(result);
            } catch (JSONException e) {
                listener.onFailure();
            }
        }
    }

    private void parseJSON(String data) throws JSONException {
        if (data == null) {
            return;
        }

        sTemperatureData.clear();
        sTemperatureData = new ArrayList<>();

        JSONObject jsonData = new JSONObject(data);
        JSONObject city = jsonData.getJSONObject("city");
        String locationName = city.getString("name");
        JSONArray jsonList = jsonData.getJSONArray("list");

        for (int i = 0; i < jsonList.length(); i++) {

            TemperatureData dayData = new TemperatureData();

            JSONObject jsonListItem = jsonList.getJSONObject(i);

            JSONObject jsonTemp = jsonListItem.getJSONObject("temp");
            //dayData.day = jsonTemp.getString("day");
            dayData.setDay(String.format("%.0f", new Double(jsonTemp.getString("day"))));
            //dayData.min = jsonTemp.getString("min");
            dayData.setMin(String.format("%.0f", new Double(jsonTemp.getString("min"))));
            //dayData.max = jsonTemp.getString("max");
            dayData.setMax(String.format("%.0f", new Double(jsonTemp.getString("max"))));
            //dayData.night = jsonTemp.getString("night");
            dayData.setNight(String.format("%.0f", new Double(jsonTemp.getString("night"))));


            //dayData.humidity = jsonListItem.getString("humidity");
            dayData.setHumidity(jsonListItem.getString("humidity"));

            JSONArray weatherArray = jsonListItem.getJSONArray("weather");

            //dayData.main = weatherArray.getJSONObject(0).getString("main");
            dayData.setMain(weatherArray.getJSONObject(0).getString("main"));
            //dayData.description = weatherArray.getJSONObject(0).getString("description");
            dayData.setDescription(weatherArray.getJSONObject(0).getString("description"));
            //dayData.city = locationName;
            dayData.setCity(locationName);

            String icon = weatherArray.getJSONObject(0).getString("icon");
            //dayData.iconURL = iconURL + icon + ".png";
            dayData.setIconURL(iconURL + icon + ".png");

            String code = weatherArray.getJSONObject(0).getString("id").substring(0, 1);
            Integer codeInt = Integer.parseInt(code);
            //dayData.code = codeInt;
            dayData.setCode(codeInt);

            //dayData.countryCode = city.getString("country");
            dayData.setCountryCode(city.getString("country"));

            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, i);
            date = c.getTime();
            //dayData.dayOfWeek = sdf.format(date);
            dayData.setDayOfWeek(sdf.format(date));

            sTemperatureData.add(dayData);
        }
        listener.onSuccess(locationName);
    }
}
