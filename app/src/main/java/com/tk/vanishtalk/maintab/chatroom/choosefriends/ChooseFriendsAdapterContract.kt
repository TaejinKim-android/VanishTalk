package com.tk.vanishtalk.maintab.chatroom.choosefriends

import com.tk.vanishtalk.model.data.local.LocalUser

interface ChooseFriendsAdapterContract {

    interface View {
        fun notifyDataChange()
        var onFriendsClickListenerCallback: ((Int) -> Boolean)?
    }

    interface Model {
        fun initAllFriends(friendsList: ArrayList<LocalUser>)
        fun changeFriendSelectedStatus(position: Int): Boolean
        fun selectChosenFriendsId(): ArrayList<String>
    }
}