package com.example.androidpractice

sealed class Routes(val route: String,) {
    object Home : Routes("home")
    object List : Routes("list")
}