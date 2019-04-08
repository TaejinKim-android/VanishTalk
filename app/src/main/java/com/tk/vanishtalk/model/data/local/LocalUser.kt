package com.tk.vanishtalk.model.data.local

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LocalUser(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("img_uri") var imgUri: String?,
    @SerializedName("friend_email") val friendEmail: String,
    @SerializedName("phone_num") var phoneNum: String?
): Serializable {
    private var isSelected: Boolean = false

    fun changeIsSelectedStatus(): Boolean {
        this.isSelected = isSelected.not()
        return this.isSelected
    }

    fun getIsSelected(): Boolean = isSelected
}