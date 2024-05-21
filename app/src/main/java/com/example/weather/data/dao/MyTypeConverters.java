package com.example.weather.data.dao;

import androidx.room.TypeConverter;


import com.example.weather.data.response.Weather;
import com.example.weather.data.response.WeatherDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MyTypeConverters {

    @TypeConverter
    public static String fromWeatherListToJson(List<Weather> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<Weather> fromJsonToWeatherList(String json) {
        Type type = new TypeToken<List<Weather>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    @TypeConverter
    public static String fromWeatherDtoListToJson(List<WeatherDto> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<WeatherDto> fromJsonToWeatherDtoList(String json) {
        Type type = new TypeToken<List<WeatherDto>>() {}.getType();
        return new Gson().fromJson(json, type);
    }
}
