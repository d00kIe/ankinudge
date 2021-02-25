package com.teraculus.lingojournalandroid.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teraculus.lingojournalandroid.R
import com.teraculus.lingojournalandroid.model.Activity

class ActivitiesAdapter(private val onClick: (Activity) -> Unit) : ListAdapter<Activity, ActivitiesAdapter.ActivityViewHolder>(NoteDiffCallback) {
    class ActivityViewHolder(itemView: View, val onClick: (Activity) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.titleTextView)
        private val tvText: TextView = itemView.findViewById(R.id.noteTextView)
        private val tvDate: TextView = itemView.findViewById(R.id.datetimeTextView)

        private var activity: Activity? = null

        init {
            itemView.setOnClickListener {
                activity?.let { it1 -> onClick(it1) }
            }
        }

        fun bind(activity: Activity) {
            this.activity = activity
            tvTitle.text = activity.title
            tvText.text = activity.text
            tvDate.text = activity.date.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return ActivityViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = getItem(position)
        holder.bind(activity)
    }
}

object NoteDiffCallback: DiffUtil.ItemCallback<Activity>() {
    override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
        return  oldItem.hashCode() == newItem.hashCode()
    }
}