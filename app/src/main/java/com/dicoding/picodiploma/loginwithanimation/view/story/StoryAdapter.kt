package com.dicoding.picodiploma.loginwithanimation.view.story

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.api.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.model.Story

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    var onNewDataLoaded: ((List<ListStoryItem>) -> Unit)? = null

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    init {
        addLoadStateListener { loadStates ->
            if (loadStates.refresh is LoadState.NotLoading || loadStates.append is LoadState.NotLoading) {
                val newData = snapshot().items
                onNewDataLoaded?.invoke(newData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let {
            holder.storyTitle.text = it.name
            holder.storyDescription.text = it.description?.substring(0, minOf(it.description.length, 100)) + "..."

            Glide.with(holder.itemView.context)
                .load(it.photoUrl)
                .into(holder.storyImage)

            holder.cardView.setOnClickListener {
                val storyItem = Story(
                    id = story.id ?: "",
                    name = story.name ?: "",
                    description = story.description ?: "",
                    photoUrl = story.photoUrl ?: "",
                    createdAt = story.createdAt ?: "",
                    lat = story.lat ?: 0.0,
                    lon = story.lon ?: 0.0
                )
                val intent = Intent(holder.itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("story", storyItem)

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    androidx.core.util.Pair(holder.storyImage, "image"),
                    androidx.core.util.Pair(holder.storyTitle, "title"),
                    androidx.core.util.Pair(holder.storyDescription, "description")
                )

                holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cardView)
        val storyTitle: TextView = view.findViewById(R.id.storyTitle)
        val storyDescription: TextView = view.findViewById(R.id.storyDescription)
        val storyImage: ImageView = view.findViewById(R.id.storyImage)
    }
}
