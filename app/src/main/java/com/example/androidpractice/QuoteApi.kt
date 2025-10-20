package com.example.androidpractice

import retrofit2.http.GET

interface QuoteApi {
    @GET("quotes/random")
    suspend fun getRandomQuote(): Quote
}