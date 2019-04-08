package com.tk.vanishtalk.maintab.chatroom.choosefriends

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tk.vanishtalk.R
import com.tk.vanishtalk.model.data.local.LocalUser

class ChooseFriendsAdapter(val context: Context): RecyclerView.Adapter<ChooseFriendsAdapter.ViewHolder>(),
    ChooseFriendsAdapterContract.View, ChooseFriendsAdapterContract.Model {

    override var onFriendsClickListenerCallback: ((Int) -> Boolean)? = null
    var friendsList = ArrayList<LocalUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ChooseFriendsAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.recyclerview_friends_list,
                parent,
                false),
            context, onFriendsClickListenerCallback
        )
    }

    override fun getItemCount() = friendsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friendsList[position]
        holder.bind(friend, position)
    }

    override fun initAllFriends(friendsList: ArrayList<LocalUser>) {
        this.friendsList = friendsList
    }

    override fun notifyDataChange() {
        notifyDataSetChanged()
    }

    override fun changeFriendSelectedStatus(position: Int): Boolean {
        return friendsList[position].changeIsSelectedStatus()
    }

    override fun selectChosenFriendsId(): ArrayList<String> {
        val selectedFriendsUserIds = ArrayList<String>()
        for (item in friendsList) {
            if (item.getIsSelected()) {
                selectedFriendsUserIds.add(item.email)
            }
        }

        return selectedFriendsUserIds
    }

    class ViewHolder(val view: View, val context: Context,
                     val onFriendsClickListenerCallback: ((Int) -> Boolean)?): RecyclerView.ViewHolder(view) {
        private val userImage = view.findViewById<ImageView>(R.id.add_friends_recycler_image)
        private val userName = view.findViewById<TextView>(R.id.add_friends_recycler_name)
        private val checkBox = view.findViewById<CheckBox>(R.id.add_friends_recycler_checkbox)

        fun bind(user: LocalUser, position: Int) {
            user.imgUri?.let {
                Glide.with(context)
                    .load(user.imgUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.ic_launcher)
                    .apply(RequestOptions.overrideOf(256, 256))
                    .thumbnail(0.5F)
                    .circleCrop()
                    .into(userImage)
            }
            userName.text = user.name
            view.setOnClickListener {
                checkBox.isChecked = onFriendsClickListenerCallback!!.invoke(position)
            }
        }
    }
}