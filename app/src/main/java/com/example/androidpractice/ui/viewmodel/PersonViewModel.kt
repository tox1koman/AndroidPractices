package com.example.androidpractice.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.domain.usecase.GetPersonsUseCase
import com.example.androidpractice.domain.model.Person
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
    private val getPersonsUseCase: GetPersonsUseCase,
    context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("filter_prefs", Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var showMale = prefs.getBoolean("showMale", true)
    private var showFemale = prefs.getBoolean("showFemale", true)
    private var jobFilter: String? = prefs.getString("professionFilter", null)

    init {
        loadPersons()
    }

    private fun loadPersons() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            try {
                val persons = getPersonsUseCase(limit = 12)
                val filtered = applyFilters(persons)
                _uiState.value = UiState.Success(filtered)
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

    private fun applyFilters(persons: List<Person>): List<Person> {
        return persons.filter { person ->
            val genderOk = (person.gender == "male" && showMale) || (person.gender == "female" && showFemale)
            val jobOk = jobFilter?.let { filter ->
                person.company?.title?.contains(filter, ignoreCase = true) ?: false
            } ?: true
            genderOk && jobOk
        }
    }

    fun retry() {
        loadPersons()
    }

    fun setJobFilter(job: String?) {
        jobFilter = job
        loadPersons()
    }

    fun setGenderFilter(male: Boolean, female: Boolean) {
        showMale = male
        showFemale = female
        loadPersons()
    }
}