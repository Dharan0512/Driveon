package com.revon.driveon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class UserAdapter(
    context: Context,
    private val users: ArrayList<String>
) : ArrayAdapter<String>(context, 0, users) {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val user = users[position]

        // Date Header

        if (user.contains("Users)")) {

            return LayoutInflater.from(context)
                .inflate(R.layout.row_date_header, parent, false)
        }

        if (user.contains("Users)")) {

            val headerView = LayoutInflater.from(context)
                .inflate(R.layout.row_date_header, parent, false)

            val txtDate = headerView.findViewById<TextView>(R.id.txtDate)
            val txtCount = headerView.findViewById<TextView>(R.id.txtCount)

            val date = user.substringBefore(" (")
            val count = user.substringAfter("(").substringBefore(")")

            txtDate.text = date
            txtCount.text = count

            return headerView
        }

        // Normal User Row
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.user_item, parent, false)

        val txtPhone = view.findViewById<TextView>(R.id.txtPhone)
        val txtName = view.findViewById<TextView>(R.id.txtName)

        val phone = user.substringBefore(" (")
        val name = user.substringAfter("(").substringBefore(")")

        txtPhone.text = phone
        txtName.text = name

        return view
    }
}