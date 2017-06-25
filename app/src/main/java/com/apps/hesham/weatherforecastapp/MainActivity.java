package com.apps.hesham.weatherforecastapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TemperatureFinderListener {


    public ProgressDialog progressDialog;

    private Button locationButton;
    private EditText locationEditText;
    private Button searchTemperatureButton;

    private FragmentViewListener dailyListener;
    private FragmentViewListener weeklyListener;

    LocationManager locationManager;
    LocationListener locationListener;

    private double Latitude;
    private double Longitude;

    private DailyFragment mDailyFragment;
    private WeeklyFragment mWeeklyFragment;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private LinearLayout mLinearLayout;

    private static final int REQUEST_CODE_SETTINGS = 1;
    private String temperatureUnit = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            SharedPreferences sharedPreferences =
                    getSharedPreferences("com.apps.hesham.weatherforecastapp"
                            , MODE_PRIVATE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                sharedPreferences.edit().putBoolean("locationEnabled", true).apply();

            } else {

                sharedPreferences.edit().putBoolean("locationEnabled", false).apply();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SETTINGS) {

                temperatureUnit = getTemperatureUnits();
                dailyListener.onUnitsChange(temperatureUnit);
                weeklyListener.onUnitsChange(temperatureUnit);

            }
        }
    }

    private String getTemperatureUnits() {
        SharedPreferences sharedPreferences =
                getSharedPreferences("com.apps.hesham.weatherforecastapp"
                        , MODE_PRIVATE);
        if (sharedPreferences.getBoolean("locationEnabled", false)){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                if (locationManager != null)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        return sharedPreferences.getString("DefaultUnits", "");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
                return true;
            case R.id.action_refresh:
                new TemperatureFinder(MainActivity.this, Latitude, Longitude).Execute();
                progressDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#795548")));
        temperatureUnit = getTemperatureUnits();

        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            SharedPreferences sharedPreferences =
                    getSharedPreferences("com.apps.hesham.weatherforecastapp"
                            , MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("locationEnabled", true).apply();

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");

        locationButton = (Button) findViewById(R.id.locationButton);
        locationEditText = (EditText) findViewById(R.id.locationEditText);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location loc = null;
                locationEditText.setText(null);
                try {
                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                } catch (SecurityException e) {
                    Toast.makeText(MainActivity.this, "is GPS enabled in Settings?", Toast.LENGTH_SHORT).show();

                }

                if (loc != null) {
                    Latitude = loc.getLatitude();
                    Longitude = loc.getLongitude();
                    new TemperatureFinder(MainActivity.this, Latitude, Longitude).Execute();
                    progressDialog.show();
                }


            }
        });


        searchTemperatureButton = (Button) findViewById(R.id.findTempButton);
        searchTemperatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Hide keypad after pressing Search Icon
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);


                if (locationEditText.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter location", Toast.LENGTH_SHORT).show();
                    return;
                }
                Geocoder coder = new Geocoder(MainActivity.this);
                List<Address> address;
                try {
                    address = coder.getFromLocationName(locationEditText.getText().toString(), 1);
                    if (address != null && address.size() > 0) {
                        Latitude = address.get(0).getLatitude();
                        Longitude = address.get(0).getLongitude();
                        new TemperatureFinder(MainActivity.this, Latitude, Longitude).Execute();
                        progressDialog.show();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter a valid location!", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    mDailyFragment = new DailyFragment();
                    dailyListener = mDailyFragment;
                    return mDailyFragment;
                } else {
                    mWeeklyFragment = new WeeklyFragment();
                    weeklyListener = mWeeklyFragment;
                    return mWeeklyFragment;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return "DAILY";
                } else
                    return "WEEKLY";
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        //Handling screen orientation changes
        if (TemperatureFinder.sTemperatureData != null && TemperatureFinder.sTemperatureData.size() > 0) {
            tabLayout.setBackgroundColor(Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).code).second));
            mLinearLayout.setBackgroundColor(Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).code).second));
            getSupportActionBar().setBackgroundDrawable
                    (new ColorDrawable(Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).code).second)));

        }

        //Retrieving state
        if (savedInstanceState != null) {
            mDailyFragment = (DailyFragment) getSupportFragmentManager().getFragment(savedInstanceState, "dailyFragment");
            mWeeklyFragment = (WeeklyFragment) getSupportFragmentManager().getFragment(savedInstanceState, "weeklyFragment");
            dailyListener = mDailyFragment;
            weeklyListener = mWeeklyFragment;
            temperatureUnit = savedInstanceState.getString("TempUnit");
            Latitude = savedInstanceState.getDouble("Lat");
            Longitude = savedInstanceState.getDouble("Lng");
        }
    }


    //Saving state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "dailyFragment", mDailyFragment);
        getSupportFragmentManager().putFragment(outState, "weeklyFragment", mWeeklyFragment);
        outState.putString("TempUnit", temperatureUnit);
        outState.putDouble("Lat", Latitude);
        outState.putDouble("Lng", Longitude);
    }


    @Override
    public void onSuccess(String jsonCityName) {

        String locationName = locationEditText.getText().toString();
        if (locationName == null || locationName.equals("")) //if the user pressed GPS button
            locationName = jsonCityName;

        dailyListener.editViewsSuccess(locationName);
        dailyListener.onUnitsChange(temperatureUnit);
        weeklyListener.editViewsSuccess(locationName);
        weeklyListener.onUnitsChange(temperatureUnit);

        tabLayout.setBackgroundColor(Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).code).second));
        mLinearLayout.setBackgroundColor(Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).code).second));
        getSupportActionBar().setBackgroundDrawable
                (new ColorDrawable(Color.parseColor(TemperatureFinder.codeColors.get(TemperatureFinder.sTemperatureData.get(0).code).second)));

        progressDialog.hide();
    }

    @Override
    public void onFailure() {
        progressDialog.hide();
        Toast.makeText(this, "Weather service not available", Toast.LENGTH_SHORT).show();

    }
}
