package com.example.sidehustleindia.model

data class UserProfile(
    val skills: Skills = Skills(),
    val city: String = "",
    val availableTime: String = "",
    val goals: String = ""
)

data class Skills(
    val technical: List<String> = emptyList(),
    val soft: List<String> = emptyList(),
    val specific: List<String> = emptyList()
)
