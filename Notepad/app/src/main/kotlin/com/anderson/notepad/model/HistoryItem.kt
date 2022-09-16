package com.anderson.notepad.model

import android.app.SearchManager

class HistoryItem(val icon: String, val title:String, val time: String) {

    companion object {

        const val ICON = SearchManager.SUGGEST_COLUMN_ICON_1
        const val SEARCH = SearchManager.SUGGEST_COLUMN_TEXT_1
        const val TIME = SearchManager.SUGGEST_COLUMN_TEXT_2

    }

}