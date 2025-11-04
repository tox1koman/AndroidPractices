package com.example.androidpractice.data.repository

import com.example.androidpractice.data.local.FavoriteDao
import com.example.androidpractice.data.local.FavoritePerson

class FavoriteRepository(private val dao: FavoriteDao) {

    suspend fun getFavorites(): List<FavoritePerson> = dao.getAllFavorites()

    suspend fun addFavorite(person: FavoritePerson) = dao.addToFavorites(person)

    suspend fun removeFavorite(person: FavoritePerson) = dao.removeFromFavorites(person)

    suspend fun isFavorite(id: Int): Boolean = dao.isFavorite(id)
}