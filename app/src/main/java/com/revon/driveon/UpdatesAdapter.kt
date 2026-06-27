package com.revon.driveon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView

class UpdatesAdapter(
    private val items: List<UpdateItem>
) : RecyclerView.Adapter<UpdatesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val image: ImageView =
            view.findViewById(R.id.updateImage)

        val title: TextView =
            view.findViewById(R.id.updateTitle)

        val desc: TextView =
            view.findViewById(R.id.updateDesc)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_update,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = items[position]

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.image)

        holder.title.text =
            item.title

        holder.desc.text =
            item.description
    }
}