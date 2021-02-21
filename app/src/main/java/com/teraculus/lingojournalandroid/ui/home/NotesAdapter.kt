package com.teraculus.lingojournalandroid.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teraculus.lingojournalandroid.R
import com.teraculus.lingojournalandroid.model.LiveRealmObject
import com.teraculus.lingojournalandroid.model.Note

class NotesAdapter(private val onClick: (Note) -> Unit) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(NoteDiffCallback) {
    class NoteViewHolder(itemView: View, val onClick: (Note) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val noteTextView: TextView = itemView.findViewById(R.id.noteTextView)
        private val datetimeTextView: TextView = itemView.findViewById(R.id.datetimeTextView)

        private var note: Note? = null

        init {
            itemView.setOnClickListener {
                note?.let { it1 -> onClick(it1) }
            }
        }

        fun bind(note: Note) {
            this.note = note
            titleTextView.text = note.title
            noteTextView.text = note.text
            datetimeTextView.text = note.date.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }
}

object NoteDiffCallback: DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return  oldItem.hashCode() == newItem.hashCode()
    }
}