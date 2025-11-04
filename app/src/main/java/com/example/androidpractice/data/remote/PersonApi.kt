package com.example.androidpractice.data.remote

import com.example.androidpractice.domain.model.Person
import com.example.androidpractice.domain.model.PersonsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonApi {
    @GET("users")
    suspend fun getPersons(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int = 0
    ): PersonsResponse

    @GET("users/{id}")
    suspend fun getPersonById(@Path("id") id: Int): Person
}