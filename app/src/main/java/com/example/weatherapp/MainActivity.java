package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV,tempTV,conditionTV;
    private TextInputEditText cityEdt;
    private ImageView backIV,iconIV,searchIV;
    private RecyclerView weatherRV;
    private ArrayList<WeatherRVModal> WeatherList;
    private WeatherRVAdapter WeatherAdapter;
    private LocationManager locationManager;
    private int location_permission = 1;
    private String CityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        tempTV = findViewById(R.id.idtemp);
        conditionTV = findViewById(R.id.idTVCondition); 
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idSearch);
        weatherRV = findViewById(R.id.idRVWeather);
        WeatherList  = new ArrayList<>();
        WeatherAdapter = new WeatherRVAdapter(this,WeatherList);
        weatherRV.setAdapter(WeatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},location_permission);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        CityName = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(CityName);

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty())
                    Toast.makeText(MainActivity.this,"Enter city name",Toast.LENGTH_SHORT).show();
                else
                {
                    CityName = city;
                    getWeatherInfo(city);

                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==location_permission)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
            else
            {
                Toast.makeText(this,"Please provide permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double lon, double lat)
    {
        String name = "NotFound";
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(lat,lon,10);

            for(Address adr: addresses)
            {
                if(adr != null)
                {
                    String city = adr.getLocality();
                    if(city != null && !city.equals(""))
                        name = city;
                    else{
                        Log.d("tag","city not found");
                        Toast.makeText(this,"USer city not found",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    private void getWeatherInfo(String cityName)
    {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try{
            addresses = geocoder.getFromLocationName(cityName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                double[] coords = new double[]{latitude, longitude};
                String apiKey = System.getenv("OpenWeatherMapAppID");
                String request = "https://api.openweathermap.org/data/3.0/onecall?lat="+String.valueOf(coords[0])+ "&lon="+String.valueOf(coords[0])+"&exclude=minutely,alerts&appid="+apiKey+"&units=metric";


            }
        }
        catch (IOException e){
            Log.e("GPS error", "Error getting coordinates: " + e.getMessage());
        }

    }
}