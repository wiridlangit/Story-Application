package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    storyRepository: StoryRepository?
) : ViewModel() {

    val storiesLiveData: LiveData<PagingData<ListStoryItem>> = storyRepository?.getStories() ?: MutableLiveData()

    init {
        loadStories()
    }

    private fun loadStories() {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Stories loaded successfully")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading stories", e)
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
