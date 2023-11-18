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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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
                String url = "https://api.openweathermap.org/data/3.0/onecall?lat="+String.valueOf(coords[0])+ "&lon="+String.valueOf(coords[0])+"&exclude=minutely,alerts&appid="+apiKey+"&units=metric";
                cityNameTV.setText(cityName);

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingPB.setVisibility(View.GONE);
                        homeRL.setVisibility(View.VISIBLE);
                        WeatherList.clear();
                        try {
                            String temp = response.getJSONObject("current").getString("temp");
                            tempTV.setText(temp+ "°C");
                            long dt =response.getJSONObject("current").getLong("dt");
                            long sunrise =response.getJSONObject("current").getLong("sunrise");
                            long sunset =response.getJSONObject("current").getLong("sunset");
                            String iconCode = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("icon");
                            String condition = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("main");
                            conditionTV.setText(condition);
                            Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png").into(iconIV);
                            boolean isDay;
                            if(dt-sunrise>0 && dt-sunset<0)
                            {
                                Picasso.get().load("https://images.unsplash.com/photo-1603883055407-968560f7522e?q=80&w=1901&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D").into(backIV);
                            }
                            else
                            {
                                Picasso.get().load("https://images.unsplash.com/photo-1507502707541-f369a3b18502?q=80&w=1976&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D").into(backIV);
                            }
                            
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Please enter valid city name",Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);

            }
        }
        catch (IOException e){
            Log.e("GPS error", "Error getting coordinates: " + e.getMessage());
        }

    }
}