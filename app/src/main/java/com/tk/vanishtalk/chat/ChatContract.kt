package com.tk.vanishtalk.chat

import com.tk.vanishtalk.model.data.local.LocalChatRoom

interface ChatContract {

    interface View {
        fun isNotNewRoomAnymore()
        fun notifyDataChangeOnUiThread()
    }

    interface Presenter {
        fun initSocket()
        fun loadAllChat()
        fun getLastRecyclerItemPosition() : Int
        fun makeChatRoom(chatRoom: LocalChatRoom, msg: String)
        fun sendChat(msg: String)
        fun close()
    }
}