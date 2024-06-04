package com.dicoding.picodiploma.loginwithanimation.view.story

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.model.Story

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {

    private lateinit var storyImage: ImageView
    private lateinit var storyTitle: TextView
    private lateinit var storyDescription: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_story)

        storyImage = findViewById(R.id.storyImage)
        storyTitle = findViewById(R.id.storyTitle)
        storyDescription = findViewById(R.id.storyDescription)

        val story = intent.getSerializableExtra("story") as? Story
        story?.let {
            storyTitle.text = it.name
            storyDescription.text = it.description
            Glide.with(this)
                .load(it.photoUrl)
                .into(storyImage)
        }
    }
}
