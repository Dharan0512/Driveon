package com.revon.driveon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(private val list: ArrayList<Booking>) :
    RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bikeName: TextView = view.findViewById(R.id.bikeName)
        val rent: TextView = view.findViewById(R.id.rent)
        val date: TextView = view.findViewById(R.id.date)
        val status: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bikeName.text = item.bikeName
        holder.rent.text = item.rent
        holder.date.text = item.date
        holder.status.text = item.status
    }

    override fun getItemCount(): Int = list.size
}