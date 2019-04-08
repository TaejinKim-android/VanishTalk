package com.tk.vanishtalk.maintab.chatroom.choosefriends

import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter

class ChooseFriendsPresenter: ChooseFriendsContract.Presenter {

    var view: ChooseFriendsContract.View? = null
    lateinit var adapterModel: ChooseFriendsAdapterContract.Model
    var adapterView: ChooseFriendsAdapterContract.View? = null
        set(value) {
            field = value
            field?.onFriendsClickListenerCallback = { onFriendClick(it) }
        }

    lateinit var localSQLModel: SQLiteAdapter
    lateinit var localSPModel: SharedPreferencesAdapter

    override fun loadAllFriends() {
        val email = localSPModel.getEmail()
        email?.let {
            val friendsList = localSQLModel.selectAllFriends(it)
            adapterModel.initAllFriends(friendsList)
            adapterView?.notifyDataChange()
        }
    }

    override fun onFriendClick(position: Int) = adapterModel.changeFriendSelectedStatus(position)

    override fun loadRoomMembers(): ArrayList<String> {
        val roomMemberList = adapterModel.selectChosenFriendsId()
        val email = localSPModel.getEmail()
        email?.let {
            roomMemberList.add(it)
        }

        return roomMemberList
    }
}