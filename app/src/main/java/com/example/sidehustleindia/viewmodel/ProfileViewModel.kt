package com.example.sidehustleindia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sidehustleindia.data.GigRepository
import com.example.sidehustleindia.data.HustleRepository
import com.example.sidehustleindia.data.remote.JSearchJob
import com.example.sidehustleindia.data.remote.JSearchResponse
import com.example.sidehustleindia.di.NetworkModule
import com.example.sidehustleindia.di.UserRequest
import com.example.sidehustleindia.model.UserProfile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val gigRepository = GigRepository()
    
    private val _liveGigs = kotlinx.coroutines.flow.MutableStateFlow<List<JSearchJob>>(emptyList())
    val liveGigs: kotlinx.coroutines.flow.StateFlow<List<JSearchJob>> = _liveGigs.asStateFlow()
    
    private val _isLoadingGigs = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isLoadingGigs: kotlinx.coroutines.flow.StateFlow<Boolean> = _isLoadingGigs.asStateFlow()
    
    private val _gigError = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val gigError: kotlinx.coroutines.flow.StateFlow<String?> = _gigError.asStateFlow()

    fun fetchLiveGigs(skill: String, city: String) {
        viewModelScope.launch {
            _isLoadingGigs.value = true
            _gigError.value = null
            val query = "$skill in $city freelance"
            val result = gigRepository.searchGigs(query)
            
            result.onSuccess { response ->
                _liveGigs.value = response.data
                if (response.data.isEmpty()) {
                    _gigError.value = "No direct gigs found. Try Client Hunt!"
                }
            }.onFailure {
                _gigError.value = "API Limit/Error. Using Smart Search."
            }
            _isLoadingGigs.value = false
        }
    }
    private val _userProfile = kotlinx.coroutines.flow.MutableStateFlow(UserProfile())
    val userProfile: kotlinx.coroutines.flow.StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    private val _isLoadingProfile = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isLoadingProfile: kotlinx.coroutines.flow.StateFlow<Boolean> = _isLoadingProfile.asStateFlow()
    
    private val _profileError = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val profileError: kotlinx.coroutines.flow.StateFlow<String?> = _profileError.asStateFlow()
    
    private val _isPro = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isPro: kotlinx.coroutines.flow.StateFlow<Boolean> = _isPro.asStateFlow()

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

    private fun getCurrentUserEmail(): String? {
        val account = GoogleSignIn.getLastSignedInAccount(getApplication())
        return account?.email
    }

    fun fetchUserProfile() {
        println("🔄 fetchUserProfile() called")
        viewModelScope.launch {
            _isLoadingProfile.value = true
            _profileError.value = null
            try {
                val email = getCurrentUserEmail()
                println("👤 User Email: $email")
                
                if (email != null) {
                    val response = NetworkModule.getUserService(getApplication()).getUser(email)
                    if (response.isSuccessful) {
                        val userData = response.body()?.user
                        if (userData != null) {
                            println("✅ Profile fetched: $userData")
                            _userProfile.value = UserProfile(
                                id = userData.id,
                                skills = userData.skills ?: com.example.sidehustleindia.model.Skills(),
                                city = userData.city ?: "",
                                availableTime = userData.available_time ?: "",
                                goals = userData.goals ?: ""
                            )
                        } else {
                             println("⚠️ Profile found but empty data, using defaults")
                        }
                    } else {
                         // 404 means user exists in Auth but maybe not in DB? Or just error.
                         // If 404 and we just logged in, maybe we assume default profile.
                         println("❌ fetchUserProfile error: ${response.code()}")
                         if (response.code() != 404) {
                             _profileError.value = "Failed to load profile (Code: ${response.code()})"
                         }
                    }
                } else {
                    println("⚠️ No user logged in")
                }
            } catch (e: Exception) {
                println("❌ fetchUserProfile failed: ${e.message}")
                e.printStackTrace()
                _profileError.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoadingProfile.value = false
            }
        }
    }
    
    fun updateProfile(newProfile: UserProfile) {
        println("💾 updateProfile() called")
        viewModelScope.launch {
            _isLoadingProfile.value = true
            _profileError.value = null
            try {
                val account = GoogleSignIn.getLastSignedInAccount(getApplication())
                val email = account?.email
                
                if (email != null) {
                    val request = UserRequest(
                        email = email,
                        name = account.displayName,
                        avatarUrl = account.photoUrl?.toString(),
                        googleId = account.id,
                        city = newProfile.city,
                        availableTime = newProfile.availableTime,
                        goals = newProfile.goals,
                        skills = newProfile.skills
                    )
                    
                    val response = NetworkModule.getUserService(getApplication()).syncUser(request)
                    if (response.isSuccessful) {
                         println("✅ Profile updated successfully")
                        _userProfile.value = newProfile.copy(id = response.body()?.user?.id ?: newProfile.id)
                    } else {
                       _profileError.value = "Failed to update profile: ${response.message()}" 
                    }
                } else {
                    _profileError.value = "Not logged in"
                }
            } catch (e: Exception) {
                println("❌ updateProfile failed: ${e.message}")
                e.printStackTrace()
                _profileError.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoadingProfile.value = false
            }
        }
    }
    
    fun toggleProStatus() {
        println("🔄 toggleProStatus() called - Current: ${_isPro.value}")
        _isPro.value = !_isPro.value
        println("✅ Pro status now: ${_isPro.value}")
    }
}
