package com.example.androidpractice.data.remote

import com.example.androidpractice.domain.model.Quote
import retrofit2.http.GET

interface QuoteApi {
    @GET("quotes/random")
    suspend fun getRandomQuote(): Quote
}