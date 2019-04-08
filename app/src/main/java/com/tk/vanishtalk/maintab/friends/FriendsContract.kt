package com.tk.vanishtalk.maintab.friends

import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.model.data.local.LocalUser

interface FriendsContract {

    interface View {
        fun setMyInfo(me: LocalUser)
        fun openNewChatRoom(chatRoom: LocalChatRoom)
        fun openExistChatRoom(chatRoom: LocalChatRoom)
        fun startAddFriendActivity()
        fun showFriend(friend: LocalUser)
        fun startCallFriendIntent(phoneNum: String)
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter {
        fun loadMyInfo()
        fun loadAllFriends()
        fun addNewFriend()
        fun sendNewFriendToAdapter(friend: LocalUser)
        fun onFriendClick(friend: LocalUser)
        fun callToFriend(friend: LocalUser)
        fun chatToFriend(friend: LocalUser)
        fun close()
    }
}