package com.example.androidpractice.data.repository

import com.example.androidpractice.data.remote.PersonApi
import com.example.androidpractice.data.remote.QuoteApi
import com.example.androidpractice.domain.model.Person

class PersonRepositoryImpl(
    private val personApi: PersonApi,
    private val quoteApi: QuoteApi
) : PersonRepository {
    override suspend fun getPersons(limit: Int): List<Person> {
        return try {
            val response = personApi.getPersons(limit = limit, skip = 0)

            response.users.map { person ->
                val quote = quoteApi.getRandomQuote()
                person.copy(bio = quote.quote)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}

interface PersonRepository {
    suspend fun getPersons(limit: Int): List<Person>
}