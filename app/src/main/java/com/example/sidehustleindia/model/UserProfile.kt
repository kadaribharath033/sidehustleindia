package com.example.sidehustleindia.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String = "",
    val skills: Skills = Skills(),
    val city: String = "",
    val availableTime: String = "",
    val goals: String = "",
    val phoneModel: String? = null  // Optional field
)

@Serializable
data class Skills(
    val technical: List<String> = emptyList(),
    val soft: List<String> = emptyList(),
    val specific: List<String> = emptyList()
)
