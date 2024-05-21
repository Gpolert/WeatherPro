package com.example.weather.data.response;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Forecast {
    @PrimaryKey
    public Integer id = 0;
    public List<Weather> list;

    public Forecast(int id, List<Weather> list){
        this.id = id;
        this.list = list;
    }
}
