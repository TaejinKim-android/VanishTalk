package com.tk.vanishtalk.chat

import com.tk.vanishtalk.model.data.local.LocalChat

interface ChatAdapterContract {

    interface View {
        fun notifyDataChange()
    }

    interface Model {
        var myEmail: String

        fun addChat(chat: LocalChat)
        fun initAllChat(chatList: ArrayList<LocalChat>)
        fun getArrayListSize(): Int
    }
}