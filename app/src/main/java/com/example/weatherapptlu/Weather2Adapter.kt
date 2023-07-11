package com.example.weatherapptlu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapptlu.model.Forecast.Forecastday
import java.time.DayOfWeek
import java.time.LocalDate


class Weather2Adapter(private val context: Context, private val listDay: List<Forecastday>) :
    RecyclerView.Adapter<Weather2Adapter.Weather2ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Weather2ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.forcast_days_card, parent, false)
        return Weather2ViewHolder(view)
    }


    override fun onBindViewHolder(holder: Weather2ViewHolder, position: Int) {

        val day = listDay[position]


        holder.dayText.text = handleDateText(day.date)
        holder.maxMinTemp.text =
            handleDegText(day.day.maxtemp_c.toString(), day.day.mintemp_c.toString())
        val icon = "https:${day.day.condition.icon}"
        Glide.with(context).load(icon).into(holder.iconWeather)
        holder.weatherInfo.text = day.day.condition.text


    }

    private fun handleDateText(d: String): String {
        val date = LocalDate.parse(d)
        val dayOfWeekText = when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "Thứ 2"
            DayOfWeek.TUESDAY -> "Thứ 3"
            DayOfWeek.WEDNESDAY -> "Thứ 4"
            DayOfWeek.THURSDAY -> "Thứ 5"
            DayOfWeek.FRIDAY -> "Thứ 6"
            DayOfWeek.SATURDAY -> "Thứ 7"
            DayOfWeek.SUNDAY -> "Chủ nhật"
        }

        return dayOfWeekText
    }

    private fun handleDegText(degMax: String, degMin: String): String {
        return "$degMax° / $degMin°"
    }

    override fun getItemCount(): Int {
        return listDay.size
    }

    class Weather2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayText: TextView = itemView.findViewById(R.id.day)
        val iconWeather: ImageView = itemView.findViewById(R.id.day_icon)
        val weatherInfo: TextView = itemView.findViewById(R.id.weather_info)
        val maxMinTemp: TextView = itemView.findViewById(R.id.deg_max_min)
    }
}