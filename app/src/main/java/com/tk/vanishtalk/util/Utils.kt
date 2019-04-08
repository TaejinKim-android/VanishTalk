package com.tk.vanishtalk.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.model.data.local.LocalUser
import com.tk.vanishtalk.model.data.remote.RemoteChatRoom
import com.tk.vanishtalk.model.data.remote.RemoteUser
import com.tk.vanishtalk.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun <T> arrayListToJson(arrayList: ArrayList<T>): String = Gson().toJson(arrayList)

    fun makeDefaultRoomName(userId: ArrayList<String>): String {
        var roomName = ""
        for (item in userId) roomName += "$item,"
        roomName = roomName.substring(0, roomName.length - 1)

        return roomName
    }

    fun remoteChatRoomToLocalChatRoom(remoteChatRoom: RemoteChatRoom): LocalChatRoom {
        val listType = object : TypeToken<ArrayList<String>>() {}.type
        return LocalChatRoom(
            remoteChatRoom.id,
            makeDefaultRoomName(Gson().fromJson<ArrayList<String>>(remoteChatRoom.userListJson, listType)),
            remoteChatRoom.userListJson,
            null,
            null,
            null
        )
    }

    fun remoteUserToLocalUser(remoteUser: RemoteUser, myEmail: String): LocalUser
            = LocalUser(
        remoteUser.email,
        remoteUser.name,
        "${RetrofitClient.BASE_URL}/users/${remoteUser.imgUri}",
        myEmail,
        remoteUser.phoneNum
        )

    fun timeStampToWTC(unixTime: Long): String
            = SimpleDateFormat("MM월 dd일 HH:mm", Locale.getDefault()).format(Date(unixTime * 1000))

}