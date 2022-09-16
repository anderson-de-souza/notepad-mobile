package com.anderson.notepad.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anderson.notepad.R
import com.anderson.notepad.databinding.HolderNoteBinding
import com.anderson.notepad.model.Note

class NoteAdapter(context: Context, var notes: List<Note>): RecyclerView.Adapter<NoteAdapter.NoteHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount() = notes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val binding = HolderNoteBinding.inflate(inflater,parent,false)
        return NoteHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.bind(notes[position])
    }

    interface OnItemClickListener {
        fun onItemClick(note: Note, position: Int)
    }

    inner class NoteHolder(private val binding: HolderNoteBinding) : RecyclerView.ViewHolder(binding.root) {

        lateinit var note: Note

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(note, adapterPosition)
            }
        }

        fun bind(note: Note) {

            this.note = note

            if(note.content.isEmpty())  {
                binding.noteIcon.setImageResource(R.drawable.icon_note)
            }else {
                binding.noteIcon.setImageResource(R.drawable.icon_note_content)
            }

            binding.noteTitle.text = note.title

        }

    }

}