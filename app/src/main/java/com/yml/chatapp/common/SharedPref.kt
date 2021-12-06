package com.yml.chatapp.common

import android.content.Context
import android.content.SharedPreferences

class SharedPref(var context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ChatAppSharedPref", Context.MODE_PRIVATE)

    companion object {
        private val instance: SharedPref? by lazy { null }
        fun getInstance(context: Context) = instance ?: SharedPref(context)
    }

    fun addUserId(userId: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_ID, userId)
        editor.apply()
    }

    fun getUserId() = sharedPreferences.getString(USER_ID, null)

    fun clearAll() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}