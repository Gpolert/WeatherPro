package com.example.weather.data.api;

import com.example.weather.data.response.Forecast;
import com.example.weather.data.response.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("data/2.5/forecast")
    Call<Forecast> getForecast(@Query("q")String city, @Query("appid")String apikey, @Query("units")String metric, @Query("lang")String lang);
    @GET("data/2.5/forecast")
    Call<Forecast>  getForecast(@Query("lat")Double lat, @Query("lon")Double lon,@Query("appid")String apikey, @Query("units")String metric, @Query("lang")String lang);
    @GET("data/2.5/weather")
    Call<Weather> getWeather(@Query("q")String city, @Query("appid")String apikey, @Query("units")String metric, @Query("lang")String lang);
    @GET("data/2.5/weather")
    Call<Weather> getWeather(@Query("lat")Double lat, @Query("lon")Double lon,@Query("appid")String apikey, @Query("units")String metric, @Query("lang")String lang);

    public static String APIKEY = "dc8ecec968c5f0fa2162b50eb1cce678";
    public static String metric = "metric";
    public static String lang = "ru";
}
