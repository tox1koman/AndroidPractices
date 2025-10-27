package com.example.androidpractice.data.local

import androidx.room.*

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    suspend fun getAllFavorites(): List<FavoritePerson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(person: FavoritePerson)

    @Delete
    suspend fun removeFromFavorites(person: FavoritePerson)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean
}
