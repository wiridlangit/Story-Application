package com.dicoding.picodiploma.loginwithanimation.data.api

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StoryResponse(
    @SerializedName("listStory") val stories: List<ListStoryItem>
)

@Entity(tableName = "story")
data class ListStoryItem(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("photoUrl") val photoUrl: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?
) : Serializable

