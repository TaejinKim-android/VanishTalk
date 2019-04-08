package com.tk.vanishtalk.maintab.chatroom

import com.tk.vanishtalk.model.data.local.LocalChatRoom

interface ChatRoomContract {

    interface View {
        fun notifyDataChangeOnUiThread()
        fun openNewChatRoom(chatRoom: LocalChatRoom)
        fun openExistChatRoom(chatRoom: LocalChatRoom)
    }

    interface Presenter {
        fun loadAllChatRoom()
        fun createChatRoom(friendsUserIds: ArrayList<String>)
        fun onRoomClick(chatRoom: LocalChatRoom)
        fun close()
    }
}