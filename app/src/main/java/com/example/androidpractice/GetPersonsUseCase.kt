package com.example.androidpractice

import com.example.androidpractice.Person
import com.example.androidpractice.PersonRepository

class GetPersonsUseCase(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(limit: Int): List<Person> {
        return repository.getPersons(limit)
    }
}