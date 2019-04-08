package com.tk.vanishtalk.maintab.friends

import com.tk.vanishtalk.model.data.local.LocalUser

interface FriendsAdapterContract {

    interface View {
        var onFriendsClickListenerCallback: ((LocalUser) -> Unit)?
        fun notifyDataChange()
    }

    interface Model {
        fun addFriends(user: LocalUser)
        fun initAllFriends(friendsList: ArrayList<LocalUser>)
    }
}