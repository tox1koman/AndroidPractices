package com.example.androidpractice.domain.usecase

import com.example.androidpractice.data.repository.PersonRepository
import com.example.androidpractice.domain.model.Person

class GetPersonsUseCase(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(limit: Int): List<Person> {
        return repository.getPersons(limit)
    }

    suspend fun getPersonById(id: Int): Person = repository.getPersonById(id)
}