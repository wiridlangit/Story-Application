package com.dicoding.picodiploma.loginwithanimation.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.repository.UserRepository
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private var storyRepository: StoryRepository?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                var isUserLoggedIn: Boolean
                runBlocking { isUserLoggedIn = userRepository.isUserLoggedIn().first() }
                Log.d("ViewModelFactory", "storyRepository: $storyRepository, isUserLoggedIn: $isUserLoggedIn")
                if (storyRepository != null && isUserLoggedIn) {
                    MainViewModel(userRepository, storyRepository!!) as T
                } else {
                    throw IllegalStateException("User is not logged in")
                }
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val instance: ViewModelFactory
                runBlocking {
                    val userRepository = Injection.provideUserRepository(context)
                    val storyRepository = Injection.provideStoryRepository(context)
                    instance = ViewModelFactory(userRepository, storyRepository)
                }
                INSTANCE = instance
                instance
            }
        }

        suspend fun updateStoryRepository(context: Context) {
            val storyRepository = withContext(Dispatchers.IO) { Injection.provideStoryRepository(context) }
            INSTANCE?.storyRepository = storyRepository
        }
    }
}
