package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    private fun provideApiService(context: Context): ApiService {
        val pref = provideUserPreference(context)
        val token = runBlocking { pref.getToken().first() ?: "" }
        Log.d("Injection", "Providing ApiService with token: $token")
        return ApiConfig.getApiService(token)
    }

    fun provideUserPreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = provideUserPreference(context)
        val apiService = provideApiService(context)
        return UserRepository.getInstance(pref, apiService)
    }

    suspend fun provideStoryRepository(context: Context): StoryRepository? {
        val pref = provideUserPreference(context)
        val token = runBlocking { pref.getToken().first() ?: "" }
        return if (token.isNotEmpty()) {
            val apiService = provideApiService(context)
            StoryRepository.getInstance(apiService, pref)
        } else {
            Log.e("Injection", "User is not logged in")
            null
        }
    }
}
