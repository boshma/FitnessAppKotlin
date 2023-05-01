//Models/Exercise.kt

package com.example.fitnessapp.models
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = User::class,
    parentColumns = ["id"],
    childColumns = ["userId"],
    onDelete = ForeignKey.CASCADE)])
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int? = null,
    val name: String,
    val reps: Int,
    val weight: Int,
    val date: String? = null
)

