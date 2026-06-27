package com.revon.driveon

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BillAdapter(
    private val list: ArrayList<Booking>
) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view){

        val vehicle =
            view.findViewById<TextView>(
                R.id.txtVehicle
            )

        val phone =
            view.findViewById<TextView>(
                R.id.txtPhone
            )

        val status =
            view.findViewById<TextView>(
                R.id.txtStatus
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            )
                .inflate(
                    R.layout.bill_item,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val booking =
            list[position]

        holder.vehicle.text =
            booking.vehicleNumber

        holder.phone.text =
            booking.userPhone

        holder.status.text =
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

    override fun getItemCount(): Int {

        return list.size
    }
}