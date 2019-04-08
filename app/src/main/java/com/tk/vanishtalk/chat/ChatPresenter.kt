package com.tk.vanishtalk.chat

import com.tk.vanishtalk.model.data.local.LocalChat
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.DbStatic
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.data.local.LocalChatForNetwork
import com.tk.vanishtalk.model.data.remote.RemoteChat
import com.tk.vanishtalk.model.data.remote.RemoteChatRoom
import com.tk.vanishtalk.model.repository.ChatRepository
import io.reactivex.disposables.CompositeDisposable

class ChatPresenter: ChatContract.Presenter, ChatEventListener {

    /**
     * Variable for temporary set msg inputted by user.
     * New chat room is created when user clicks 'send msg' button, not user enters to the room to sync
     * chat room info to server.
     *
     * <Logic>
     * User clicks button ->
     * User sends users list in room(JSON) (fun makeChatRoom) ->
     * Server saves chat room info and returns it to client ->
     * Client saves chat room info to SQLite ->
     * User sends msg(fun onMakeChatRoom)
     *
     * Socket does not need user's msg in second logic stage(fun makeChatRoom), and third,
     * but it does in final logic stage(fun onMakeChatRoom)
     * That's the reason why tempMsg is declared.
     *
     * Logic is not clean. Need to be improved.
     */
    private lateinit var tempMsg: String

    var view: ChatContract.View? = null
    lateinit var chatRoom: LocalChatRoom
    lateinit var adapterView: ChatAdapterContract.View
    lateinit var adapterModel: ChatAdapterContract.Model
    lateinit var localSQLModel: SQLiteAdapter
    lateinit var localSPModel: SharedPreferencesAdapter
    lateinit var socket: ChatRepository

    override fun initSocket() {
        socket.chatEventListener = this
    }

    override fun loadAllChat() {
        val roomId = chatRoom.id
        roomId?.let {
            val chatList = localSQLModel.selectAllChat(roomId)
            adapterModel.myEmail = localSPModel.getEmail()!!
            adapterModel.initAllChat(chatList)
            adapterView.notifyDataChange()
        }
    }

    override fun getLastRecyclerItemPosition() = adapterModel.getArrayListSize() - 1

    override fun makeChatRoom(chatRoom: LocalChatRoom, msg: String) {
        tempMsg = msg
        socket.makeRoom(chatRoom)
    }

    override fun sendChat(msg: String) {
        val email = localSPModel.getEmail()
        email?.let {
            val chatForNetwork = LocalChatForNetwork(
                chatRoom.id,
                email,
                DbStatic.VALUE_MSG_TYPE_TEXT,
                msg
            )

            socket.sendChat(chatForNetwork)
        }
    }

    override fun onMakeRoom(remoteChatRoom: RemoteChatRoom) {
        //set chat room id from server
        chatRoom.id = remoteChatRoom.id

        //first chat in this chat room is not sent yet
        chatRoom.lastMsg = "none"
        chatRoom.lastMsgTime = 0

        adapterModel.myEmail = localSPModel.getEmail()!!

        localSQLModel.createChatRoomTable(chatRoom.id!!)
        localSQLModel.insertChatRoomInfo(chatRoom)
        view?.isNotNewRoomAnymore()
        sendChat(tempMsg)
    }

    override fun onEnterRoom() {

    }

    override fun onReceiveChat(remoteChat: RemoteChat) {
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
        adapterModel.addChat(chat)

        //receiving msg event fired by sub thread, cannot use adapterView's notifyDataSetChanged()
        //this makes business logic complicated, need to be improved
        view?.notifyDataChangeOnUiThread()
    }

    override fun close() {
        view = null
    }
}