package com.tk.vanishtalk.maintab.chatroom

import com.tk.vanishtalk.model.data.local.LocalChatRoom

interface ChatRoomAdapterContract {

    interface View {
        var onRoomClickListenerCallback: (LocalChatRoom) -> Unit
        fun notifyDataChange()
    }

    interface Model {
        fun initAllChatRoom(chatRoomList: ArrayList<LocalChatRoom>)
    }
}