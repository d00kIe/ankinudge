package com.teraculus.lingojournalandroid.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teraculus.lingojournalandroid.R

class HeaderAdapter: RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {
    private var count: Int = 0
    class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val helloMessage: TextView = itemView.findViewById(R.id.helloMessage)

        fun bind(count: Int) {
            helloMessage.text = "Hi Dimi, you have ${count.toString()} notes."
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(count)
    }

    override fun getItemCount(): Int {
        return 1
    }

    fun updateCount(newCount: Int) {
        count = newCount
        notifyDataSetChanged()
    }
}