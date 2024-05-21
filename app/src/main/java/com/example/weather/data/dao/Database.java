package com.example.weather.data.dao;

import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.weather.data.response.Forecast;
import com.example.weather.data.response.Weather;

@androidx.room.Database(
        entities = {Weather.class, Forecast.class},
        version = 1
)
@TypeConverters({MyTypeConverters.class})
public abstract class Database extends RoomDatabase {
    public abstract Dao dao();
}
