package com.example.weather.ui

import com.example.weather.data.response.Forecast
import com.example.weather.data.response.Weather

sealed interface UiState {


    class Success(
        val weather: Weather,
        val forecast: Forecast,
    ): UiState

    class Error(
        val message: String,
    ): UiState

    data object Loading: UiState

}