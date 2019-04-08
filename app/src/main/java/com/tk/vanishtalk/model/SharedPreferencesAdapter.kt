package com.tk.vanishtalk.model

import android.content.Context
import com.tk.vanishtalk.model.data.local.LocalUser

class SharedPreferencesAdapter private constructor(context: Context) {

    companion object {
        @Volatile private var INSTANCE: SharedPreferencesAdapter? = null

        fun getInstance(context: Context): SharedPreferencesAdapter {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = SharedPreferencesAdapter(context)
                }
            }

            return INSTANCE!!
        }
    } //Singleton

    private val sharedPreferences = context.getSharedPreferences(DbStatic.DB_NAME, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun setCurrentUserData(user: LocalUser) {
        editor.putString(DbStatic.SHARED_CURRENT_USER_EMAIL_KEY, user.email)
        editor.putString(DbStatic.SHARED_CURRENT_USER_NAME_KEY, user.name)
        editor.putString(DbStatic.SHARED_CURRENT_USER_IMG_URI_KEY, user.imgUri)
        editor.putString(DbStatic.SHARED_CURRENT_USER_PHONE_NUM_KEY, user.phoneNum)
        editor.apply()
    }

    fun setUpdatedUserData(user: LocalUser) {
        editor.putString(DbStatic.SHARED_CURRENT_USER_NAME_KEY, user.name)
        editor.putString(DbStatic.SHARED_CURRENT_USER_IMG_URI_KEY, user.imgUri)
        editor.apply()
    }

    fun setCurrentUserImg(userImgUri: String) {
        editor.putString(DbStatic.SHARED_CURRENT_USER_IMG_URI_KEY, userImgUri)
        editor.apply()
    }

    fun getCurrentUserData() = LocalUser(
        sharedPreferences.getString(DbStatic.SHARED_CURRENT_USER_EMAIL_KEY, null),
        sharedPreferences.getString(DbStatic.SHARED_CURRENT_USER_NAME_KEY, null),
        sharedPreferences.getString(DbStatic.SHARED_CURRENT_USER_IMG_URI_KEY, null),
        sharedPreferences.getString(DbStatic.SHARED_CURRENT_USER_EMAIL_KEY, null),
        sharedPreferences.getString(DbStatic.SHARED_CURRENT_USER_PHONE_NUM_KEY, null)
    )

    fun getEmail(): String? = sharedPreferences.getString(DbStatic.SHARED_CURRENT_USER_EMAIL_KEY, null)

    fun clearCurrentUserData() {
        editor.clear().apply()
    }
}