package com.example.androidpractice.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.data.local.FavoriteDao
import com.example.androidpractice.data.local.FavoritePerson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoritePerson>>(emptyList())
    val favorites: StateFlow<List<FavoritePerson>> get() = _favorites

    fun loadFavorites(dao: FavoriteDao) {
        viewModelScope.launch {
            _favorites.value = dao.getAllFavorites()
        }
    }

    fun addToFavorites(dao: FavoriteDao, person: FavoritePerson) {
        viewModelScope.launch {
            dao.addToFavorites(person)
            _favorites.value = dao.getAllFavorites()
        }
    }

    fun removeFromFavorites(dao: FavoriteDao, person: FavoritePerson) {
        viewModelScope.launch {
            dao.removeFromFavorites(person)
            _favorites.value = dao.getAllFavorites()
        }
    }

    suspend fun isFavorite(dao: FavoriteDao, id: Int): Boolean {
        return dao.isFavorite(id)
    }
}