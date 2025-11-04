package com.example.androidpractice.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonsResponse(
    val users: List<Person>,
    val total: Int,
    val skip: Int,
    val limit: Int
)