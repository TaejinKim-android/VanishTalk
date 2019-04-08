package com.tk.vanishtalk.model.data.local

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LocalChatRoom(
    @SerializedName("id") var id: String?,
    @SerializedName("name") val name: String,
    @SerializedName("userListJson") val userListJson: String,
    @SerializedName("lastMsg") var lastMsg: String?,
    @SerializedName("lastMsgId") var lastMsgId: Long?,
    @SerializedName("lastMsgTime") var lastMsgTime: Long?
): Serializable
