package com.example.weather.data.response;

public class WeatherDto {
    public String main;
    public String description;
    public String icon;

    public WeatherDto(String main, String description, String icon) {
        this.main = main;
        this.description = description;
        this.icon = icon;
    }
}
