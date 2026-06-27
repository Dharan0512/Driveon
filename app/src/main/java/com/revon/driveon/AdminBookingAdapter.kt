package com.revon.driveon

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminBookingAdapter(
    private val list: ArrayList<Booking>
) : RecyclerView.Adapter<AdminBookingAdapter.ViewHolder>() {

    class ViewHolder(v: View) :
        RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.booking_admin_item,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val booking = list[position]

        holder.itemView
            .findViewById<TextView>(
                R.id.txtVehicle
            )
            .text =
            booking.vehicleNumber

        holder.itemView
            .findViewById<TextView>(
                R.id.txtStatus
            )
            .text =
            booking.status

        holder.itemView.setOnClickListener {

            val intent =
                Intent(
                    holder.itemView.context,
                    BillReviewActivity::class.java
                )

            intent.putExtra(
                "bookingId",
                booking.bookingId
            )

            holder.itemView.context
                .startActivity(intent)
        }
    }

    override fun getItemCount() =
        list.size
}