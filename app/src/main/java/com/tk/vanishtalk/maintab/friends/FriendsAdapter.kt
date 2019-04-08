package com.tk.vanishtalk.maintab.friends

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tk.vanishtalk.R
import com.tk.vanishtalk.model.data.local.LocalUser

class FriendsAdapter(val context: Context): RecyclerView.Adapter<FriendsAdapter.ViewHolder>(),
    FriendsAdapterContract.View, FriendsAdapterContract.Model {

    override var onFriendsClickListenerCallback: ((LocalUser) -> Unit)? = null
    var friendsList = ArrayList<LocalUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return FriendsAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.recyclerview_friends,
                parent,
                false),
            context, onFriendsClickListenerCallback)
    }

    override fun getItemCount() = friendsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = friendsList[position]
        holder.bind(user)
    }

    override fun notifyDataChange() {
        notifyDataSetChanged()
    }

    override fun addFriends(user: LocalUser) {
        friendsList.add(user)
    }

    override fun initAllFriends(friendsList: ArrayList<LocalUser>) {
        this.friendsList = friendsList
    }

    class ViewHolder(val view: View, val context: Context,
                     val onFriendsClickListenerCallback: ((LocalUser) -> Unit)?
    ): RecyclerView.ViewHolder(view) {
        private val userImage = view.findViewById<ImageView>(R.id.friends_recycler_image)
        private val userName = view.findViewById<TextView>(R.id.friends_recycler_name)

        fun bind(user: LocalUser) {
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
                onFriendsClickListenerCallback?.invoke(user)
            }
        }
    }
}