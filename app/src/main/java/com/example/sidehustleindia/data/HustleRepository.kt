package com.example.sidehustleindia.data

import com.example.sidehustleindia.model.Hustle
import com.example.sidehustleindia.model.UserProfile

object HustleRepository {

    val allHustles = listOf(
        Hustle(
            id = "reel_editor",
            title = "Reel/Shorts Editor",
            description = "Edit short-form content for influencers and small businesses. High demand in India.",
            tags = listOf("Video Editing", "Social Media", "Creativity"),
            estimatedIncome = "₹15,000 - ₹50,000/month"
        ),
        Hustle(
            id = "local_ads",
            title = "Local Ads Manager",
            description = "Run Facebook/Instagram ads for local shops (Gyms, Salons).",
            tags = listOf("Digital Marketing", "Sales", "Social Media"),
            estimatedIncome = "₹20,000 - ₹60,000/month"
        ),
        Hustle(
            id = "website_builder",
            title = "Small Business Website Builder",
            description = "Build simple landing pages using tools like Wix/WordPress or Code.",
            tags = listOf("Coding", "Web Design", "Technical"),
            estimatedIncome = "₹10,000 - ₹40,000/project"
        ),
        Hustle(
            id = "tuition_finder",
            title = "Home Tuition/Coaching",
            description = "Teach school subjects or skills like English speaking.",
            tags = listOf("Teaching", "Communication", "Academic"),
            estimatedIncome = "₹5,000 - ₹20,000/month"
        ),
        Hustle(
            id = "ai_prompter",
            title = "AI Prompt Specialist",
            description = "Generate images/text for businesses using ChatGPT/Midjourney.",
            tags = listOf("Technical", "Creativity", "AI"),
            estimatedIncome = "₹10,000 - ₹50,000/month"
        ),
        Hustle(
            id = "content_writer",
            title = "Content Writer (Hindi/English)",
            description = "Write blogs, scripts, or social media posts.",
            tags = listOf("Writing", "Communication", "Creativity"),
            estimatedIncome = "₹0.50 - ₹2/word"
        )
    )

    fun matchHustles(userProfile: UserProfile): List<Hustle> {
        // Simple matching algorithm (V1)
        // Matches if any user skill matches a hustle tag
        // Or if the hustle has no specific skill requirements (generic)
        
        val userSkills = (userProfile.skills.technical + userProfile.skills.soft + userProfile.skills.specific)
            .map { it.lowercase() }

        return allHustles.filter { hustle ->
            val hustleTags = hustle.tags.map { it.lowercase() }
            // Check for intersection
            val hasMatch = hustleTags.any { tag -> 
                userSkills.any { skill -> skill.contains(tag) || tag.contains(skill) } 
            }
            
            // Boost if city matches logic (future) or if generic
            hasMatch || hustleTags.isEmpty()
        }.sortedByDescending { hustle ->
             // Sort by number of matches
             val hustleTags = hustle.tags.map { it.lowercase() }
             hustleTags.count { tag -> 
                 userSkills.any { skill -> skill.contains(tag) || tag.contains(skill) } 
             }
        }
    }
}
