package com.revon.driveon

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminBookingAdapter(
    private val list: ArrayList<Booking>
) : RecyclerView.Adapter<AdminBookingAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val accent: View = view.findViewById(R.id.vAccent)
        val vehicle: TextView = view.findViewById(R.id.txtVehicle)
        val plate: TextView = view.findViewById(R.id.txtPlate)
        val phone: TextView = view.findViewById(R.id.txtPhone)
        val date: TextView = view.findViewById(R.id.txtDate)
        val status: TextView = view.findViewById(R.id.txtStatus)
        val call: View = view.findViewById(R.id.btnCall)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.booking_row,
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

        BookingRowBinder.bind(holder.itemView.context, booking,
            holder.accent, holder.vehicle, holder.plate,
            holder.phone, holder.date, holder.status)

        holder.call.setOnClickListener {
            if (booking.userPhone.isNotBlank()) {
                holder.itemView.context.startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + booking.userPhone)
                    )
                )
            }
        }

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

    override fun getItemCount() = list.size

    fun updateData(newList: List<Booking>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
