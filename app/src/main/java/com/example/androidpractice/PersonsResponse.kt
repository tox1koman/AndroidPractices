package com.example.androidpractice

import kotlinx.serialization.Serializable

@Serializable
data class PersonsResponse(
    val users: List<Person>,
    val total: Int,
    val skip: Int,
    val limit: Int
)