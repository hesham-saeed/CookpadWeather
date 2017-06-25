package com.apps.hesham.weatherforecastapp;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class WeeklyFragment extends Fragment implements FragmentViewListener {

    private RecyclerView recyclerView;
    private dayAdapter adapter;

    private TextView hintWeeklyTextView;
    private ImageView arrowWeeklyImageView;
    private String symbol = "\u2103";

    private RelativeLayout weeklyRelativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_weekly, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        hintWeeklyTextView = (TextView)view.findViewById(R.id.hintWeeklyTextView);

        weeklyRelativeLayout = (RelativeLayout)view.findViewById(R.id.weeklyRelativeLayout);
        weeklyRelativeLayout.setBackgroundColor(Color.parseColor("#A1887F"));
        arrowWeeklyImageView = (ImageView) view.findViewById(R.id.weeklyArrowImageView);


        if (TemperatureFinder.sTemperatureData != null && TemperatureFinder.sTemperatureData.size() > 0){
            editViewsSuccess(TemperatureFinder.sTemperatureData.get(0).city);
        }

        return view;
    }


    @Override
    public void editViewsSuccess(String locationName) {
        if (arrowWeeklyImageView == null)
            arrowWeeklyImageView = (ImageView) getView().findViewById(R.id.weeklyArrowImageView);
        arrowWeeklyImageView.setVisibility(View.INVISIBLE);

        if (hintWeeklyTextView == null)
            hintWeeklyTextView = (TextView) getView().findViewById(R.id.hintWeeklyTextView);
        hintWeeklyTextView.setVisibility(View.INVISIBLE);

        if (weeklyRelativeLayout == null)
            weeklyRelativeLayout = (RelativeLayout)getView().findViewById(R.id.weeklyRelativeLayout);
        weeklyRelativeLayout.setBackgroundColor
                (Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).code).first));

        if (recyclerView == null)
            recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setBackgroundColor(Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).code).second));

        adapter = new dayAdapter();
        recyclerView.setAdapter(adapter);

    }


    private class dayHolder extends RecyclerView.ViewHolder {

        private TextView weeklyDayTextView;
        private ImageView weeklyDayIconImageView;
        private TextView weeklyDateDegreeTextView;

        public dayHolder(View itemView) {
            super(itemView);

            weeklyDayTextView = (TextView) itemView.findViewById(R.id.weeklyDayTextView);
            weeklyDayIconImageView = (ImageView)itemView.findViewById(R.id.weeklyDayIconImageView);
            weeklyDateDegreeTextView = (TextView) itemView.findViewById(R.id.weeklyDayDegreeTextView);
        }

    }


    private class dayAdapter extends RecyclerView.Adapter<dayHolder> {

        private List<TemperatureData> tempData = TemperatureFinder.sTemperatureData;

        @Override
        public dayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.day_item, parent, false);
            view.setBackgroundColor(Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).code).first));
            return new dayHolder(view);
        }

        @Override
        public void onBindViewHolder(dayHolder holder, int position) {
            Double day = Double.valueOf(tempData.get(position).day);
            String unit = getTemperatureUnits();
            if (unit.equals("Celsius")){
                symbol = "\u2103";
            }
            else if (unit.equals("Fahrenheit")){
                symbol = "\u2109";
                day = day * 1.8 + 32;
            }
            else if (unit.equals("Kelvin")){
                symbol = "\u212A";
                day += 273.15;
            }

            holder.weeklyDateDegreeTextView.setText(String.format("%.0f", new Double(day)) + symbol);
            Picasso.with(getActivity()).load(tempData.get(0).iconURL).into(holder.weeklyDayIconImageView);
            holder.weeklyDayTextView.setText(tempData.get(position).dayOfWeek);

        }

        @Override
        public int getItemCount() {
            return tempData.size();
        }
    }

    @Override
    public void onUnitsChange(String unit) {
        adapter = new dayAdapter();
        recyclerView.setAdapter(adapter);
    }

    private String getTemperatureUnits(){
        SharedPreferences sharedPreferences =
                getActivity().getSharedPreferences("com.apps.hesham.weatherforecastapp"
                        , getActivity().MODE_PRIVATE);
        return sharedPreferences.getString("DefaultUnits", "");
    }

}
