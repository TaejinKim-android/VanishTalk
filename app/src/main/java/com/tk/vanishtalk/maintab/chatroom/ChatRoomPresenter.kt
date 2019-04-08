package com.tk.vanishtalk.maintab.chatroom

import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.util.Utils
import java.io.Serializable

class ChatRoomPresenter: ChatRoomContract.Presenter, Serializable {

    var view: ChatRoomContract.View? = null
    lateinit var adapterModel: ChatRoomAdapterContract.Model
    var adapterView: ChatRoomAdapterContract.View? = null
    set(value) {
        field = value
        field?.onRoomClickListenerCallback = { onRoomClick(it) }
    }

    lateinit var localSQLModel: SQLiteAdapter
    lateinit var localSPModel: SharedPreferencesAdapter

    override fun loadAllChatRoom() {
        val email = localSPModel.getEmail()
        email?.let {
            val chatRoomList = localSQLModel.selectAllChatRoom(it)
            adapterModel.initAllChatRoom(chatRoomList)
            adapterView?.notifyDataChange()
            }
    }

    override fun createChatRoom(friendsUserIds: ArrayList<String>) {
        //id will be set by server; mongoDB document _id
        val chatRoom = LocalChatRoom(
            null,
            Utils.makeDefaultRoomName(friendsUserIds),
            Utils.arrayListToJson(friendsUserIds),
            null,
            null,
            null
        )

        view?.openNewChatRoom(chatRoom)
    }

    override fun onRoomClick(chatRoom: LocalChatRoom) {
        view?.openExistChatRoom(chatRoom)
    }

    override fun close() {
        view = null
    }
}