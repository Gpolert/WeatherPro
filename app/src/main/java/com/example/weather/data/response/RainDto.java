package com.example.weather.data.response;

public class RainDto {
    Double rainChance;
    Double rain1h;

    public RainDto(Double rainChance, Double rain1h) {
        this.rainChance = rainChance;
        this.rain1h = rain1h;
    }
}
