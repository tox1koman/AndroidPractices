package com.example.androidpractice.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val bio: String = "Нет биографии",
    val gender: String,
    val company: Company = Company("None", "Unknown"),
    val image: String
)

@Serializable
data class Company(
    val name: String,
    val title: String
)