package com.example.sidehustleindia.model

data class Hustle(
    val id: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val estimatedIncome: String,
    // Future expansion: Plan steps, Templates, etc.
)
