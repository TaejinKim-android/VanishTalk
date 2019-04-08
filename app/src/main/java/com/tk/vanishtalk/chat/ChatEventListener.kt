package com.tk.vanishtalk.chat

import com.tk.vanishtalk.model.data.remote.RemoteChat
import com.tk.vanishtalk.model.data.remote.RemoteChatRoom

interface ChatEventListener {

    //fun onSyncChat() //callback excuted when app is start; retrieve all unreceived messages when socket has been closed
    fun onMakeRoom(remoteChatRoom: RemoteChatRoom)
    fun onEnterRoom() //callback executed when room member enters include myself
    fun onReceiveChat(remoteChat: RemoteChat)
}