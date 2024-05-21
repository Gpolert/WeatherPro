package com.example.weather.data.response;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Weather {
    @PrimaryKey
    public Integer id;
    public List<WeatherDto> weather;
    @Embedded
    public MainDto main;
    @Embedded
    public WindDto wind;
    public Long dt;

    public Weather(Integer id, List<WeatherDto> weather, MainDto main, WindDto wind, Long dt) {
        this.id = id;
        this.weather = weather;
        this.main = main;
        this.wind = wind;
        this.dt = dt;
    }
}
