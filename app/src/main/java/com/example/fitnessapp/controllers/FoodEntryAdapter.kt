// FoodEntryAdapter.kt
package com.example.fitnessapp.controllers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.FoodEntry

class FoodEntryAdapter(
    context: Context,
    private val foodEntries: List<FoodEntry>,
    private val onDeleteClickListener: (position: Int) -> Unit
) : ArrayAdapter<FoodEntry>(context, 0, foodEntries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.food_entry_item, parent, false
        )
        val deleteButton = itemView.findViewById<Button>(R.id.deleteButton)

        deleteButton.setOnClickListener {
            onDeleteClickListener(position)
        }


        val foodEntry = foodEntries[position]

        val mealNumberTextView = itemView.findViewById<TextView>(R.id.mealNumberTextView)
        mealNumberTextView.text = "Meal ${position + 1}"

        val macrosTextView = itemView.findViewById<TextView>(R.id.macrosTextView)
        macrosTextView.text =
            "Protein: ${foodEntry.protein}g, Carbs: ${foodEntry.carbs}g, Fat: ${foodEntry.fat}g"

        return itemView
    }
}
