package com.tk.vanishtalk.model.data.local

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LocalChat(
    @SerializedName("id") val id: Long,
    @SerializedName("email") val email: String,
    @SerializedName("type") val type: Int,
    @SerializedName("content") val content: String,
    @SerializedName("sendTime") val sendTime: Long,
    @SerializedName("readCount") var readCount: Int
): Serializable