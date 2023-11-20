package com.example.weatherapp;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WeekForcastAdapter extends RecyclerView.Adapter<WeekForcastAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeekForcastModal> ForcastList;

    public WeekForcastAdapter(Context context, ArrayList<WeekForcastModal> weatherList) {
        this.context = context;
        this.ForcastList = weatherList;
    }
    @NonNull
    @Override
    public WeekForcastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forcast_list_item,parent,false);
        return new WeekForcastAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull WeekForcastAdapter.ViewHolder holder, int position) {
        WeekForcastModal modal = ForcastList.get(position);
        holder.High.setText(modal.getHigh());
        holder.Low.setText(modal.getLow());
        holder.Day.setText(modal.getDay());
        Picasso.get().load("https://openweathermap.org/img/wn/" + modal.getIcon() + "@2x.png").into(holder.Icon);
    }
    @Override
    public int getItemCount() {
        return ForcastList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView High,Low,Day;
        private ImageView Icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            High = itemView.findViewById(R.id.idHigh);
            Low = itemView.findViewById(R.id.idLow);
            Day = itemView.findViewById(R.id.idDay);
            Icon = itemView.findViewById(R.id.idIcon);
        }
    }
}
