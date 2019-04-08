package com.tk.vanishtalk.maintab.chatroom

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.tk.vanishtalk.R
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.util.Utils
import java.io.Serializable

class ChatRoomAdapter(val context: Context): RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>(),
    ChatRoomAdapterContract.View, ChatRoomAdapterContract.Model, Serializable{

    override lateinit var onRoomClickListenerCallback: (LocalChatRoom) -> Unit
    var chatRoomList = ArrayList<LocalChatRoom>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ChatRoomAdapter.ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.recyclerview_chat_room,
                parent,
                false), onRoomClickListenerCallback)
    }

    override fun getItemCount() = chatRoomList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatRoom = chatRoomList[position]
        holder.bind(chatRoom)
    }

    override fun notifyDataChange() {
        notifyDataSetChanged()
    }

    override fun initAllChatRoom(chatRoomList: ArrayList<LocalChatRoom>) {
        this.chatRoomList = chatRoomList
    }

    class ViewHolder(val view: View,
                     val onRoomClickListenerCallback: (LocalChatRoom) -> Unit
    ): RecyclerView.ViewHolder(view) {
        //private val chatRoomImage = view.findViewById<ImageView>(R.id.chat_room_recycler_image)
        private val chatRoomName = view.findViewById<TextView>(R.id.chat_room_recycler_name)
        private val chatRoomLastMsg = view.findViewById<TextView>(R.id.chat_room_recycler_msg)
        private val charRoomLastMsgTime = view.findViewById<TextView>(R.id.chat_room_recycler_time)

        fun bind(chatRoom: LocalChatRoom) {
            chatRoomName.text = chatRoom.name
            chatRoomLastMsg.text = chatRoom.lastMsg
            charRoomLastMsgTime.text = Utils.timeStampToWTC(chatRoom.lastMsgTime!!)
            view.setOnClickListener {
                onRoomClickListenerCallback.invoke(chatRoom)
            }
        }
    }
}