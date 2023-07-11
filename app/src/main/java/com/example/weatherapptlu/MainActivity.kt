package com.example.weatherapptlu
import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapptlu.api.RetrofitClient
import com.example.weatherapptlu.databinding.ActivityMainBinding
import com.example.weatherapptlu.model.Advice
import com.example.weatherapptlu.model.Card.AirCard
import com.example.weatherapptlu.model.Card.CardDetail
import com.example.weatherapptlu.model.Card.CardTime
import com.example.weatherapptlu.model.Forecast.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.Normalizer
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var binding: ActivityMainBinding
    var listCardTime: List<CardTime> = listOf()
    var listDetail: List<CardDetail> = listOf()
    var listAir: List<AirCard> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLocaion()

        binding.searchTp.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                testApi(removeUnicode(query))

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Handle the query text changes here
                return true
            }
        })
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY >= 100) {
                if (binding.cityName.text != binding.dfCityName.text) {
                    binding.cityName.text = binding.dfCityName.text
                    binding.toolbar.setBackgroundResource(R.drawable.bd_bottom)
                }
            }
            if (scrollY == 0) {
                if (binding.cityName.text == binding.dfCityName.text) {
                    binding.cityName.text = ""
                    binding.toolbar.setBackgroundResource(0)
                }

            }
        }

    }

    private fun getLocaion() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001
            )

        }
        else{
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    try {
                        val addresses: List<Address> = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        ) as List<Address>
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            callForecastApi(removeUnicode(address.adminArea))


                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocaion()
            }
        }
    }

    private fun testApi(city: String) {
        RetrofitClient.forecastWeatherApi.getForecast(city)
            .enqueue(object : Callback<ForecastWeather> {
                override fun onResponse(
                    call: Call<ForecastWeather>,
                    response: Response<ForecastWeather>
                ) {
                    if (response.isSuccessful) {
                        val forecastData = response.body()
                        if (forecastData != null) {
                            val cityRes = forecastData.location.name

                            binding.dfCityName.text = "$cityRes"
                            val searchView=binding.searchTp
                            searchView.setQuery("", false) // Xóa nội dung hiện tại của SearchView
                            searchView.clearFocus() // Xóa trạng thái tập trung của SearchView
                            searchView.isIconified = true // Đóng SearchView bằng cách thu nhỏ nó lại
                            searchView.onActionViewCollapsed() // Đóng SearchView bằng cách thu gọn nó lại

                            Toast.makeText(
                                this@MainActivity,
                                "Đã chuyển vị trí sang $cityRes",
                                Toast.LENGTH_LONG
                            ).show()

                            handleViewFromData(forecastData)
                            binding.searchTp.clearFocus()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Không có dữ liệu. Hãy thử lại",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Không tồn tại thành phố. Hãy thử lại",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ForecastWeather>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Có lỗi, xảy ra.", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun callForecastApi(city: String) {
        binding.dfCityName.text=city
        Toast.makeText(this@MainActivity, "Bạn đang ở $city", Toast.LENGTH_LONG).show()
        RetrofitClient.forecastWeatherApi.getForecast(city)
            .enqueue(object : Callback<ForecastWeather> {
                override fun onResponse(
                    call: Call<ForecastWeather>,
                    response: Response<ForecastWeather>
                ) {
                    if (response.isSuccessful) {
                        val forecastData = response.body()
                        if (forecastData != null) {


                            handleViewFromData(forecastData)


                        }
                    }
                }

                override fun onFailure(call: Call<ForecastWeather>, t: Throwable) {
                    Log.d("MainActivity", "Request failed: ${t.message}")
                }
            })
    }
    fun removeUnicode(text: String): String {
        val normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD)
        val regex = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return regex.matcher(normalizedText).replaceAll("")
    }

    private fun anim() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val textWidth = binding.tvAirInfo.paint.measureText(binding.tvAirInfo.text.toString())

        val animator = ValueAnimator.ofFloat(screenWidth, -textWidth)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            binding.tvAirInfo.translationX = value
        }
        animator.duration = 10000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.start()

    }

    private fun astroInfo(astro: Astro) {
        binding.bmText.text = astro.sunrise
        binding.hhText.text = astro.sunset
    }

    private fun handleViewFromData(forecastData: ForecastWeather) {
        handleCurrentWeather(forecastData)
        astroInfo(forecastData.forecast.forecastday[0].astro)
        val queryCurrent=forecastData.current
        binding.mainApp.setBackgroundResource(backgroundGenerator(queryCurrent.condition.code,queryCurrent.last_updated))
        binding.forecastHours.layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.forecastDays.layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.forecastAir.layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        handleDataCardTime(forecastData)
        listCardTime[0].time = "Bây giờ"

        binding.forecastHours.adapter = WeatherAdapter(applicationContext, listCardTime)

        binding.forecastDays.adapter =
            Weather2Adapter(applicationContext, forecastData.forecast.forecastday)

        val ad = handleDataCardAir(forecastData.current.air_quality)
        binding.airTitle.text = ad.alert
        binding.airMess.text = ad.message
        binding.airTitle.setTextColor(Color.parseColor(ad.color))
        binding.forecastAir.adapter = AirIndexAdapter(applicationContext, listAir)

        binding.tvAirInfo.text =
            "Chất lượng không khí ${forecastData.current.air_quality.pm25.toInt()} - ${ad.alert}"
        anim()

        handleDataCardDetail(forecastData.current)
        binding.gridWeather.adapter = Weather3Adapter(applicationContext, listDetail)

        val weatherAdvice: WeatherAdvice =
            getWeatherAdvice(forecastData.forecast.forecastday[0].day.condition.code)
        binding.advice.text = weatherAdvice.advice
        binding.alert.text = weatherAdvice.alert
    }

    private fun handleDataCardTime(weather: ForecastWeather) {
        val currentTime = weather.location.localtime
        listCardTime = listOf()
        val curHour = if (currentTime.length == 15) {
            currentTime.substring(11, 12).toInt()
        } else {
            currentTime.substring(11, 13).toInt()
        }
        Log.d("curA", curHour.toString())
        for (hourIndex in curHour until 24) {

            val query = weather.forecast.forecastday[0]
            val forecast = query.hour[hourIndex]
            val time = forecast.time
            val formattedTime = if (time.length == 15) {
                "0$time.substring(11, 12)"
            } else {
                time.substring(11, 13)
            }
            var degCur = query.hour[hourIndex].temp_c.toString()
            if (hourIndex == curHour) {
                degCur = weather.current.temp_c.toString()
            }
            Log.d("curA$hourIndex", "$formattedTime:00")

            val cardTime = CardTime(
                time = "$formattedTime:00",
                icon_time = query.hour[hourIndex].condition.icon,
                deg = degCur
            )
            listCardTime = listCardTime.plus(cardTime)
        }

        if (curHour > 0) {
            for (hourIndex in 0 until curHour) {
                val query = weather.forecast.forecastday[1]
                val forecast = query.hour[hourIndex]
                val time = forecast.time
                val formattedTime = if (time.length == 15) {
                    "0$time.substring(11, 12)"
                } else {
                    time.substring(11, 13)
                }
                val cardTime = CardTime(
                    time = "$formattedTime:00",
                    icon_time = query.hour[hourIndex].condition.icon,
                    deg = query.hour[hourIndex].temp_c.toString()
                )
                listCardTime = listCardTime.plus(cardTime)
            }
        }
    }

    private fun handleDataCardDetail(current: Current) {
        val cardDeg =
            CardDetail("Nhiệt độ cảm nhận", R.drawable.ic_temp, "${current.feelslike_c}°", "")
        val cardWind = CardDetail(
            "${huongGio(current.wind_dir)}",
            R.drawable.ic_wind,
            "${current.wind_kph.toInt()}",
            "km/h"
        )
        val cardHum = CardDetail("Độ ẩm", R.drawable.ic_hum, "${current.humidity}", "%")
        val cardUv = CardDetail(
            "UV",
            R.drawable.ic_sun,
            "${current.uv.toInt()}",
            "${xacDinhMucDoUV(current.uv.toInt())}"
        )
        val cardView = CardDetail("Tầm nhìn", R.drawable.ic_eye, "${current.vis_km.toInt()}", "km")
        val cardPress = CardDetail(
            "Áp suất không khí",
            R.drawable.ic_press_air,
            "${current.pressure_mb.toInt()}",
            "hPa"
        )

        listDetail = listOf(cardDeg, cardWind, cardHum, cardUv, cardView, cardPress)
    }

    private fun handleDataCardAir(airQuality: AirQuality): Advice {
        val ad = generateAdvice(airQuality)
        val color = ad.color
        val cardCo = AirCard(String.format("%.1f", airQuality.co), "CO", color)
        val cardNo2 = AirCard(String.format("%.1f", airQuality.no2), "NO₂", color)
        val cardSo2 = AirCard(String.format("%.1f", airQuality.so2), "SO₂", color)
        val cardO3 = AirCard(String.format("%.1f", airQuality.o3), "O₃", color)
        val cardPm25 = AirCard(String.format("%.1f", airQuality.pm25), "PM₂.₅", color)
        val cardPm10 = AirCard(String.format("%.1f", airQuality.pm10), "PM₁₀", color)
        listAir = listOf(cardCo, cardNo2, cardO3, cardSo2, cardPm25, cardPm10)
        return ad
    }

    private fun generateAdvice(airQualityData: AirQuality): Advice {
        if (airQualityData.usEpaIndex == 6) {
            return Advice(
                alert = "Nguy hiểm",
                message = "Ra ngoài giờ này chỉ có đắp chiếu thôi.",
                color = "#ff0000"
            )
        }

        if (airQualityData.usEpaIndex == 5 || airQualityData.usEpaIndex == 4) {
            return Advice(
                alert = "Không lành mạnh",
                message = "Hạn chế ra ngoài và sử dụng khẩu trang.",
                color = "#ff7e00"
            )
        }

        if (airQualityData.usEpaIndex == 3) {
            return Advice(
                alert = "Trung bình",
                message = "Hạn chế ra ngoài trong thời gian dài với những người nhạy cảm",
                color = "#ffff00"
            )
        }

        if (airQualityData.usEpaIndex == 2) {
            return Advice(
                alert = "Tốt",
                message = "Thật là một dịp đặc biệt để ra ngoài với bạn bè và người thân",
                color = "#00e400"
            )
        }

        return Advice(
            alert = "Tuyệt vời",
            message = "Cùng người yêu bạn dạo quanh thành phố, tận hưởng không khí trong lành đi nào",
            color = "#00e400"
        )
    }
    private fun backgroundGenerator(code:Int,time:String):Int{
        return when (code){
            1000, 1003 ,1150, 1153 -> getDaytime(time)
            1063, 1066, 1069, 1072, 1198, 1201, 1240, 1243, 1246, 1273, 1276 -> R.drawable.rain
            1087 -> R.drawable.storm
            1009, 1030, 1222, 1237, 1249, 1255, 1261 -> R.drawable.dark
            else -> R.drawable.no_find
        }
    }
    fun getDaytime(currentTime:String): Int{
        val curHour = if (currentTime.length == 15) {
            currentTime.substring(11, 12).toInt()
        } else {
            currentTime.substring(11, 13).toInt()
        }
        return when (curHour) {
            in 6..11 -> R.drawable.morning
            in 12..17 -> R.drawable.afternoon
            else -> R.drawable.night
        }
    }
    data class WeatherAdvice(val alert: String, val advice: String)

    private fun getWeatherAdvice(code: Int): WeatherAdvice {
        return when (code) {
            1000 -> WeatherAdvice("Trời nắng và trong trẻo", "Hãy tận hưởng thời tiết đẹp!")
            1003, 1006 -> WeatherAdvice("Trời hơi có mây", "Có thể nên mang ô khi ra ngoài")
            1009, 1030, 1135, 1147, 1150, 1153 -> WeatherAdvice(
                "Sương mù hoặc mưa phùn",
                "Hãy cẩn thận khi lái xe"
            )
            1063, 1066, 1069, 1072, 1087, 1114, 1117 -> WeatherAdvice(
                "Có thể có mưa",
                "Hãy xem xét ở trong nhà"
            )
            1171, 1198, 1201, 1204, 1207, 1210, 1213, 1216, 1219, 1222, 1225, 1237 -> WeatherAdvice(
                "Đang có tuyết hoặc trời lạnh",
                "Hãy giữ ấm và cẩn thận khi di chuyển"
            )
            1180, 1183, 1186, 1189, 1192, 1195, 1240, 1243, 1246 -> WeatherAdvice(
                "Có mưa hoặc có thể có cơn mưa",
                "Đừng quên mang ô"
            )
            1261, 1264 -> WeatherAdvice("Có thể có mưa đá", "Hãy cẩn thận và chú ý đến các bước đi")
            1273, 1276 -> WeatherAdvice(
                "Có thể có cơn giông",
                "Tìm nơi trú ẩn và tránh các khu vực mở"
            )
            1279, 1282 -> WeatherAdvice(
                "Có thể có tuyết kèm sấm sét",
                "Ở trong nhà và tận hưởng cảnh tượng"
            )
            else -> WeatherAdvice("Không rõ điều kiện thời tiết.", "")
        }
    }

    private fun huongGio(tenVietTat: String): String {
        return when (tenVietTat) {
            "S" -> "Gió Nam"
            "SSE" -> "Gió Nam Đông Nam"
            "SE" -> "Gió Đông Nam"
            "ESE" -> "Gió Đông Đông Nam"
            "E" -> "Gió Đông"
            "ENE" -> "Gió Đông Đông Bắc"
            "NE" -> "Gió Đông Bắc"
            "NNE" -> "Gió Bắc Đông Bắc"
            "N" -> "Gió Bắc"
            "NNW" -> "Gió Bắc Tây Bắc"
            "NW" -> "Gió Tây Bắc "
            "WNW" -> "Gió Tây Tây Bắc "
            "W" -> "Gió Tây"
            "WSW" -> "Gió Tây Tây Nam"
            "SW" -> "Gió Tây Nam"
            "SSW" -> "Gió Nam Tây Nam"
            else -> "Gió không xác định"
        }
    }

    private fun handleCurrentWeather(forecastData: ForecastWeather) {
        val current: Forecastday = forecastData.forecast.forecastday[0]
        val currentTemp = forecastData.current.temp_c.toInt()
        val tx1 = "$currentTemp°"
        binding.tvTemp.text = tx1
        val date = LocalDate.parse(current.date)
        val dayOfWeekText = when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "Thứ 2"
            DayOfWeek.TUESDAY -> "Thứ 3"
            DayOfWeek.WEDNESDAY -> "Thứ 4"
            DayOfWeek.THURSDAY -> "Thứ 5"
            DayOfWeek.FRIDAY -> "Thứ 6"
            DayOfWeek.SATURDAY -> "Thứ 7"
            DayOfWeek.SUNDAY -> "Chủ nhật"
            else -> ""
        }
        binding.tvDayTemp.text = dayOfWeekText

        val maxTemp = current.day.maxtemp_c
        val minTemp = current.day.mintemp_c

        val tx2 = "$maxTemp° / $minTemp°"
        val fullText = "$dayOfWeekText $tx2"
        binding.tvDayTemp.text = fullText
        binding.tvInfo.text = forecastData.current.condition.text

    }

    private fun xacDinhMucDoUV(uv: Int): String {
        return when (uv) {
            in 0..2 -> "Rất yếu"
            in 3..5 -> "Yếu"
            in 6..7 -> "Mạnh"
            in 8..10 -> "Rất mạnh"
            else -> "Cực mạnh"
        }
    }
}