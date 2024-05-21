package com.example.weather.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.weather.R;
import com.example.weather.data.response.Forecast;
import com.example.weather.data.response.Weather;
import com.example.weather.databinding.WeatherItemBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private Forecast forecast = new Forecast(0, new ArrayList<>());

    public Forecast getForecast() {
        return forecast;
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        public final TextView time;
        public final ImageView icon;
        public final TextView temp;

        public WeatherViewHolder(WeatherItemBinding binding) {
            super(binding.getRoot());
            time = binding.time;
            icon = binding.icon;
            temp = binding.temperature;
        }
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeatherViewHolder(WeatherItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public int getItemCount() {
        return forecast.list.size();
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        Weather data = forecast.list.get(position);
        String type = data.weather.get(0).main;
        if ("Clear".equals(type)) {
            holder.icon.setImageResource(R.drawable.clear);
        } else if ("Rain".equals(type)) {
            holder.icon.setImageResource(R.drawable.rain);
        } else if ("Snow".equals(type)) {
            holder.icon.setImageResource(R.drawable.snow);
        } else if ("Clouds".equals(type)) {
            holder.icon.setImageResource(R.drawable.cloud);
        } else if ("Drizzle".equals(type)) {
            holder.icon.setImageResource(R.drawable.drizzle);
        } else {
            holder.icon.setImageResource(R.drawable.thunder);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(data.dt * 1000L);

        holder.time.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        holder.temp.setText(String.format("%.1f°C", data.main.temp));
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
        notifyDataSetChanged();
    }
}