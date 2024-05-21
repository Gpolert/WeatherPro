package com.example.weather.data.response;

public class MainDto {
    public Double temp;
    public Integer pressure;
    public Integer humidity;

    public MainDto(Double temp, Integer pressure, Integer humidity){
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
    }
}
