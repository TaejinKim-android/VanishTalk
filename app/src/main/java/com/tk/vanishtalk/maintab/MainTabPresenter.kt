package com.tk.vanishtalk.maintab

import com.tk.vanishtalk.maintab.chatroom.SyncEventListener
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.data.local.LocalChat
import com.tk.vanishtalk.model.data.local.LocalUserForNetwork
import com.tk.vanishtalk.model.data.remote.RemoteChat
import com.tk.vanishtalk.model.data.remote.RemoteChatRoom
import com.tk.vanishtalk.model.repository.ChatRepository
import com.tk.vanishtalk.util.Utils

class MainTabPresenter: MainTabContract.Presenter, SyncEventListener {

    var view: MainTabContract.View? = null
    lateinit var localSQLModel: SQLiteAdapter
    lateinit var localSPModel: SharedPreferencesAdapter
    lateinit var socket: ChatRepository

    override fun connectSocket() {
        socket.syncEventListener = this
        socket.connect()
    }

    override fun onConnect() {
        val email = localSPModel.getEmail()
        email?.let {
            //to enroll socket id to server
            val user = LocalUserForNetwork(it, null, null, null)
            socket.enrollSocket(user)
        }
    }

    override fun onEnrollSocket() {
        val email = localSPModel.getEmail()
        email?.let {
            val roomList = localSQLModel.selectAllChatRoom(it)
            socket.connectRooms(roomList)
        }
    }

    override fun onConnectRoom() {
        val email = localSPModel.getEmail()
        email?.let {
            val roomList = localSQLModel.selectAllChatRoom(it)
            socket.syncChat(roomList)
        }
    }

    override fun onSyncChat(remoteChatList: List<RemoteChat>) {
        //Server socket emits chat array room by room, so every chat's roomId is same.
        if (remoteChatList.isNotEmpty()) {
            localSQLModel.createChatRoomTable(remoteChatList[0].roomId) //create if not exists
            for (remoteChat in remoteChatList) {
                val chat = LocalChat(
                    remoteChat.chatId,
                    remoteChat.senderEmail,
                    remoteChat.type,
                    remoteChat.content,
                    remoteChat.sendTime,
                    remoteChat.readCount
                )
                localSQLModel.insertChat(remoteChat.roomId, chat)
                localSQLModel.updateChatRoomStatus(remoteChat.roomId, chat)
            }
        }
    }

    override fun onSyncChatRoom(remoteChatRoomList: List<RemoteChatRoom>) {
        for (remoteChatRoom in remoteChatRoomList) {
            val chatRoom = Utils.remoteChatRoomToLocalChatRoom(remoteChatRoom)
            localSQLModel.insertChatRoomInfo(chatRoom)
        }
    }

    override fun close() {
        socket.syncEventListener = null
        view = null
    }
}