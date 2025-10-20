package com.example.androidpractice

import retrofit2.http.GET
import retrofit2.http.Query

interface PersonApi {
    @GET("users")
    suspend fun getPersons(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int = 0
    ): PersonsResponse
}