package com.example.weather.ui

import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.ui.viewmodels.MainViewModel
import com.example.weather.ui.viewmodels.MainViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val adapter by lazy {
        WeatherAdapter()
    }
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted){
                viewModel.getScreen(true)
            }else{
                Toast.makeText(this, "Для работы приложения разрешите доступ к геолокации", Toast.LENGTH_LONG).show()
            }
        }

    private val today by lazy {
        val calendar = Calendar.getInstance()
        val month = (calendar.get(Calendar.MONTH)+1).toString()
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        "${day.padStart(2,'0')}.${month.padStart(2,'0')}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val llm = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.weatherRecycler.adapter = adapter
        binding.weatherRecycler.layoutManager = llm

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect{
                    binding.refresh.isRefreshing = false
                    when(it){
                        is UiState.Error -> {
                            binding.layout.visibility = View.GONE
                            binding.loading.visibility = View.VISIBLE
                            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                        }
                        UiState.Loading -> {
                            binding.layout.visibility = View.GONE
                            binding.loading.visibility = View.VISIBLE
                        }
                        is UiState.Success -> {
                            binding.layout.visibility = View.VISIBLE
                            binding.loading.visibility = View.GONE
                            adapter.setForecast(it.forecast)
                            adapter.notifyDataSetChanged()
                            binding.rain.text = "${it.weather.main.humidity}%"
                            binding.hpa.text = "${it.weather.main.pressure}hpa"
                            binding.wind.text = "${it.weather.wind.speed}км/ч"
                            val type = it.weather.weather[0].main
                            binding.weatherIcon.setImageResource(if (type=="Clear"){
                                R.drawable.clear
                            }else if(type=="Rain"){
                                R.drawable.rain
                            }else if(type=="Snow"){
                                R.drawable.snow
                            }else if (type=="Clouds"){
                                R.drawable.cloud
                            }else if (type=="Drizzle"){
                                R.drawable.drizzle
                            }else{
                                R.drawable.thunder
                            })
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = it.weather.dt*1000
                            binding.date.text = "${calendar.get(Calendar.HOUR_OF_DAY)}:${Calendar.MINUTE}"
                            binding.weatherTemp.text ="${String.format("%.1f", it.weather.main.temp)}°C"
                            binding.weatherType.text = it.weather.weather[0].description
                        }
                    }
                }
            }
        }
        binding.search.setOnEditorActionListener { v, actionId, event ->
            viewModel.getCityWeather(binding.search.text.toString())
            true
        }
        binding.searchIcon.setOnClickListener{
            viewModel.getCityWeather(binding.search.text.toString())
        }
        binding.weatherRecycler.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val time = adapter.forecast.list[llm.findFirstCompletelyVisibleItemPosition()].dt
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = time*1000
                val month = (calendar.get(Calendar.MONTH)+1).toString()
                val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
                val date = "${day.padStart(2,'0')}.${month.padStart(2,'0')}"
                if (date == today){
                    binding.day.text="Сегодня"
                }else{
                    binding.day.text=date
                }
            }
        })

        binding.refresh.setOnRefreshListener{
            viewModel.getScreen(false)
            llm.scrollToPosition(0)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            viewModel.getScreen(true)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}