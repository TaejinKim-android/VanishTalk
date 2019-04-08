package com.tk.vanishtalk.maintab.chatroom.choosefriends

interface ChooseFriendsContract {

    interface View {

    }

    interface Presenter {
        fun loadAllFriends()
        fun onFriendClick(position: Int): Boolean
        fun loadRoomMembers(): ArrayList<String>
    }
}