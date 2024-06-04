package com.dicoding.picodiploma.loginwithanimation.data.model

import java.io.Serializable

data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double,
    val lon: Double
): Serializable