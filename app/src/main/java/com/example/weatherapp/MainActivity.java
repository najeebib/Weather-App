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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private TextView cityNameTV,tempTV,conditionTV;
    private TextInputEditText cityEdt;
    private ImageView backIV,iconIV,searchIV;
    private RecyclerView weatherRV;
    private ArrayList<WeatherRVModal> WeatherList;
    private WeatherRVAdapter WeatherAdapter;
    private int location_permission = 1;
    private String CityName;
    private FusedLocationProviderClient fusedLocationClient;
    private ProgressBar loadingPB;
    private RecyclerView weekRV;
    private ArrayList<WeekForcastModal> forcastList;
    private WeekForcastAdapter ForcastAdapter;

    // This function initializes UI components, requests location permissions, retrieves location, and sets up event listeners.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //RelativeLayout for the main UI.
        homeRL = findViewById(R.id.idRLHome);
        //TextView displaying the city name.
        cityNameTV = findViewById(R.id.idTVCityName);
        //TextView showing the temperature.
        tempTV = findViewById(R.id.idtemp);
        //TextView displaying the weather condition.
        //TextInputEditText for entering a city name.
        conditionTV = findViewById(R.id.idTVCondition); 
        cityEdt = findViewById(R.id.idEdtCity);
        //iconIV: ImageViews for background and weather icons.
        backIV = findViewById(R.id.idBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idSearch);
        //RecyclerView for hourly weather data.
        weatherRV = findViewById(R.id.idRVWeather);
        //ArrayList for storing hourly weather data.
        WeatherList  = new ArrayList<WeatherRVModal>();
        //Adapter for hourly weather RecyclerView.
        WeatherAdapter = new WeatherRVAdapter(this,WeatherList);
        weatherRV.setAdapter(WeatherAdapter);
        //ProgressBar for loading indication.
        loadingPB = findViewById(R.id.idLoading);
        //RecyclerView for 5 day forecast.
        weekRV = findViewById(R.id.idRVWeekForcast);
        //ArrayList for storing weekly forecast data.
        forcastList = new ArrayList<WeekForcastModal>();
        //Adapter for weekly forecast RecyclerView.
        ForcastAdapter = new WeekForcastAdapter(this,forcastList);
        weekRV.setAdapter(ForcastAdapter);
        //FusedLocationProviderClient for location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // check for user permissions
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},location_permission);
        }
        // get the location of the suer
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // show the weather data for user's current location
                            getWeatherInfo(location.getLatitude(),location.getLongitude());
                            getForcast(location.getLatitude(),location.getLongitude());
                        }
                    }
                });
        //Users can enter a city name in the cityEdt field and click the search button (searchIV) to fetch weather information for that city.
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
                    getForcast(city);

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


    //this function retrieves the weather information, parses it and displays it on screen
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
                String apiKey =  getResources().getString(R.string.api_endpoint);
                String url = "https://api.openweathermap.org/data/3.0/onecall?lat="+ coords[0] + "&lon="+ coords[0] +"&exclude=minutely,alerts&appid="+apiKey+"&units=metric";
                cityNameTV.setText(cityName);
                Toast.makeText(MainActivity.this,"Url: " +url+ ", "+ apiKey,Toast.LENGTH_SHORT).show();
                Log.d("tag",url + "," + apiKey);
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        WeatherList.clear();
                        try {
                            String temp = String.valueOf(response.getJSONObject("current").getInt("temp"));
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
                            JSONArray hours = response.optJSONArray("hourly");
                            if (hours != null) {
                                int arrayLength = Math.min(hours.length(), 24); // Get the minimum of 24 or the array length
                                for (int i = 0; i < arrayLength; i++) {
                                    JSONObject hourObj = hours.optJSONObject(i);
                                    if (hourObj != null) {
                                        String hourTemp = String.valueOf(hourObj.optInt("temp"));
                                        String hourWindSpeed = hourObj.optString("wind_speed");
                                        String hourIcon = hourObj.getJSONArray("weather").getJSONObject(0).getString("icon");
                                        Toast.makeText(MainActivity.this,"icon code:" + hourIcon,Toast.LENGTH_SHORT).show();
                                        Long hourTime = hourObj.optLong("dt");
                                        WeatherRVModal m = new WeatherRVModal(hourTime,hourTemp,hourIcon,hourWindSpeed);
                                        WeatherList.add(m);
                                    }
                                }
                                WeatherAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("TAG", "No 'hourly' array found in JSON response");
                            }
                            loadingPB.setVisibility(View.GONE);
                            homeRL.setVisibility(View.VISIBLE);
                            
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this,"Please enter valid city name",Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);

            }
        }
        catch (IOException e){
            Log.e("GPS error", "Error getting coordinates: " + e.getMessage());
        }

    }
    ///this function retrieves the weather information, parses it and displays it on screen
    private void getWeatherInfo(double latitude,double longitude)
    {

        double[] coords = new double[]{latitude, longitude};
        String apiKey =  getResources().getString(R.string.api_endpoint);
        String url = "https://api.openweathermap.org/data/3.0/onecall?lat="+ coords[0] + "&lon="+ coords[0] +"&exclude=minutely,alerts&appid="+apiKey+"&units=metric";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        cityNameTV.setText("");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                WeatherList.clear();
                try {
                    String temp =  String.valueOf(response.getJSONObject("current").getInt("temp"));
                    tempTV.setText(temp+ "°C");
                    long dt =response.getJSONObject("current").getLong("dt");
                    long sunrise =response.getJSONObject("current").getLong("sunrise");
                    long sunset =response.getJSONObject("current").getLong("sunset");
                    String iconCode = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("icon");
                    String condition = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description");
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
                    JSONArray hours = response.optJSONArray("hourly");
                    if (hours != null) {
                        int arrayLength = Math.min(hours.length(), 24); // Get the minimum of 24 or the array length
                        for (int i = 0; i < arrayLength; i++) {
                            JSONObject hourObj = hours.optJSONObject(i);
                            if (hourObj != null) {
                                String hourTemp = String.valueOf(hourObj.optInt("temp"));
                                String hourWindSpeed = hourObj.optString("wind_speed");
                                String hourIcon = hourObj.getJSONArray("weather").getJSONObject(0).getString("icon");
                                Toast.makeText(MainActivity.this,"icon code:" + hourIcon,Toast.LENGTH_SHORT).show();
                                Long hourTime = hourObj.optLong("dt");
                                WeatherRVModal m = new WeatherRVModal(hourTime,hourTemp,hourIcon,hourWindSpeed);
                                WeatherList.add(m);
                            }
                        }
                        WeatherAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "No 'hourly' array found in JSON response");
                    }

                    loadingPB.setVisibility(View.GONE);
                    homeRL.setVisibility(View.VISIBLE);

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
    //this function converts time in epoch format to datetime and returns the day of the week
    public static String dtToDay(long epochSeconds) {
        Date date = new Date(epochSeconds * 1000); // Convert epoch to milliseconds
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String dayName;
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                dayName = "Sunday";
                break;
            case Calendar.MONDAY:
                dayName = "Monday";
                break;
            case Calendar.TUESDAY:
                dayName = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayName = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayName = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayName = "Friday";
                break;
            case Calendar.SATURDAY:
                dayName = "Saturday";
                break;
            default:
                dayName = "Invalid day";
                break;
        }

        return dayName;
    }
    //This function retrieves the 5 day forecast and displays it on screen
    private void getForcast(String cityName)
    {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocationName(cityName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();

                double[] coords = new double[]{latitude, longitude};
                String apiKey =  getResources().getString(R.string.api_endpoint);
                String url = "https://api.openweathermap.org/data/2.5/forecast?lat="+ coords[0] + "&lon="+ coords[0] +"&appid="+apiKey+"&units=metric";
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        forcastList.clear();
                        try {

                            JSONArray hours = response.optJSONArray("list");
                            if (hours != null) {
                                for (int i = 0; i < hours.length(); i+=8) {
                                    JSONObject hourObj = hours.optJSONObject(i);
                                    if (hourObj != null) {
                                        String min = String.valueOf(hourObj.getJSONObject("main").optInt("temp_min"));
                                        String max = String.valueOf(hourObj.getJSONObject("main").optInt("temp_max"));
                                        String hourIcon = hourObj.getJSONArray("weather").getJSONObject(0).getString("icon");
                                        Long hourTime = hourObj.optLong("dt");
                                        String Day =  dtToDay(hourTime);
                                        WeekForcastModal modal = new WeekForcastModal(min,max,Day,hourIcon);
                                        forcastList.add(modal);
                                    }
                                }
                                ForcastAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("TAG", "No 'hourly' array found in JSON response");
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this,"Please enter valid city name",Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);

            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //This function retrieves the 5 day forecast and displays it on screen
    private void getForcast(double latitude,double longitude)
    {
        double[] coords = new double[]{latitude, longitude};
        String apiKey =  getResources().getString(R.string.api_endpoint);
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat="+ coords[0] + "&lon="+ coords[0] +"&appid="+apiKey+"&units=metric";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                forcastList.clear();
                try {

                    JSONArray hours = response.optJSONArray("list");
                    if (hours != null) {
                        for (int i = 0; i < hours.length(); i+=8) {
                            JSONObject hourObj = hours.optJSONObject(i);
                            if (hourObj != null) {
                                String min = String.valueOf(hourObj.getJSONObject("main").optInt("temp_min"));
                                String max = String.valueOf(hourObj.getJSONObject("main").optInt("temp_max"));
                                String hourIcon = hourObj.getJSONArray("weather").getJSONObject(0).getString("icon");
                                Long hourTime = hourObj.optLong("dt");
                                String Day =  dtToDay(hourTime);
                                WeekForcastModal modal = new WeekForcastModal(min,max,Day,hourIcon);
                                forcastList.add(modal);
                            }
                        }
                        ForcastAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("TAG", "No 'hourly' array found in JSON response");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this,"Please enter valid city name",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}