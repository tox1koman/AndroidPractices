package com.example.androidpractice


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.Person
import com.example.androidpractice.GetPersonsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class UiState {
    object Loading : UiState()
    data class Success(val persons: List<Person>) : UiState()
    data class Error(val message: String) : UiState()
}

class PersonViewModel(
    private val getPersonsUseCase: GetPersonsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadPersons()
    }

    private fun loadPersons() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            try {
                val persons = getPersonsUseCase(limit = 12)
                _uiState.value = UiState.Success(persons)
            } catch (e: IOException) {
                _uiState.value = UiState.Error("Нет интернета или сервер недоступен")
            } catch (e: HttpException) {
                _uiState.value = UiState.Error("Ошибка сервера: ${e.code()}")
            } catch (e: Exception) {
                Log.d("ERR", e.stackTrace.toList().toString())
                _uiState.value = UiState.Error("Неизвестная ошибка: ${e.message}")
            }
        }
    }

    fun retry() {
        loadPersons()
    }
}