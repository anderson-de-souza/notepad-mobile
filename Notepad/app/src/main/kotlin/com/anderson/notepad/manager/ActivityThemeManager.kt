package com.anderson.notepad.manager

import com.anderson.notepad.R
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class ActivityThemeManager(private val activity: AppCompatActivity) {

    private val preferences: SharedPreferences = activity.getSharedPreferences("Notepad",Context.MODE_PRIVATE)

    fun setNightMode(state: Boolean) {
        preferences.edit().putBoolean("dark-mode", state).apply()
    }

    fun getNightMode(): Boolean {
        return preferences.getBoolean("dark-mode", false)
    }

    fun setStyle(id: Int) {
        preferences.edit().putInt("styles", id).apply()
    }

    fun initThemeMode() {
        if(preferences.getBoolean("dark-mode",false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun initTheme(recreateActivity: Boolean) {
        activity.setTheme(preferences.getInt("styles", R.style.SunnySky))
        if (recreateActivity) {
            activity.recreate()
        }
    }

    fun getStyleName(): String {
        return activity.resources.getResourceEntryName(preferences.getInt("styles", R.style.SunnySky))
    }

}