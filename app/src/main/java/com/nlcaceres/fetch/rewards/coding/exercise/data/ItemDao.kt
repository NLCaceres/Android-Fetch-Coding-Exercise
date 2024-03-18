package com.nlcaceres.fetch.rewards.coding.exercise.data

import androidx.room.*

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    suspend fun getAll(): List<Item>

    @Query("SELECT * from items WHERE name IS NOT NULL AND TRIM(name) != ''")
    suspend fun getAllWithNames(): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg items: Item)

    @Delete
    suspend fun deleteItems(vararg items: Item)
}