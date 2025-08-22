package com.example.musicapp.SharedPreferences

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class SearchPrefManager(context: Context) {
    private val prefs = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun saveSearch(query: String) {
        val current = prefs.getStringSet("recent_searches", mutableSetOf())!!.toMutableList()
        current.remove(query)        // duplicate remove
        current.add(0, query)        // new at top
        if (current.size > 5) current.removeLast()
        prefs.edit().putStringSet("recent_searches", current.toSet()).apply()
    }

    fun deleteSearch(query: String) {
        val current = prefs.getStringSet("recent_searches", mutableSetOf())!!.toMutableList()
        current.remove(query)
        prefs.edit().putStringSet("recent_searches", current.toSet()).apply()
    }


    fun getSearches(): List<String> {
        return prefs.getStringSet("recent_searches", emptySet())!!.toList()
    }



}