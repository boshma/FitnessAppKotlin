//Models/FoodEntry.kt
package com.example.fitnessapp.models
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = User::class,
    parentColumns = ["id"],
    childColumns = ["userId"],
    onDelete = ForeignKey.CASCADE)])
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val date: String
)
