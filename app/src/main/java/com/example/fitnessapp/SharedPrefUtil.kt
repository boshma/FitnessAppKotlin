//SharedPrefUtil.kt
package com.example.fitnessapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPrefUtil {

    private const val EXERCISES_PREF = "exercises_pref"
    private const val EXERCISES_KEY = "exercises_key"

    fun saveExercises(context: Context, exercises: List<String>) {
        val sharedPreferences = context.getSharedPreferences(EXERCISES_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(exercises)
        editor.putString(EXERCISES_KEY, json)
        editor.apply()
    }

    fun loadExercises(context: Context): MutableList<String> {
        val sharedPreferences = context.getSharedPreferences(EXERCISES_PREF, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(EXERCISES_KEY, null)
        val type = object : TypeToken<List<String>>() {}.type

        return gson.fromJson(json, type) ?: mutableListOf("Squat", "Bench Press", "Deadlift", "Overhead Press", "Barbell Row")
    }
}
