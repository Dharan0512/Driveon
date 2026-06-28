package com.revon.driveon

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VehicleAdapter(
    private val vehicleList: ArrayList<Vehicle>,
    private val onDelete: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val bikeName: TextView = view.findViewById(R.id.tvBikeName)
        val vehicleNumber: TextView = view.findViewById(R.id.tvVehicleNumber)
        val status: TextView = view.findViewById(R.id.tvStatus)

        val day1Price: TextView = view.findViewById(R.id.tv1DPrice)
        val day2Price: TextView = view.findViewById(R.id.tv2DPrice)
        val day7Price: TextView = view.findViewById(R.id.tv7DPrice)
        val day30Price: TextView = view.findViewById(R.id.tv30DPrice)

        val day1ExtraKm: TextView = view.findViewById(R.id.tv1DExtraKm)
        val day2ExtraKm: TextView = view.findViewById(R.id.tv2DExtraKm)
        val day7ExtraKm: TextView = view.findViewById(R.id.tv7DExtraKm)
        val day30ExtraKm: TextView = view.findViewById(R.id.tv30DExtraKm)

        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vehicle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = vehicleList[position]

        // Bike name + vehicle number
        holder.bikeName.text = vehicle.bikeName
        holder.vehicleNumber.text = vehicle.vehicleNumber

        // Status
        val finalStatus = if (vehicle.status.isNotEmpty()) {
            vehicle.status
        } else {
            if (vehicle.available) "Available" else "Booked"
        }

        holder.status.text = finalStatus
        setStatusStyle(holder.status, finalStatus)

        // Prices
        holder.day1Price.text = "₹" + if (vehicle.day1Price.isNotEmpty()) vehicle.day1Price else "0"
        holder.day2Price.text = "₹" + if (vehicle.day2Price.isNotEmpty()) vehicle.day2Price else "0"
        holder.day7Price.text = "₹" + if (vehicle.day7Price.isNotEmpty()) vehicle.day7Price else "0"
        holder.day30Price.text = "₹" + if (vehicle.day30Price.isNotEmpty()) vehicle.day30Price else "0"

        // Extra KM
        holder.day1ExtraKm.text = "+ ₹" + if (vehicle.day1ExtraKm.isNotEmpty()) vehicle.day1ExtraKm else "0" + "/km"
        holder.day2ExtraKm.text = "+ ₹" + if (vehicle.day2ExtraKm.isNotEmpty()) vehicle.day2ExtraKm else "0" + "/km"
        holder.day7ExtraKm.text = "+ ₹" + if (vehicle.day7ExtraKm.isNotEmpty()) vehicle.day7ExtraKm else "0" + "/km"
        holder.day30ExtraKm.text = "+ ₹" + if (vehicle.day30ExtraKm.isNotEmpty()) vehicle.day30ExtraKm else "0" + "/km"

        // Delete button
        holder.btnDelete.setOnClickListener {
            onDelete(vehicle)
        }

        // Edit button
        holder.btnEdit.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditVehicleActivity::class.java)

            intent.putExtra("vehicleNumber", vehicle.vehicleNumber)
            intent.putExtra("bikeName", vehicle.bikeName)
            intent.putExtra("status", finalStatus)

            intent.putExtra("day1Price", vehicle.day1Price)
            intent.putExtra("day2Price", vehicle.day2Price)
            intent.putExtra("day7Price", vehicle.day7Price)
            intent.putExtra("day30Price", vehicle.day30Price)

            intent.putExtra("day1ExtraKm", vehicle.day1ExtraKm)
            intent.putExtra("day2ExtraKm", vehicle.day2ExtraKm)
            intent.putExtra("day7ExtraKm", vehicle.day7ExtraKm)
            intent.putExtra("day30ExtraKm", vehicle.day30ExtraKm)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }

    private fun setStatusStyle(statusView: TextView, statusText: String) {
        val context = statusView.context
        when (statusText.trim().lowercase()) {
            "available" -> {
                statusView.setBackgroundResource(R.drawable.bg_status)
                statusView.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.success))
                statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_success, 0, 0, 0)
            }

            "booked" -> {
                statusView.setBackgroundResource(R.drawable.bg_status_rejected)
                statusView.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.danger))
                statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_rejected, 0, 0, 0)
            }

            "service" -> {
                statusView.setBackgroundResource(R.drawable.bg_status_pending)
                statusView.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.brand))
                statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_pending, 0, 0, 0)
            }

            else -> {
                statusView.setBackgroundResource(R.drawable.bg_status_pending)
                statusView.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.brand))
                statusView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dot_pending, 0, 0, 0)
            }
        }
    }
}