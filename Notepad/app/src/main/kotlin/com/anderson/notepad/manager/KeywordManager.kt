package com.anderson.notepad.manager

import android.content.Context
import android.content.SharedPreferences

class KeywordManager(val context: Context) {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences("Notepad", Context.MODE_PRIVATE)
    }

    private fun getKeyword(): String {
        return preferences.getString("keyword","null")!!
    }

    private fun save(word: String) {
        preferences.edit().putString("keyword",word).apply()
    }


    fun setKeyword(word: String): Boolean {

        return if (word.length < 16 && word.isNotEmpty()) {

            if (word != getKeyword()) save(word)

            true

        }else false

    }

}