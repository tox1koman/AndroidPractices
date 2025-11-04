package com.example.androidpractice.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritePerson(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val bio: String?,
    val gender: String?,
    val jobTitle: String?,
    val imageUrl: String?,

)
