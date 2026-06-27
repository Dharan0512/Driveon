package com.revon.driveon

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import com.revon.driveon.ContactStatus

class ContactAdapter(
    private val originalList: List<ContactStatus>
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private var filteredList: MutableList<ContactStatus> =
        originalList.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.name)
        val phone = view.findViewById<TextView>(R.id.phone)
        val status = view.findViewById<TextView>(R.id.status)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = filteredList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = filteredList[position]

        holder.name.text = item.name
        holder.phone.text = item.phone

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = item.isSelected

        holder.checkBox.setOnCheckedChangeListener { _, checked ->
            item.isSelected = checked
        }
    }

    // ⭐ SEARCH FUNCTION
    fun filter(query: String) {

        val text = query.lowercase().trim()

        filteredList = if (text.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.lowercase().contains(text) ||
                        it.phone.contains(text)
            }.toMutableList()
        }

        notifyDataSetChanged()
    }
}