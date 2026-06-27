package com.revon.driveon

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RegisteredUsersAdapter(

    private val groups: MutableList<RegisteredUserGroup>,
    private val onUserClick: (RegisteredUser) -> Unit,
    private val onUserCall: (RegisteredUser) -> Unit

) : RecyclerView.Adapter<RegisteredUsersAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val txtGroupTitle: TextView =
            view.findViewById(R.id.txtGroupTitle)

        val txtGroupSubtitle: TextView =
            view.findViewById(R.id.txtGroupSubtitle)

        val txtGroupCount: TextView =
            view.findViewById(R.id.txtGroupCount)

        val imgArrow: ImageView =
            view.findViewById(R.id.imgArrow)

        val groupHeader: LinearLayout =
            view.findViewById(R.id.groupHeader)

        val usersContainer: LinearLayout =
            view.findViewById(R.id.usersContainer)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_registered_group,
                parent,
                false
            )

        return GroupViewHolder(view)
    }

    override fun getItemCount(): Int {

        return groups.size
    }

    override fun onBindViewHolder(

        holder: GroupViewHolder,

        position: Int

    ) {

        val group = groups[position]

        holder.txtGroupTitle.text = group.title

        holder.txtGroupSubtitle.text = group.subtitle

        holder.txtGroupCount.text = group.count.toString()

        holder.usersContainer.removeAllViews()

        if (group.expanded) {

            holder.usersContainer.visibility = View.VISIBLE

            rotateArrow(holder.imgArrow,180f)

            for ((index, user) in group.users.withIndex()) {

                val row =
                    LayoutInflater.from(holder.itemView.context)
                        .inflate(
                            R.layout.item_registered_user,
                            holder.usersContainer,
                            false
                        )

                // First row sits right under the header — no divider
                row.findViewById<View>(R.id.rowDivider).visibility =
                    if (index == 0) View.GONE else View.VISIBLE

                val txtAvatar =
                    row.findViewById<TextView>(
                        R.id.txtAvatar
                    )

                val txtName =
                    row.findViewById<TextView>(
                        R.id.txtName
                    )

                val txtPhone =
                    row.findViewById<TextView>(
                        R.id.txtPhone
                    )

                val txtStatus =
                    row.findViewById<TextView>(
                        R.id.txtStatus
                    )

                val imgNext =
                    row.findViewById<ImageView>(
                        R.id.imgNext
                    )

                val btnCall =
                    row.findViewById<FrameLayout>(
                        R.id.btnCall
                    )

                txtName.text = user.name

                txtPhone.text = user.phone

                txtStatus.text = user.status

                if(user.name.isNotEmpty()){

                    txtAvatar.text =
                        user.name
                            .substring(0,1)
                            .uppercase()

                }else{

                    txtAvatar.text="?"

                }

                imgNext.alpha = 0.70f

                row.setOnClickListener{

                    onUserClick(user)

                }

                btnCall.setOnClickListener {

                    onUserCall(user)

                }

                holder.usersContainer.addView(row)

            }

        } else {

            holder.usersContainer.visibility = View.GONE

            rotateArrow(holder.imgArrow,0f)

        }

        holder.groupHeader.setOnClickListener {

            val pos = holder.bindingAdapterPosition

            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            groups[pos].expanded = !groups[pos].expanded

            notifyItemChanged(pos)

        }

    }

    /**
     * Smooth arrow rotation
     */
    private fun rotateArrow(

        image: ImageView,

        rotation: Float

    ) {

        image.animate()

            .rotation(rotation)

            .setDuration(220)

            .start()

    }

    /**
     * Expand all groups
     */
    fun expandAll() {

        groups.forEach {

            it.expanded = true

        }

        notifyDataSetChanged()

    }

    /**
     * Collapse all groups
     */
    fun collapseAll() {

        groups.forEach {

            it.expanded = false

        }

        notifyDataSetChanged()

    }

    /**
     * Update adapter data
     */
    fun updateData(

        newGroups: MutableList<RegisteredUserGroup>

    ) {

        groups.clear()

        groups.addAll(newGroups)

        notifyDataSetChanged()

    }

}