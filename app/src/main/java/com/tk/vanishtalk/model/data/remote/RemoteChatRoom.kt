package com.tk.vanishtalk.model.data.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RemoteChatRoom(
    @SerializedName("_id") val id: String,
    @SerializedName("user_list_json") val userListJson: String,
    @SerializedName("create_time") val createTime: Long
): Serializable