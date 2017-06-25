package com.apps.hesham.weatherforecastapp;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DailyFragment extends Fragment implements FragmentViewListener {

    private TextView locationTextView;
    private TextView dayTextView;
    private TextView descriptionTextView;
    private TextView minMaxTextView;
    private TextView humidityTextView;
    private TextView nightTextView;
    private TextView hintTextView;
    private ImageView weatherIcon;
    private ImageView arrowImageView;
    private RelativeLayout dailyRelativeLayout;
    private String symbol = "\u2103";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_daily, container, false);
        locationTextView = (TextView) v.findViewById(R.id.locationTextView);
        dayTextView = (TextView) v.findViewById(R.id.dayTextView);
        descriptionTextView = (TextView) v.findViewById(R.id.descriptionTextView);
        minMaxTextView = (TextView) v.findViewById(R.id.minMaxTextView);
        humidityTextView = (TextView) v.findViewById(R.id.humidityTextView);
        weatherIcon = (ImageView)v.findViewById(R.id.weather_icon);
        nightTextView = (TextView)v.findViewById(R.id.nightTextView);
        hintTextView = (TextView)v.findViewById(R.id.hintTextView);
        arrowImageView = (ImageView)v.findViewById(R.id.arrowImageView);
        dailyRelativeLayout = (RelativeLayout) v.findViewById(R.id.dailyRelativeLayout);
        dailyRelativeLayout.setBackgroundColor(Color.parseColor("#A1887F"));


        //Handling screen orientations
        if (TemperatureFinder.sTemperatureData != null && TemperatureFinder.sTemperatureData.size() > 0){
            editViewsSuccess(TemperatureFinder.sTemperatureData.get(0).getCity());
            onUnitsChange(getTemperatureUnits());
        }

        return v;
    }


    @Override
    public void editViewsSuccess(String locationName) {

        if (arrowImageView == null)
            arrowImageView = (ImageView)getView().findViewById(R.id.arrowImageView);
        if (arrowImageView != null)
        arrowImageView.setVisibility(View.INVISIBLE);

        if (hintTextView == null)
            hintTextView = (TextView) getView().findViewById(R.id.hintTextView);
        if (hintTextView != null)
        hintTextView.setVisibility(View.INVISIBLE);

        if (dailyRelativeLayout == null)
            dailyRelativeLayout = (RelativeLayout) getView().findViewById(R.id.dailyRelativeLayout);
        if (dailyRelativeLayout != null)
        dailyRelativeLayout.setBackgroundColor
                (Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).getCode()).first));


        List<TemperatureData> tempData = TemperatureFinder.sTemperatureData;

        //if (locationName == null || locationName == "")
        //    locationName = tempData.get(0).countryCode;

        if (tempData.get(0).getCity() != null && !(tempData.get(0).getCity().equals("")))
            locationName = tempData.get(0).getCity();

        locationName =  Character.toUpperCase(locationName.charAt(0)) + locationName.substring(1);

        locationTextView.setText(locationName);

        descriptionTextView.setText(tempData.get(0).getDescription());

        dayTextView.setText(tempData.get(0).getDay() + symbol);

        nightTextView.setText("Night: " + tempData.get(0).getNight() + symbol);

        String minMax = "Min: " + tempData.get(0).getMin() + symbol  + "  |  " + "Max: " + tempData.get(0).getMax() + symbol;
        minMaxTextView.setText(minMax);

        Picasso.with(getActivity()).load(tempData.get(0).getIconURL()).into(weatherIcon);

        humidityTextView.setText("Humidity: " + tempData.get(0).getHumidity()+"%");

    }


    @Override
    public void onUnitsChange(String unit) {
        if (unit .equals(""))
            return;

        Double day = Double.valueOf(TemperatureFinder.sTemperatureData.get(0).getDay());
        Double night = Double.valueOf(TemperatureFinder.sTemperatureData.get(0).getNight());
        Double min = Double.valueOf(TemperatureFinder.sTemperatureData.get(0).getMin());
        Double max = Double.valueOf(TemperatureFinder.sTemperatureData.get(0).getMax());

        if (unit.equals("Celsius")){
            symbol = "\u2103";
        }
        else if (unit.equals("Fahrenheit")){
            symbol = "\u2109";
            day = day * 1.8 + 32;
            night = night * 1.8 + 32;
            min = min * 1.8 + 32;
            max = max * 1.8 + 32;

        }
        else if (unit.equals("Kelvin")){
            symbol = "\u212A";
            day += 273.15;
            night += 273.15;
            min += 273.15;
            max += 273.15;
        }

        dayTextView.setText(day + symbol);
        dayTextView.setText(String.format("%.0f", new Double(day)) + symbol);
        nightTextView.setText(String.format("%.0f", new Double(night)) + symbol);
        String minMax = "Min: " + String.format("%.0f", new Double(min))
                + symbol  + "  |  " + "Max: " + String.format("%.0f", new Double(max)) + symbol;
        minMaxTextView.setText(minMax);

    }

    private String getTemperatureUnits(){
        SharedPreferences sharedPreferences =
                getActivity().getSharedPreferences("com.apps.hesham.weatherforecastapp", getActivity().MODE_PRIVATE);
        return sharedPreferences.getString("DefaultUnits", "");
    }
}
