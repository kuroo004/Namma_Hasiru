package com.example.namma_hasiru.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single tree sapling entry in the local database.
 * Part of the 'Sustainability Mission' to track survival rates.
 */
@Entity(tableName = "tree_history")
data class TreeEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val speciesName: String,
    val latitude: Double,
    val longitude: Double,
    val day1PhotoUri: String,
    val healthStatus: String, // Sprouted, Growing, Healthy, or Died
    val datePlanted: Long, // Unix timestamp of the planting date
    val lastCheckUpDate: Long? = null,
    val survivalRate: Float = 100f // Calculated based on status updates
)
