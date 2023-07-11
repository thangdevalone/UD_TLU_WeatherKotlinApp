package com.example.weatherapptlu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapptlu.model.Card.CardTime


class WeatherAdapter(private val context: Context, private val listWeather: List<CardTime>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_hours_card, parent, false)
        return WeatherViewHolder(view)
    }


    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {

        val weather = listWeather[position]


        holder.timeText.text = weather.time
        holder.tvTemp.text = weather.deg + "°"
        val icon = "https:${weather.icon_time}"
        Glide.with(context).load(icon).into(holder.iconWeather)



    }
    override fun getItemCount(): Int {
        return listWeather.size
    }
    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeText: TextView = itemView.findViewById(R.id.time)
        val iconWeather: ImageView= itemView.findViewById(R.id.time_icon)
        val tvTemp: TextView = itemView.findViewById(R.id.deg)
    }
}