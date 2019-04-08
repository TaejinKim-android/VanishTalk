package com.tk.vanishtalk.model.data.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RemoteUser(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("img_uri") val imgUri: String?,
    @SerializedName("friend_list_json") val friendsEmailList: String,
    @SerializedName("phone_num") val phoneNum: String?
): Serializable