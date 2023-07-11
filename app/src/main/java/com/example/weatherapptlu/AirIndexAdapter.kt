package com.example.weatherapptlu


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapptlu.model.Card.AirCard
import com.example.weatherapptlu.model.Card.CardTime


class AirIndexAdapter(private val context: Context, private val listAirCard: List<AirCard>) :
    RecyclerView.Adapter<AirIndexAdapter.AirViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forcast_air_card, parent, false)
        return AirViewHolder(view)
    }


    override fun onBindViewHolder(holder: AirViewHolder, position: Int) {

        val card = listAirCard[position]
        holder.detailAir.text=card.detail_air

        if(card.index_air!=""){
            holder.indexAir.text=card.index_air
        }
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = 15F
        shape.setStroke(1, Color.WHITE)
        shape.setColor(Color.parseColor(card.color))
        holder.mainAirCard.background = shape



    }
    override fun getItemCount(): Int {
        return listAirCard.size
    }
    class AirViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val indexAir:TextView=itemView.findViewById(R.id.index_air)
        val detailAir:TextView=itemView.findViewById(R.id.detail_air)
        val mainAirCard:LinearLayout=itemView.findViewById(R.id.main_air_card)
    }
}