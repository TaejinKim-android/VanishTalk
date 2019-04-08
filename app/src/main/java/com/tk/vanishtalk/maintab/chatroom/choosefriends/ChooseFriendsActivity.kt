package com.tk.vanishtalk.maintab.chatroom.choosefriends

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.tk.vanishtalk.BaseActivity
import com.tk.vanishtalk.R
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter

import kotlinx.android.synthetic.main.activity_choose_friends.*
import kotlinx.android.synthetic.main.content_choose_friends.*

class ChooseFriendsActivity : BaseActivity(), ChooseFriendsContract.View {

    lateinit var adapter: ChooseFriendsAdapter
    lateinit var presenter: ChooseFriendsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_friends)
        setSupportActionBar(friends_list_toolbar)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)
            title = getString(R.string.basic_friends_actionbar_name)
        }

        adapter = ChooseFriendsAdapter(this)
        presenter = ChooseFriendsPresenter().apply {
            view = this@ChooseFriendsActivity
            adapterView = adapter
            adapterModel = adapter
            localSQLModel = SQLiteAdapter.getInstance(this@ChooseFriendsActivity)
            localSPModel = SharedPreferencesAdapter.getInstance(this@ChooseFriendsActivity)
        }

        choose_friends_recycler.apply {
            layoutManager = LinearLayoutManager(this@ChooseFriendsActivity, LinearLayoutManager.VERTICAL, false)
            adapter = this@ChooseFriendsActivity.adapter
        }

        presenter.loadAllFriends()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_friends_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.friends_list_menu_confirm -> {
            val roomMemberList = presenter.loadRoomMembers()
            if (roomMemberList.size > 1) {
                val result = Intent()
                result.putStringArrayListExtra(getString(R.string.basic_selected_friends_id), roomMemberList)
                setResult(Activity.RESULT_OK, result)
                finish()
            }

            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
