package com.example.androidpractice.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FilterBadgeCache {
    private val _showBadge = MutableStateFlow(false)
    val showBadge: StateFlow<Boolean> = _showBadge

    fun setChanged(changed: Boolean) {
        _showBadge.value = changed
    }
}