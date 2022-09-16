package com.anderson.notepad.filter

import android.widget.Filter
import com.anderson.notepad.model.Note
import java.util.LinkedList

class NoteFilter(private val notes: List<Note>): Filter() {

    var onPublishResultsListener: OnPublishResultsListener? = null

    override fun performFiltering(query: CharSequence?): FilterResults {

        val filteredList = LinkedList<Note>()

        query?.let {

            val innerQuery = it.toString().lowercase().trim()

            if (it.isEmpty()) {

                filteredList.addAll(notes)

            }else {

                for (note in notes) {

                    val title = note.title.lowercase()

                    if (title.contains(innerQuery)) {
                        filteredList.add(note)
                    }

                }

                for (note in notes) {

                    val content = note.content.lowercase()

                    if (!filteredList.contains(note) && content.contains(innerQuery)) {
                        filteredList.add(note)
                    }

                }

            }

        }

        val results = FilterResults()
        results.values = filteredList

        return results

    }

    override fun publishResults(query: CharSequence?, results: FilterResults?) {

        results?.apply {

            onPublishResultsListener?.onPublishResults(query, values as List<Note>)
        
        }

    }

    interface OnPublishResultsListener {

        fun onPublishResults(query: CharSequence?, results: List<Note>)

    }

}