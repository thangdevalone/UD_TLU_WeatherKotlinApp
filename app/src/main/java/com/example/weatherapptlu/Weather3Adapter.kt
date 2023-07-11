package com.example.weatherapptlu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapptlu.model.Card.CardDetail
import com.example.weatherapptlu.model.Forecast.Forecastday
import java.time.DayOfWeek
import java.time.LocalDate

class Weather3Adapter(private val context: Context, private val listCard: List<CardDetail>) :
    RecyclerView.Adapter<Weather3Adapter.Weather3ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Weather3ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.detail_card, parent, false)
        return Weather3ViewHolder(view)
    }


    override fun onBindViewHolder(holder: Weather3ViewHolder, position: Int) {

        val card = listCard[position]
        holder.infoDetail.text = card.info_detail
        holder.logDetail.text = card.log_detail
        holder.iconDetail.setImageResource(card.icon_detail)
        holder.logDv.text=card.log_dv

    }

    override fun getItemCount(): Int {
        return listCard.size
    }

    class Weather3ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconDetail: ImageView = itemView.findViewById(R.id.detail_icon)
        val infoDetail: TextView =itemView.findViewById(R.id.detail_info)
        val logDetail: TextView = itemView.findViewById(R.id.detail_log)
        val logDv:TextView=itemView.findViewById(R.id.log_dv)
    }
}