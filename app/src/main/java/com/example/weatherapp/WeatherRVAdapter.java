package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModal> weatherList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModal> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weathr_rv_item,parent,false);
        return new ViewHolder(view);
    }
    private String convertTimestampToTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        return sdf.format(date);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModal modal = weatherList.get(position);
        String s = "https://openweathermap.org/img/wn/".concat(modal.getIcon() + "@2x.png");
        long timestamp = modal.getTime() * 1000;
        String formattedTime = convertTimestampToTime(timestamp);
        holder.time.setText(formattedTime);
        holder.temp.setText(modal.getTemperature()+ "°C");
        Picasso.get().load(s).into(holder.condition);
        holder.wind.setText(modal.getWindspeed() + "Km/h");
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView wind,time,temp;
        private ImageView condition;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wind = itemView.findViewById(R.id.idTVWindSpeed);
            time = itemView.findViewById(R.id.idTVWindSpeed);
            temp = itemView.findViewById(R.id.idTVTemp);
            condition = itemView.findViewById(R.id.idTVTime);
        }
    }
}
