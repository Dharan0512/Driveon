package com.revon.driveon

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class BillAdapter(
    private val list: ArrayList<Booking>
) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

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

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Booking>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}

/**
 * Shared binding helper so [BillAdapter] and [AdminBookingAdapter]
 * render the premium booking row identically, with a status-driven
 * accent stripe and badge.
 */
object BookingRowBinder {

    fun bind(
        context: android.content.Context,
        booking: Booking,
        accent: View,
        vehicle: TextView,
        plate: TextView,
        phone: TextView,
        date: TextView,
        status: TextView
    ) {

        vehicle.text =
            if (booking.bikeName.isNotBlank()) booking.bikeName
            else booking.vehicleNumber.ifBlank { "Vehicle" }

        plate.text = booking.vehicleNumber
        plate.visibility =
            if (booking.vehicleNumber.isBlank()) View.GONE else View.VISIBLE

        phone.text =
            booking.userPhone.ifBlank { "No contact" }

        date.text = booking.date.ifBlank { "--" }

        when (booking.status) {

            "ACTIVE" -> {
                accent.setBackgroundColor(color(context, R.color.brand))
                status.text = "ACTIVE"
                status.setTextColor(color(context, R.color.brand))
                status.setBackgroundResource(R.drawable.bg_status_pending)
                setStatusDot(status, R.drawable.ic_dot_pending)
            }

            "PENDING_BILLING" -> {
                accent.setBackgroundColor(color(context, R.color.danger))
                status.text = "PENDING BILL"
                status.setTextColor(color(context, R.color.danger))
                status.setBackgroundResource(R.drawable.bg_status_rejected)
                setStatusDot(status, R.drawable.ic_dot_rejected)
            }

            "COMPLETED" -> {
                accent.setBackgroundColor(color(context, R.color.success))
                status.text = "COMPLETED"
                status.setTextColor(color(context, R.color.success))
                status.setBackgroundResource(R.drawable.bg_status)
                setStatusDot(status, R.drawable.ic_dot_success)
            }

            else -> {
                accent.setBackgroundColor(color(context, R.color.text_tertiary))
                status.text = booking.status.ifBlank { "--" }
                status.setTextColor(color(context, R.color.text_secondary))
                status.setBackgroundResource(R.drawable.bg_status_pending)
                setStatusDot(status, R.drawable.ic_dot_pending)
            }
        }
    }

    private fun setStatusDot(status: TextView, res: Int) {
        status.setCompoundDrawablesRelativeWithIntrinsicBounds(
            res, 0, 0, 0
        )
    }

    private fun color(context: android.content.Context, res: Int) =
        ContextCompat.getColor(context, res)
}
