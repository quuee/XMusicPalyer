package cn.x.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SPUtil (private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("xmusic_prefs", Context.MODE_PRIVATE)

    // === String ===
    fun getString(key: String, defaultValue: String = ""): String =
        prefs.getString(key, defaultValue) ?: defaultValue

    fun putString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    // === Int ===
    fun getInt(key: String, defaultValue: Int = 0): Int =
        prefs.getInt(key, defaultValue)

    fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    // === Boolean ===
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean =
        prefs.getBoolean(key, defaultValue)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    // === Long ===
    fun getLong(key: String, defaultValue: Long = 0L): Long =
        prefs.getLong(key, defaultValue)

    fun putLong(key: String, value: Long) {
        prefs.edit { putLong(key, value) }
    }

    // === Float ===
    fun getFloat(key: String, defaultValue: Float = 0f): Float =
        prefs.getFloat(key, defaultValue)

    fun putFloat(key: String, value: Float) {
        prefs.edit { putFloat(key, value) }
    }

    // === 清除 ===
    fun clear() {
        prefs.edit { clear() }
    }

    fun remove(key: String) {
        prefs.edit { remove(key) }
    }
}