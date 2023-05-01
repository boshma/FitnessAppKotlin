// AddedExercisesAdapter.kt
package com.example.fitnessapp.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.Exercise

class AddedExercisesAdapter(
    private val addedExercises: List<Exercise>,
    private val onDeleteClick: (Exercise) -> Unit
) : RecyclerView.Adapter<AddedExercisesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseNameTextView: TextView = view.findViewById(R.id.exerciseNameTextView)
        val exerciseInfoTextView: TextView = view.findViewById(R.id.exerciseInfoTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_added_exercise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = addedExercises[position]
        holder.exerciseNameTextView.text = exercise.name
        holder.exerciseInfoTextView.text = "${exercise.weight} kg x ${exercise.reps} reps"
        holder.itemView.findViewById<Button>(R.id.deleteExerciseButton).setOnClickListener {
            onDeleteClick(exercise)
        }
    }

    override fun getItemCount(): Int {
        return addedExercises.size
    }
}
