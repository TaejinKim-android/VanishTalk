package com.tk.vanishtalk.model.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tk.vanishtalk.chat.ChatEventListener
import com.tk.vanishtalk.maintab.chatroom.SyncEventListener
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.model.data.local.LocalChatForNetwork
import com.tk.vanishtalk.model.data.local.LocalUserForNetwork
import com.tk.vanishtalk.model.data.remote.RemoteChat
import com.tk.vanishtalk.model.data.remote.RemoteChatRoom
import com.tk.vanishtalk.network.RetrofitClient
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

object ChatRepository {

    const val EVENT_CONNECT_ROOMS = "connect rooms"
    const val EVENT_ENROLL_SOCKET_ID = "enroll socket"
    const val EVENT_SYNC_MESSAGE = "sync chat"
    const val EVENT_SYNC_ROOM = "sync room"
    const val EVENT_MAKE_ROOM = "make room"
    const val EVENT_ENTER_ROOM = "enter room"
    const val EVENT_SEND_MESSAGE = "send chat"
    const val EVENT_RECEIVE_MESSAGE = "receive chat"

    var socket: Socket = IO.socket("${RetrofitClient.BASE_URL}:3000")
    lateinit var chatEventListener: ChatEventListener
    var syncEventListener: SyncEventListener? = null

    fun connect() {
        socket.on(Socket.EVENT_CONNECT, onConnect())
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect())
        socket.on(EVENT_ENROLL_SOCKET_ID, onEnrollSocket())
        socket.on(EVENT_CONNECT_ROOMS, onConnectRooms())
        socket.on(EVENT_SYNC_MESSAGE, onSyncChat())
        socket.on(EVENT_SYNC_ROOM, onSyncRoom())
        socket.on(EVENT_MAKE_ROOM, onMakeRoom())
        socket.on(EVENT_ENTER_ROOM, onEnterRoom())
        socket.on(EVENT_RECEIVE_MESSAGE, onReceiveChat())
        socket.connect()
    }

    fun isConnected(): Boolean = socket.connected()

    fun disconnect() {
        socket.disconnect()
    }

    fun enrollSocket(user: LocalUserForNetwork) {
        socket.emit(EVENT_ENROLL_SOCKET_ID, Gson().toJson(user))
    }

    fun connectRooms(chatRooms: ArrayList<LocalChatRoom>) {
        socket.emit(EVENT_CONNECT_ROOMS, Gson().toJson(chatRooms))
    }

    fun syncChat(chatRooms: ArrayList<LocalChatRoom>) {
        socket.emit(EVENT_SYNC_MESSAGE, Gson().toJson(chatRooms))
    }

    fun makeRoom(chatRoom: LocalChatRoom) {
        socket.emit(EVENT_MAKE_ROOM, Gson().toJson(chatRoom))
    }

    fun enterRoom(chatRooms: ArrayList<LocalChatRoom>) {
        socket.emit(EVENT_ENTER_ROOM, Gson().toJson(chatRooms))
    }

    fun sendChat(chat: LocalChatForNetwork) {
        socket.emit(EVENT_SEND_MESSAGE, Gson().toJson(chat))
    }

    private fun onConnect() = Emitter.Listener {
        syncEventListener?.onConnect()
    }

    private fun onDisconnect() = Emitter.Listener {
        socket.disconnect()
    }

    private fun onEnrollSocket() = Emitter.Listener {
        syncEventListener?.onEnrollSocket()
    }

    private fun onConnectRooms() = Emitter.Listener {
        syncEventListener?.onConnectRoom()
    }

    private fun onSyncChat() = Emitter.Listener {
        val listType = object : TypeToken<List<RemoteChat>>() {}.type
        val chatList = Gson().fromJson<List<RemoteChat>>(it[0].toString(), listType)
        syncEventListener?.onSyncChat(chatList)
    }

    private fun onSyncRoom() = Emitter.Listener {
        val listType = object : TypeToken<List<RemoteChatRoom>>() {}.type
        val roomList = Gson().fromJson<List<RemoteChatRoom>>(it[0].toString(), listType)
        syncEventListener?.onSyncChatRoom(roomList)

    }

    private fun onMakeRoom() = Emitter.Listener {
        val chatRoom = Gson().fromJson<RemoteChatRoom>(it[0].toString(), RemoteChatRoom::class.java)
        chatEventListener.onMakeRoom(chatRoom)
    }

    private fun onEnterRoom() = Emitter.Listener {
        chatEventListener.onEnterRoom()
    }

    private fun onReceiveChat() = Emitter.Listener {
        val chat = Gson().fromJson<RemoteChat>(it[0].toString(), RemoteChat::class.java)
        chatEventListener.onReceiveChat(chat)
    }
}