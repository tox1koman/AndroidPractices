package com.example.androidpractice.di

import com.example.androidpractice.domain.usecase.GetPersonsUseCase
import com.example.androidpractice.di.NetworkModule
import com.example.androidpractice.data.repository.PersonRepository
import com.example.androidpractice.data.repository.PersonRepositoryImpl
import com.example.androidpractice.data.FilterBadgeCache

object AppModule {
    val personRepository: PersonRepository = PersonRepositoryImpl(
        NetworkModule.personApi,
        NetworkModule.quoteApi
    )
    val getPersonsUseCase = GetPersonsUseCase(personRepository)
    val filterBadgeCache: FilterBadgeCache by lazy { FilterBadgeCache() }
}