package com.example.weather.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.data.response.Forecast
import com.example.weather.data.response.Weather

@Dao
interface Dao {

    @Query("SELECT * FROM weather")
    suspend fun getWeather(): Weather

    @Query("SELECT * FROM forecast")
    suspend fun getForevast(): Forecast

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Weather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: Forecast)

}