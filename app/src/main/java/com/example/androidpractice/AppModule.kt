package com.example.androidpractice


import com.example.androidpractice.PersonRepository
import com.example.androidpractice.PersonRepositoryImpl
import com.example.androidpractice.GetPersonsUseCase

object AppModule {
    val personRepository: PersonRepository = PersonRepositoryImpl(
        NetworkModule.personApi,
        NetworkModule.quoteApi)
    val getPersonsUseCase = GetPersonsUseCase(personRepository)
}