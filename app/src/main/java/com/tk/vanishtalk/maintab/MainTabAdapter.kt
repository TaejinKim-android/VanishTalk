package com.tk.vanishtalk.maintab

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.tk.vanishtalk.maintab.chatroom.ChatRoomFragment
import com.tk.vanishtalk.maintab.friends.FriendsFragment
import com.tk.vanishtalk.maintab.settings.SettingsFragment

class MainTabAdapter(
    fm: FragmentManager,
    val tabCount: Int
): FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return when(position) {
            0 -> FriendsFragment()
            1 -> ChatRoomFragment()
            2 -> SettingsFragment()
            else -> null
        }
    }

    override fun getCount(): Int {
        return tabCount
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}