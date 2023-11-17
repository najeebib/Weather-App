package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.location.Address;
import android.location.Geocoder;

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