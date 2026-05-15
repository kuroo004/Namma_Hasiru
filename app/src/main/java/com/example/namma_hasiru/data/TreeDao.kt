package com.example.namma_hasiru.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TreeDao {
    @Insert
    suspend fun insertTree(tree: TreeEntry)

    @Update
    suspend fun updateTree(tree: TreeEntry)

    @Delete
    suspend fun deleteTree(tree: TreeEntry)

    @Query("SELECT * FROM tree_history ORDER BY datePlanted DESC")
    fun getAllTrees(): Flow<List<TreeEntry>>

    @Query("SELECT * FROM tree_history WHERE id = :id")
    suspend fun getTreeById(id: Int): TreeEntry?

    @Query("SELECT (CAST(SUM(CASE WHEN healthStatus != 'Died' THEN 1 ELSE 0 END) AS FLOAT) / COUNT(*)) * 100 FROM tree_history")
    fun getSurvivalRate(): Flow<Float?>
}
