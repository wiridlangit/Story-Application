package com.dicoding.picodiploma.loginwithanimation.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.story.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryAdapter
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var token: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        lifecycleScope.launch {
            val userPreference = Injection.provideUserPreference(applicationContext)
            token = userPreference.getToken().first() ?: ""

            if (token.isEmpty()) {
                navigateToWelcome()
                return@launch
            }

            val isLoggedIn = userPreference.getSession().first().isLogin

            if (isLoggedIn) {
                try {
                    ViewModelFactory.updateStoryRepository(applicationContext)
                    val factory = ViewModelFactory.getInstance(applicationContext)
                    viewModel = ViewModelProvider(this@MainActivity, factory)[MainViewModel::class.java]

                    viewModel.getSession().observe(this@MainActivity) { user ->
                        if (!user.isLogin) {
                            navigateToWelcome()
                        }
                    }

                    Log.d("MainActivity", "Setting up observer for storiesLiveData")
                    viewModel.storiesLiveData.observe(this@MainActivity) { pagingData ->
                        Log.d("MainActivity", "storiesLiveData observed")
                        showLoading(true)
                        val storyAdapter = StoryAdapter()
                        binding.recyclerView.adapter = storyAdapter
                        storyAdapter.submitData(lifecycle, pagingData)
                        showLoading(false)
                    }

                } catch (e: IllegalStateException) {
                    Log.e("MainActivity", "IllegalStateException caught", e)
                    navigateToWelcome()
                }
            } else {
                navigateToWelcome()
            }
        }

        setupView()
        setupAction()
    }

    private fun navigateToWelcome() {
        val intent = Intent(this, WelcomeActivity::class.java)
        Log.d("MainActivity", "Navigating to WelcomeActivity")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
    }

    private fun setupAction() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            intent.putExtra("TOKEN", token)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                lifecycleScope.launch {
                    viewModel.logout()
                    Log.d("MainActivity", "User logged out, navigating to WelcomeActivity")
                    navigateToWelcome()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
