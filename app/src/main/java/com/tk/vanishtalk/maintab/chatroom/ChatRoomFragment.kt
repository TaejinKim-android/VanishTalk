package com.tk.vanishtalk.maintab.chatroom


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*

import com.tk.vanishtalk.R
import com.tk.vanishtalk.chat.ChatActivity
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.maintab.chatroom.choosefriends.ChooseFriendsActivity
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import kotlinx.android.synthetic.main.fragment_chat_room_list.view.*


class ChatRoomFragment : Fragment(), ChatRoomContract.View {

    private var adapter: ChatRoomAdapter? = null
    private var presenter: ChatRoomPresenter? = null
    lateinit var fragmentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContext = requireContext()

        if (adapter == null) {
            adapter = ChatRoomAdapter(fragmentContext)
        }

        if (presenter == null) {
            presenter = ChatRoomPresenter().apply {
                view = this@ChatRoomFragment
                adapterView = adapter
                adapterModel = adapter!!
                localSQLModel = SQLiteAdapter.getInstance(fragmentContext)
                localSPModel = SharedPreferencesAdapter.getInstance(fragmentContext)
            }
        }

        val rootView =  inflater.inflate(R.layout.fragment_chat_room_list, container, false)

        with(rootView) {
            chat_room_recycler.apply {
                layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
                adapter = this@ChatRoomFragment.adapter
            }

            setHasOptionsMenu(true) //set actionbar menu
            retainInstance = true //retain current values when device rotated
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        presenter?.loadAllChatRoom()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_chatroom, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.chat_room_menu_addchatroom -> {
                val intent = Intent(fragmentContext, ChooseFriendsActivity::class.java)
                startActivityForResult(intent, CHOOSE_FRIEND)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun notifyDataChangeOnUiThread() {
        activity?.runOnUiThread {
           adapter?.notifyDataSetChanged()
        }
    }

    override fun openExistChatRoom(chatRoom: LocalChatRoom) {
        val intent = Intent(fragmentContext, ChatActivity::class.java)
        intent.putExtra(getString(R.string.basic_chat_room), chatRoom)
        intent.putExtra(getString(R.string.basic_is_new_room), false)
        activity?.startActivity(intent)
    }

    override fun openNewChatRoom(chatRoom: LocalChatRoom) {
        val intent = Intent(fragmentContext, ChatActivity::class.java)
        intent.putExtra(getString(R.string.basic_chat_room), chatRoom)
        intent.putExtra(getString(R.string.basic_is_new_room), true)
        activity?.startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when(requestCode) {
                CHOOSE_FRIEND -> {
                    val selectedFriendsId = data?.getStringArrayListExtra(getString(R.string.basic_selected_friends_id))
                    presenter?.createChatRoom(selectedFriendsId!!)
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        presenter?.close()
    }

    companion object {
        const val CHOOSE_FRIEND = 26
    }
}
