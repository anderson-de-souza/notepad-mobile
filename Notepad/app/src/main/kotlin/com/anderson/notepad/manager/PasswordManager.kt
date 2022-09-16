package com.anderson.notepad.manager

import android.content.Context
import android.content.SharedPreferences

class PasswordManager(val context: Context) {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences("Notepad", Context.MODE_PRIVATE)
    }

    private fun getPassword(): String {
        return preferences.getString("Password","null")!!
    }

    private fun save(word: String) {
       preferences.edit().putString("Password",word).apply()
    }


    fun setPassword(word: String): Boolean {

        return if (word.length < 16 && word.isNotEmpty()) {

            if (word != getPassword()) save(word)

            true

        }else false

    }

}