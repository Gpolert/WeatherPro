package com.example.weather.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.weather.data.api.WeatherApi
import com.example.weather.data.dao.Database
import com.example.weather.ui.UiState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel(
    context: Context
) : ViewModel() {

    private val api by lazy {
        val retrofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create()).build()
        retrofit.create(WeatherApi::class.java)
    }
    private var isDbLoaded = false
    private val db by lazy {
        Room.databaseBuilder(context, Database::class.java, "Weather").build().dao()
    }

    private val location by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)

    @SuppressLint("MissingPermission")
    fun getScreen(isFirstOpen: Boolean) {
        if (isFirstOpen && uiState.value!=UiState.Loading){
            return
        }
        viewModelScope.launch(
            context = CoroutineExceptionHandler { coroutineContext: CoroutineContext, throwable: Throwable ->
                Log.d("qwerty", throwable.message.toString())
                if(isFirstOpen) return@CoroutineExceptionHandler
                viewModelScope.launch {
                    uiState.emit(UiState.Error("Ошибка загрузки данных"))
                    loadDB()
                }
            }) {
            uiState.emit(UiState.Loading)
            if (isFirstOpen) {
                loadDB()
            }
            withContext(Dispatchers.IO) {
                val cont = suspendCoroutine<Location> { cont ->
                    location.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener {
                        cont.resume(it)
                    }
                }
                Log.d("qwerty","${cont.latitude} ${cont.longitude}")
                val weather =
                    async {api.getWeather(
                        cont.latitude,
                        cont.longitude,
                        WeatherApi.APIKEY,
                        WeatherApi.metric,
                        WeatherApi.lang,
                    ).execute()}
                val forecast =
                    async {api.getForecast(
                        cont.latitude,
                        cont.longitude,
                        WeatherApi.APIKEY,
                        WeatherApi.metric,
                        WeatherApi.lang,
                    ).execute()}
                uiState.emit(
                    UiState.Success(
                        weather.await().body()!!,
                        forecast.await().body()!!,
                    )
                )
                db.insertWeather(weather.await().body()!!)
                db.insertForecast(forecast.await().body()!!)
            }
        }
    }

    private suspend fun loadDB() {
        try {
            withContext(Dispatchers.IO) {
                Log.d("qwerty","db")
                val weather = db.getWeather()
                val forecast = db.getForevast()
                uiState.emit(
                    UiState.Success(
                        weather,
                        forecast,
                    )
                )

                Log.d("qwerty","loaded")
                isDbLoaded = true
            }

        } catch (e: Exception) {
            Log.e("qwerty", e.message.toString())
        }
    }


    fun getCityWeather(city: String) {
        viewModelScope.launch(
            context = CoroutineExceptionHandler { coroutineContext: CoroutineContext, throwable: Throwable ->
                viewModelScope.launch {
                    uiState.emit(UiState.Error("Ошибка загрузки данных"))
                    loadDB()
                }
            }) {
            uiState.emit(UiState.Loading)
            withContext(Dispatchers.IO) {
                val weather = async { api.getWeather(city,WeatherApi.APIKEY, WeatherApi.metric, WeatherApi.lang,).execute() }
                val forecast = async { api.getForecast(city, WeatherApi.APIKEY, WeatherApi.metric, WeatherApi.lang,).execute() }
                val ui =  UiState.Success(
                    weather.await().body()!!,
                    forecast.await().body()!!,
                )
                uiState.emit(UiState.Error("Погода в ${city}"))
                uiState.emit(ui)

            }
        }
    }
}
