package com.example.sidehustleindia.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sidehustleindia.data.HustleRepository
import com.example.sidehustleindia.model.Hustle
import com.example.sidehustleindia.model.Skills
import com.example.sidehustleindia.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    val matchedHustles = userProfile.map { profile ->
        HustleRepository.matchHustles(profile)
    }

    fun updateTechnicalSkills(skills: List<String>) {
        _userProfile.update { it.copy(skills = it.skills.copy(technical = skills)) }
    }

    fun updateSoftSkills(skills: List<String>) {
        _userProfile.update { it.copy(skills = it.skills.copy(soft = skills)) }
    }

    fun updateSpecificSkills(skills: List<String>) {
        _userProfile.update { it.copy(skills = it.skills.copy(specific = skills)) }
    }

    fun updateCity(city: String) {
        _userProfile.update { it.copy(city = city) }
    }

    fun updateTime(time: String) {
        _userProfile.update { it.copy(availableTime = time) }
    }

    fun updateGoals(goals: String) {
        _userProfile.update { it.copy(goals = goals) }
    }
}
