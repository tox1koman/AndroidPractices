package com.example.androidpractice.di

import com.example.androidpractice.data.remote.QuoteApi
import com.example.androidpractice.data.remote.PersonApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object NetworkModule {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val personApi: PersonApi = retrofit.create(PersonApi::class.java)
    val quoteApi: QuoteApi = retrofit.create(QuoteApi::class.java)
}