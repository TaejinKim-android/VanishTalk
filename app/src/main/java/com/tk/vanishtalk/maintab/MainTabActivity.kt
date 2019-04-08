package com.tk.vanishtalk.maintab

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import com.tk.vanishtalk.BaseActivity
import com.tk.vanishtalk.R
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.repository.ChatRepository

import kotlinx.android.synthetic.main.activity_main_tab.*
import kotlinx.android.synthetic.main.content_main_tab.*

class MainTabActivity : BaseActivity(), MainTabContract.View {

    private var presenter: MainTabPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tab)
        setSupportActionBar(maintab_toolbar)
        supportActionBar?.title = getString(R.string.basic_friends_actionbar_name)

        if (presenter == null) {
            presenter = MainTabPresenter().apply {
                view = this@MainTabActivity
                localSQLModel = SQLiteAdapter.getInstance(this@MainTabActivity)
                localSPModel = SharedPreferencesAdapter.getInstance(this@MainTabActivity)
                socket = ChatRepository
            }
        }

        presenter?.connectSocket()

        maintab_tablayout.addTab(maintab_tablayout.newTab().setText(R.string.basic_friends_actionbar_name))
        maintab_tablayout.addTab(maintab_tablayout.newTab().setText(R.string.basic_chat_room_actionbar_name))
        maintab_tablayout.addTab(maintab_tablayout.newTab().setText(R.string.basic_settings_actionbar_name))

        val viewPagerAdapter = MainTabAdapter(supportFragmentManager, maintab_tablayout.tabCount)
        maintab_viewpager.adapter = viewPagerAdapter
        maintab_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(maintab_tablayout))

        maintab_tablayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab) {
                maintab_viewpager.currentItem = p0.position
                when(p0.position) {
                    0 -> supportActionBar?.title = getString(R.string.basic_friends_actionbar_name)
                    1 -> {
                        supportActionBar?.title = getString(R.string.basic_chat_room_actionbar_name)
                        viewPagerAdapter.notifyDataSetChanged()
                    }
                    else -> supportActionBar?.title = getString(R.string.basic_settings_actionbar_name)
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.close()
    }
}
