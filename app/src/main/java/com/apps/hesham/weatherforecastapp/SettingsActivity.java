package com.apps.hesham.weatherforecastapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by Hesham on 25/06/2017.
 */

public class SettingsActivity extends AppCompatActivity {


    private RadioGroup tempUnitsRadioGroup;
    private RadioButton celsiusRadioButton;
    private RadioButton fahrenheitRadioButton;
    private RadioButton kelvinRadioButton;
    private Button locationEnabledButton;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            SharedPreferences sharedPreferences =
                    getSharedPreferences("com.apps.hesham.weatherforecastapp", MODE_PRIVATE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                sharedPreferences.edit().putBoolean("locationEnabled",true).apply();
                locationEnabledButton.setEnabled(false);


            } else{

                sharedPreferences.edit().putBoolean("locationEnabled",false).apply();
            }

        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        celsiusRadioButton = (RadioButton)findViewById(R.id.celsiusRadioButton);
        fahrenheitRadioButton = (RadioButton)findViewById(R.id.fahrenheitRadioButton);
        kelvinRadioButton = (RadioButton)findViewById(R.id.kelvinRadioButton);
        tempUnitsRadioGroup = (RadioGroup) findViewById(R.id.tempUnitsRadioGroup);
        locationEnabledButton = (Button)findViewById(R.id.locationEnabledButton);

        String units = getPermenantStorage();
        if (units .equals("Celsius"))
            celsiusRadioButton.setChecked(true);
        else if (units.equals("Fahrenheit"))
            fahrenheitRadioButton.setChecked(true);
        else if (units.equals("Kelvin"))
            kelvinRadioButton.setChecked(true);


        SharedPreferences sharedPreferences =
                getSharedPreferences("com.apps.hesham.weatherforecastapp", MODE_PRIVATE);
        Boolean locationEnabled = sharedPreferences.getBoolean("locationEnabled", false);
        if (locationEnabled) {
            locationEnabledButton.setEnabled(false);
        } else {
            locationEnabledButton.setEnabled(true);
        }

        locationEnabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        });

        tempUnitsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                String unit="";
                if (checkedId == R.id.celsiusRadioButton)
                    unit = celsiusRadioButton.getText().toString();
                else if (checkedId == R.id.fahrenheitRadioButton)
                    unit = fahrenheitRadioButton.getText().toString();
                else if (checkedId == R.id.kelvinRadioButton)
                    unit = kelvinRadioButton.getText().toString();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                saveToPermenantStorage(unit);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default :return super.onOptionsItemSelected(item);
        }
    }

    private void saveToPermenantStorage(String defUnits){
        SharedPreferences sharedPreferences =
                getSharedPreferences("com.apps.hesham.weatherforecastapp", MODE_PRIVATE);
        sharedPreferences.edit().putString("DefaultUnits",defUnits).apply();
    }

    private String getPermenantStorage(){
        SharedPreferences sharedPreferences =
                getSharedPreferences("com.apps.hesham.weatherforecastapp", MODE_PRIVATE);
        return sharedPreferences.getString("DefaultUnits", "");
    }


}
