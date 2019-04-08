package com.tk.vanishtalk.chat

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tk.vanishtalk.R
import com.tk.vanishtalk.model.data.local.LocalChat
import com.tk.vanishtalk.util.Utils

class ChatAdapter(val context: Context): RecyclerView.Adapter<ChatAdapter.ViewHolder>(),
    ChatAdapterContract.View, ChatAdapterContract.Model {

    override lateinit var myEmail: String
    private var chatList = ArrayList<LocalChat>()

    open class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        open fun bind(chat: LocalChat) {}
    }

    override fun getItemViewType(position: Int): Int {
        return when(chatList[position].email) {
            myEmail -> 0
            else -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when(viewType) {
            0 -> MyViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.recyclerview_chat_me,
                    parent,
                    false))
            else -> OtherViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.recyclerview_chat_other,
                    parent,
                    false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bind(chat)
    }

    override fun getItemCount() = chatList.size

    override fun notifyDataChange() {
        notifyDataSetChanged()
    }

    override fun addChat(chat: LocalChat) {
        chatList.add(chat)
    }

    override fun initAllChat(chatList: ArrayList<LocalChat>) {
        this.chatList = chatList
    }

    override fun getArrayListSize() = itemCount

    class MyViewHolder(view: View): ChatAdapter.ViewHolder(view) {
        private val chatMsg = view.findViewById<TextView>(R.id.chat_recycler_me_msg)
        private val chatTime = view.findViewById<TextView>(R.id.chat_recycler_me_time)
        private val chatReadCount = view.findViewById<TextView>(R.id.chat_recycler_me_readcount)

        override fun bind(chat: LocalChat) {
            chatMsg.text = chat.content
            chatTime.text = Utils.timeStampToWTC(chat.sendTime)
            chatReadCount.text = chat.readCount.toString()
        }
    }

    class OtherViewHolder(view: View): ChatAdapter.ViewHolder(view) {
        private val userName = view.findViewById<TextView>(R.id.chat_recycler_other_nametext)
        private val chatMsg = view.findViewById<TextView>(R.id.chat_recycler_other_msg)
        private val chatTime = view.findViewById<TextView>(R.id.chat_recycler_other_time)
        private val chatReadCount = view.findViewById<TextView>(R.id.chat_recycler_other_readcount)

        override fun bind(chat: LocalChat) {
            userName.text = chat.email
            chatMsg.text = chat.content
            chatTime.text = Utils.timeStampToWTC(chat.sendTime)
            chatReadCount.text = chat.readCount.toString()
        }
    }
}