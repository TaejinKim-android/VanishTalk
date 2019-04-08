package com.tk.vanishtalk.model.data.local

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LocalChatForNetwork(
    @SerializedName("roomId") val roomId: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("type") val type: Int?,
    @SerializedName("content") val content: String?
): Serializable