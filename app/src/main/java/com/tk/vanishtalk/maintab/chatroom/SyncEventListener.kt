package com.tk.vanishtalk.maintab.chatroom

import com.tk.vanishtalk.model.data.remote.RemoteChat
import com.tk.vanishtalk.model.data.remote.RemoteChatRoom

interface SyncEventListener {

    fun onConnect()
    fun onEnrollSocket()
    fun onConnectRoom()
    fun onSyncChat(remoteChatList: List<RemoteChat>)
    fun onSyncChatRoom(remoteChatRoomList: List<RemoteChatRoom>)
}