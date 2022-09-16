package com.anderson.notepad.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.anderson.notepad.databinding.HolderHistoryItemBinding
import com.anderson.notepad.model.HistoryItem

class HistoryItemAdapter(context: Context, private var items: List<HistoryItem>): ArrayAdapter<HistoryItem>(context, 0, items) {

    private val allItems by lazy {
        ArrayList<HistoryItem>(items)
    }

    private val inflater by lazy {
      LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    
        return convertView?.apply { 

            with(HolderHistoryItemBinding.bind(this)) {

                val item: HistoryItem = getItem(position)!!

                iconView.setImageURI(Uri.parse(item.icon))
                titleTextView.text = item.title
                timeTextView.text = item.time

            }

        }?: HolderHistoryItemBinding.inflate(inflater, parent, false).apply {

            val item: HistoryItem = getItem(position)!!

            iconView.setImageURI(Uri.parse(item.icon))
            titleTextView.text = item.title
            timeTextView.text = item.time

        }.root

    }

    override fun getFilter(): Filter {
        return HistoryItemFilter()
    }

    inner class HistoryItemFilter: Filter() {

        override fun performFiltering(query: CharSequence?): FilterResults {

            val filteredList = ArrayList<HistoryItem>()

            if (query != null && query.isNotEmpty()) {

                val innerQuery = query.toString().lowercase().trim()

                for (item in allItems) {

                    if (item.title.lowercase().trim().contains(innerQuery)) filteredList.add(item)

                }

            }else {

                filteredList.addAll(allItems)

            }

            val results = FilterResults()
            results.values = filteredList

            return results

        }

        override fun publishResults(query: CharSequence?, results: FilterResults?) {
          
             results?.also {
               
                  clear()
                  addAll(it.values as List<HistoryItem>)
                  notifyDataSetChanged()
               
             }
          
        }
        
        override fun convertResultToString(result: Any?): String {
          return (result as HistoryItem).title
        }

    }

}