package com.example.reactiveex.ui.staffdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reactiveex.R
import com.example.reactiveex.data.Staff
import com.google.android.material.textview.MaterialTextView

class StaffAdapter(
    private val staffList: MutableList<Staff>,
) : RecyclerView.Adapter<StaffAdapter.StaffViewHolder>() {
    private var onItemClickListener: ((Staff) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StaffViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_design, parent, false)
        return StaffViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StaffViewHolder,
        position: Int,
    ) {
        val staff = staffList[position]
        holder.bind(staff)
        holder.itemView.setOnClickListener { onItemClickListener?.invoke(staff) }
    }

    override fun getItemCount(): Int = staffList.size

    fun setOnItemClickListener(listener: (Staff) -> Unit) {
        onItemClickListener = listener
    }

    class StaffViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameView: MaterialTextView = itemView.findViewById(R.id.tenNhanVien)
        private val yearOfBirthView: MaterialTextView = itemView.findViewById(R.id.namSinh)
        private val hometownView: MaterialTextView = itemView.findViewById(R.id.queQuan)

        fun bind(staff: Staff) {
            nameView.text = staff.name
            yearOfBirthView.text = staff.yearOfBirth
            hometownView.text = staff.hometown
        }
    }
}
