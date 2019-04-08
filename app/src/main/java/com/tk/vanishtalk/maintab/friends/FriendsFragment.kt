package com.tk.vanishtalk.maintab.friends

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions

import com.tk.vanishtalk.R
import com.tk.vanishtalk.chat.ChatActivity
import com.tk.vanishtalk.maintab.MainTabActivity
import com.tk.vanishtalk.maintab.friends.add.AddFriendsActivity
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.model.data.local.LocalUser
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.android.synthetic.main.fragment_friends.view.*


class FriendsFragment : Fragment(), FriendsContract.View {

    private var adapter: FriendsAdapter? = null
    private var presenter: FriendsPresenter? = null
    lateinit var fragmentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContext = requireContext()

        if (adapter == null) {
            adapter = FriendsAdapter(fragmentContext)
        }

        if (presenter == null) {
            presenter = FriendsPresenter().apply {
                view = this@FriendsFragment
                adapterView = adapter
                adapterModel = adapter
                localSQLModel = SQLiteAdapter.getInstance(fragmentContext)
                localSPModel = SharedPreferencesAdapter.getInstance(fragmentContext)
            }
        }

        val rootView =  inflater.inflate(R.layout.fragment_friends, container, false)

        with(rootView) {
            friends_recycler.apply {
                layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
                adapter = this@FriendsFragment.adapter
            }

            setHasOptionsMenu(true) //set actionbar menu
            retainInstance = true //retain current values when device rotated
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.loadMyInfo()
        presenter?.loadAllFriends()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_friends, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when(item?.itemId) {
        R.id.friends_menu_addfriend -> {
            startAddFriendActivity()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun setMyInfo(me: LocalUser) {
        friends_name.text = me.name
        me.imgUri?.let {
            Glide.with(this)
                .load(me.imgUri)
                .apply(RequestOptions.overrideOf(96, 96))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .thumbnail(0.5F)
                .circleCrop()
                .into(friends_userimage)
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

    override fun startAddFriendActivity() {
        val intent = Intent(fragmentContext, AddFriendsActivity::class.java)
        startActivityForResult(intent, ADD_FRIEND)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_FRIEND && resultCode == RESULT_OK) {
            presenter?.sendNewFriendToAdapter(data?.extras?.getSerializable("friend") as LocalUser)
        }
    }

    override fun showFriend(friend: LocalUser) {
        val dialog = FriendsDialog(fragmentContext).apply {
            this.friend = friend
            this.presenter = this@FriendsFragment.presenter!!
        }
        dialog.showDialog()
    }

    override fun startCallFriendIntent(phoneNum: String) {
        val num = "tel:$phoneNum"
        val intent = Intent("android.intent.action.DIAL", Uri.parse(num))
        startActivity(intent)
    }

    override fun showProgress() {
        val activity = activity as MainTabActivity
        activity.showProgressDialog(R.string.basic_loading)
    }

    override fun hideProgress() {
        val activity = activity as MainTabActivity
        activity.hideProgressDialog()
    }

    override fun onDetach() {
        super.onDetach()
        presenter?.close()
    }

    companion object {
        const val ADD_FRIEND = 26
    }
}
