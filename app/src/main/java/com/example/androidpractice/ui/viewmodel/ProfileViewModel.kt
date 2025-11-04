package com.example.androidpractice.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.data.local.ProfileData
import com.example.androidpractice.data.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    val profile = repository.profile.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        ProfileData()
    )

    fun saveProfile(profile: ProfileData) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }
}