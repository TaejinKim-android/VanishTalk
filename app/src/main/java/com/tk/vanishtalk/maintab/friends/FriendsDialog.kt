package com.tk.vanishtalk.maintab.friends

import android.app.Dialog
import android.content.Context
import android.view.View
import com.tk.vanishtalk.R
import com.tk.vanishtalk.model.data.local.LocalUser
import kotlinx.android.synthetic.main.dialog_friends.*

class FriendsDialog(context: Context): View.OnClickListener {

    lateinit var friend: LocalUser
    var presenter: FriendsPresenter? = null
    private val dialog = Dialog(context)

    fun showDialog() {

        dialog.setContentView(R.layout.dialog_friends)
        dialog.show()

        dialog.friends_dialog_username.text = friend.name
        dialog.friends_dialog_callbtn.setOnClickListener(this)
        dialog.friends_dialog_chatbtn.setOnClickListener(this)

        if (friend.phoneNum == null) {
            dialog.friends_dialog_callbtn.isEnabled = false
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.friends_dialog_callbtn -> {
                presenter?.callToFriend(friend)
                dialog.dismiss()
            }
            R.id.friends_dialog_chatbtn -> {
                presenter?.chatToFriend(friend)
                dialog.dismiss()
            }
        }
    }
}