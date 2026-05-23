// data/models/GenreChannelResponse.kt
package com.bacbpl.iptv.jetStram.data.models

import com.bacbpl.iptv.jetStram.data.entities.TvChannel
import com.google.gson.annotations.SerializedName

data class GenreChannelResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("genre")
    val genre: String,
    @SerializedName("data")
    val data: List<GenreChannel>
)

data class GenreChannel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("channel_number")
    val channelNumber: String,
    @SerializedName("local_number")
    val localNumber: String,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("banner")
    val banner: String?,
    @SerializedName("stream_url")
    val streamUrl: String,
    @SerializedName("backup_stream_url")
    val backupStreamUrl: String?,
    @SerializedName("source_type")
    val sourceType: String,
    @SerializedName("multicast_ip")
    val multicastIp: String?,
    @SerializedName("port")
    val port: String?,
    @SerializedName("category_id")
    val categoryId: Int?,
    @SerializedName("genre_id")
    val genreId: Int,
    @SerializedName("is_live")
    val isLive: Int,
    @SerializedName("status")
    val status: String
)

// Convert to TvChannel for player
fun GenreChannel.toTvChannel(): TvChannel {
    return TvChannel(
        id = this.id,
        name = this.name,
        logoUrl = this.logo ?: "",
        streamUrl = this.streamUrl,
        category = "Live TV",
        localNumber = this.localNumber
    )
}