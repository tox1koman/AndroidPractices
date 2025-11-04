package com.example.androidpractice.data.repository

import com.example.androidpractice.data.remote.PersonApi
import com.example.androidpractice.data.remote.QuoteApi
import com.example.androidpractice.domain.model.Person
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class PersonRepositoryImpl(
    private val personApi: PersonApi,
    private val quoteApi: QuoteApi
) : PersonRepository {

    override suspend fun getPersons(limit: Int): List<Person> = coroutineScope {
        val response = personApi.getPersons(limit = limit, skip = 0)

        response.users.map { person ->
            async {
                val quote = quoteApi.getRandomQuote()
                person.copy(bio = quote.quote)
            }
        }.awaitAll()
    }

    override suspend fun getPersonById(id: Int): Person = coroutineScope {
        val response = personApi.getPersonById(id)

        val person = response
        val quote = quoteApi.getRandomQuote()

        person.copy(bio = quote.quote)
    }
}

interface PersonRepository {
    suspend fun getPersons(limit: Int): List<Person>
    suspend fun getPersonById(id: Int): Person
}