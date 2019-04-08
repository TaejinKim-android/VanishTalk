package com.tk.vanishtalk.model.data.local

import com.google.gson.annotations.SerializedName

/**
 * GSON Data Class
 * Mostly used to communicate server to modify user's info, such as friends list or user name.
 */
data class LocalUserForNetwork(
    @SerializedName("email") val email: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("img_uri") val imgUri: String?,
    @SerializedName("phone_num") val phoneNum: String?
)