package com.tk.vanishtalk.model.data.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RemoteChat(
    @SerializedName("_id") val chatId: Long,
    @SerializedName("room_id") val roomId: String,
    @SerializedName("email") val senderEmail: String,
    @SerializedName("type") val type: Int,
    @SerializedName("content") val content: String,
    @SerializedName("read_count") val readCount: Int,
    @SerializedName("send_time") val sendTime: Long
): Serializable