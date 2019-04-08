package com.tk.vanishtalk.chat

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import kotlinx.android.synthetic.main.activity_chat.*
import android.widget.Toast
import android.util.TypedValue
import com.tk.vanishtalk.BaseActivity
import com.tk.vanishtalk.R
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.repository.ChatRepository


class ChatActivity : BaseActivity(), ChatContract.View, View.OnClickListener {

    lateinit var adapter: ChatAdapter
    lateinit var presenter: ChatPresenter
    private var isNewChatRoom = false
    lateinit var chatRoom: LocalChatRoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(chat_toolbar)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)
            title = getString(R.string.basic_chat_actionbar_name)
        }

        val intent = intent
        isNewChatRoom = intent.getBooleanExtra(getString(R.string.basic_is_new_room), false)
        chatRoom = intent.getSerializableExtra(getString(R.string.basic_chat_room)) as LocalChatRoom

        chat_sendbtn.setOnClickListener(this)

        adapter = ChatAdapter(this)

        presenter = ChatPresenter().apply {
            view = this@ChatActivity
            this.chatRoom = this@ChatActivity.chatRoom
            adapterView = adapter
            adapterModel = adapter
            localSQLModel = SQLiteAdapter.getInstance(this@ChatActivity)
            localSPModel = SharedPreferencesAdapter.getInstance(this@ChatActivity)
            socket = ChatRepository
        }

        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        chat_recycler.apply {
            layoutManager = lm
            adapter = this@ChatActivity.adapter
        }

        presenter.initSocket()

        if (!isNewChatRoom) {
            //If user opens chat room already exists, show the latest msg.
            presenter.loadAllChat()
            chat_recycler.scrollToPosition(presenter.getLastRecyclerItemPosition())
        }

        /**
         * for user convenience
         */

        //position of the latest msg on the screen
        var latestMsgPosition = 0

        //set position of the latest msg on the screen when recyclerView finishes scrolling
        chat_recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        latestMsgPosition = lm.findLastCompletelyVisibleItemPosition()
                    }
                }
            }
        })

        chat_parent.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{

            private var isKeyboardShownBefore: Boolean = false
            private val defaultKeyboardHeightDP = 148.0F
            private val rect = Rect()

            override fun onGlobalLayout() {
                //figure out softKeyboard is shown or not
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    defaultKeyboardHeightDP,
                    chat_parent.resources.displayMetrics
                ).toInt()
                chat_parent.getWindowVisibleDisplayFrame(rect)
                val heightDiff = chat_parent.rootView.height - (rect.bottom - rect.top)
                val isKeyboardShown = heightDiff >= estimatedKeyboardHeight

                //set position of the latest msg on the screen when keyboard is shown
                //execute when activity is created first time, or keyboard is shown after activity is created
                if (isKeyboardShown == isKeyboardShownBefore) {
                    latestMsgPosition = lm.findLastVisibleItemPosition()
                    return
                }

                isKeyboardShownBefore = isKeyboardShown

                //scroll to the latest msg when user is typing; when keyboard is shown
                when(isKeyboardShown) {
                    true -> {
                        if (latestMsgPosition == presenter.getLastRecyclerItemPosition()) {
                            chat_recycler.scrollToPosition(latestMsgPosition)
                        }
                    }
                }
            }
        })

        /**
         * user convenience part end
         */
    }

    override fun isNotNewRoomAnymore() {
        isNewChatRoom = false
    }

    override fun notifyDataChangeOnUiThread() {
        this@ChatActivity.runOnUiThread {
            adapter.notifyDataSetChanged()
            chat_recycler.scrollToPosition(presenter.getLastRecyclerItemPosition())
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.chat_sendbtn -> {
                if (chat_edittext.text.isNotEmpty()) {
                    val msg = chat_edittext.text.toString()
                    when(isNewChatRoom) {
                        true -> presenter.makeChatRoom(chatRoom, msg)
                        false -> presenter.sendChat(msg)
                    }
                    chat_edittext.text = null
                    chat_recycler.scrollToPosition(presenter.getLastRecyclerItemPosition())
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_chat, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.close()
    }
}
