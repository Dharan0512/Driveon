package com.revon.driveon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color

class BikeAdapter(private val list: ArrayList<Bike>) :
    RecyclerView.Adapter<BikeAdapter.BikeViewHolder>() {

    class BikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.bikeName)
        val rent: TextView = itemView.findViewById(R.id.bikeRent)
        val category: TextView = itemView.findViewById(R.id.bikeCategory)
        val status: TextView = itemView.findViewById(R.id.bikeStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BikeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bike_item, parent, false)
        return BikeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BikeViewHolder, position: Int) {
        val bike = list[position]

        holder.name.text = bike.name
        holder.rent.text = "₹${bike.rent}/day"
        holder.category.text = bike.category

        if (bike.available == "true") {
            holder.status.text = "Available"
            holder.status.setBackgroundColor(Color.parseColor("#4CAF50"))
        } else {
            holder.status.text = "Not Available"
            holder.status.setBackgroundColor(Color.parseColor("#F44336"))
        }
    }

    override fun getItemCount(): Int = list.size
}