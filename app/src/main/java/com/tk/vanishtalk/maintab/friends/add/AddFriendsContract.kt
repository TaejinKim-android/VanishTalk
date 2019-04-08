package com.tk.vanishtalk.maintab.friends.add

import com.tk.vanishtalk.model.data.local.LocalUser
import io.reactivex.subjects.Subject

interface AddFriendsContract {

    interface View {
        fun showInputValueStatus(isMyEmail: Boolean)
        fun showProgress()
        fun hideProgress()
        fun showAlert()
        fun sendResultToFriendsFragment(friend: LocalUser)
    }

    interface Presenter {
        fun startTextWatch(textSubject: Subject<String>)
        fun addFriend(friendEmail: String)
        fun close()
    }
}