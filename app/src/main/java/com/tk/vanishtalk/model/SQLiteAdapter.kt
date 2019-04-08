package com.tk.vanishtalk.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tk.vanishtalk.model.data.local.LocalChat
import com.tk.vanishtalk.model.data.local.LocalChatRoom
import com.tk.vanishtalk.model.data.local.LocalUser
import com.tk.vanishtalk.model.data.remote.RemoteUser
import com.tk.vanishtalk.util.Utils

class SQLiteAdapter private constructor(context: Context): SQLiteOpenHelper(
    context, DbStatic.DB_NAME, null, DbStatic.DB_VER
) {

    companion object {
        @Volatile private var INSTANCE: SQLiteAdapter? = null

        fun getInstance(context: Context): SQLiteAdapter {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = SQLiteAdapter(context)
                }
            }

            return INSTANCE!!
        }
    } //Singleton

    private var db = writableDatabase

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DbStatic.CREATE_USER_TABLE)
        db.execSQL(DbStatic.CREATE_ROOM_LIST_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    //$table is a key(mongoDB _id). It starts with a numeric, must be enclosed by quotes.
    fun createChatRoomTable(tableName: String) {
        db.execSQL(DbStatic.CREATE_CHAT_TABLE0 + "\'$tableName\'" + DbStatic.CREATE_CHAT_TABLE1)
    }

    fun dropChatTable(tableName: String) = db.execSQL(DbStatic.DROP_TABLE + "\'$tableName\'")

    fun insertUser(user: LocalUser) {
        if (isNewInsertUser(user.email)) {
            val values = ContentValues()
            values.put(DbStatic.COL_EMAIL, user.email)
            values.put(DbStatic.COL_NAME, user.name)
            values.put(DbStatic.COL_PROFILE_IMG_URI, user.imgUri)
            values.put(DbStatic.COL_FRIEND_EMAIL, user.friendEmail)
            values.put(DbStatic.COL_PHONE_NUM, user.phoneNum)
            db.insert(DbStatic.TABLE_USER, null, values)
        }
    }

    fun insertChatRoomInfo(chatRoom: LocalChatRoom) {
        val values = ContentValues()
        values.put(DbStatic.COL_ROOM_TABLE_ID, chatRoom.id)
        values.put(DbStatic.COL_ROOM_TABLE_NAME, chatRoom.name)
        values.put(DbStatic.COL_USER_LIST_JSON, chatRoom.userListJson)
        values.put(DbStatic.COL_LAST_MSG, chatRoom.lastMsg)
        values.put(DbStatic.COL_LAST_MSG_TIME, chatRoom.lastMsgTime)
        db.insert(DbStatic.TABLE_ROOM_TABLE_LIST, null, values)
    }

    fun insertChat(tableName: String, chat: LocalChat) {
        val values = ContentValues()
        values.put(DbStatic.COL_MSG_ID, chat.id)
        values.put(DbStatic.COL_EMAIL, chat.email)
        values.put(DbStatic.COL_MSG_TYPE, chat.type)
        values.put(DbStatic.COL_MSG_CONTENT, chat.content)
        values.put(DbStatic.COL_MSG_SEND_TIME, chat.sendTime)
        values.put(DbStatic.COL_MSG_READER_COUNT, chat.readCount)
        db.insert("\'$tableName\'", null, values)
    }

    fun selectAllChat(tableName: String): ArrayList<LocalChat> {
        val chatList = ArrayList<LocalChat>()
        val cursor = db.query("\'$tableName\'",
            null,
            null,
            null,
            null,
            null,
            DbStatic.COL_MSG_ID + " ASC")
        while (cursor.moveToNext()) {
            val chatId = cursor.getLong(cursor.getColumnIndex(DbStatic.COL_MSG_ID))
            val senderEmail = cursor.getString(cursor.getColumnIndex(DbStatic.COL_EMAIL))
            val msgType = cursor.getInt(cursor.getColumnIndex(DbStatic.COL_MSG_TYPE))
            val msgContent = cursor.getString(cursor.getColumnIndex(DbStatic.COL_MSG_CONTENT))
            val msgSendTime = cursor.getLong(cursor.getColumnIndex(DbStatic.COL_MSG_SEND_TIME))
            val msgReaderCount = cursor.getInt(cursor.getColumnIndex(DbStatic.COL_MSG_READER_COUNT))

            val chat = LocalChat(chatId, senderEmail, msgType, msgContent, msgSendTime, msgReaderCount)
            chatList.add(chat)
        }
        cursor.close()
        return chatList
    }

    fun selectAllChatRoom(email: String): ArrayList<LocalChatRoom> {
        val chatRoomList = ArrayList<LocalChatRoom>()
        val cursor = db.query(
            DbStatic.TABLE_ROOM_TABLE_LIST,
            null,
            "${DbStatic.COL_USER_LIST_JSON} LIKE ?",
            arrayOf("%$email%"),
            null,
            null,
            DbStatic.COL_LAST_MSG_TIME + " DESC")
        while (cursor.moveToNext()) {
            val roomId = cursor.getString(cursor.getColumnIndex(DbStatic.COL_ROOM_TABLE_ID))
            val roomName = cursor.getString(cursor.getColumnIndex(DbStatic.COL_ROOM_TABLE_NAME))
            val userListJson = cursor.getString(cursor.getColumnIndex(DbStatic.COL_USER_LIST_JSON))
            val lastMsg = cursor.getString(cursor.getColumnIndex(DbStatic.COL_LAST_MSG))
            val lastMsgId = cursor.getLong(cursor.getColumnIndex(DbStatic.COL_LAST_MSG_ID))
            val lastMsgTime = cursor.getLong(cursor.getColumnIndex(DbStatic.COL_LAST_MSG_TIME))

            val chatRoom = LocalChatRoom(roomId, roomName, userListJson, lastMsg, lastMsgId, lastMsgTime)
            chatRoomList.add(chatRoom)
        }
        cursor.close()
        return chatRoomList
    }

    fun selectAllFriends(email: String): ArrayList<LocalUser> {
        val friendsList = ArrayList<LocalUser>()
        val cursor = db.query(
            DbStatic.TABLE_USER,
            null,
            "${DbStatic.COL_FRIEND_EMAIL} = ? AND ${DbStatic.COL_EMAIL} <> ${DbStatic.COL_FRIEND_EMAIL}",
            arrayOf(email),
            null,
            null,
            DbStatic.COL_NAME + " ASC")
        while (cursor.moveToNext()) {
            val friendId = cursor.getString(cursor.getColumnIndex(DbStatic.COL_EMAIL))
            val friendName = cursor.getString(cursor.getColumnIndex(DbStatic.COL_NAME))
            val userImgUri = cursor.getString(cursor.getColumnIndex(DbStatic.COL_PROFILE_IMG_URI))
            val friendOfUserId = cursor.getString(cursor.getColumnIndex(DbStatic.COL_FRIEND_EMAIL))
            val friendPhoneNum = cursor.getString(cursor.getColumnIndex(DbStatic.COL_PHONE_NUM))

            val friend = LocalUser(friendId, friendName, userImgUri, friendOfUserId, friendPhoneNum)
            friendsList.add(friend)
        }
        cursor.close()
        return friendsList
    }

    fun selectUserByEmail(userEmail: String): LocalUser {
        val cursor = db.query(
            DbStatic.TABLE_USER,
            null,
            "${DbStatic.COL_EMAIL} = ?",
            arrayOf(userEmail),
            null,
            null,
            DbStatic.COL_NAME + " ASC")

        val email = cursor.getString(cursor.getColumnIndex(DbStatic.COL_EMAIL))
        val name = cursor.getString(cursor.getColumnIndex(DbStatic.COL_NAME))
        val imgUri = cursor.getString(cursor.getColumnIndex(DbStatic.COL_PROFILE_IMG_URI))
        val friendEmail = cursor.getString(cursor.getColumnIndex(DbStatic.COL_FRIEND_EMAIL))
        val phoneNum = cursor.getString(cursor.getColumnIndex(DbStatic.COL_PHONE_NUM))

        cursor?.close()
        return LocalUser(email, name, imgUri, friendEmail, phoneNum)
    }

    fun selectChatRoomByRoomMemberEmailJson(userEmailJson: String): LocalChatRoom {
        val cursor = db.query(
            DbStatic.TABLE_ROOM_TABLE_LIST,
            null,
            "${DbStatic.COL_USER_LIST_JSON} = ?",
            arrayOf(userEmailJson),
            null,
            null,
            null)

        cursor.moveToFirst()

        val roomId = cursor.getString(cursor.getColumnIndex(DbStatic.COL_ROOM_TABLE_ID))
        val roomName = cursor.getString(cursor.getColumnIndex(DbStatic.COL_ROOM_TABLE_NAME))
        val userListJson = cursor.getString(cursor.getColumnIndex(DbStatic.COL_USER_LIST_JSON))
        val lastMsg = cursor.getString(cursor.getColumnIndex(DbStatic.COL_LAST_MSG))
        val lastMsgId = cursor.getLong(cursor.getColumnIndex(DbStatic.COL_LAST_MSG_ID))
        val lastMsgTime = cursor.getLong(cursor.getColumnIndex(DbStatic.COL_LAST_MSG_TIME))

        cursor.close()
        return LocalChatRoom(roomId, roomName, userListJson, lastMsg, lastMsgId, lastMsgTime)
    }

    fun updateUserInfo(user: LocalUser) {
        val values = ContentValues()
        values.put(DbStatic.COL_NAME, user.name)
        values.put(DbStatic.COL_PROFILE_IMG_URI, user.imgUri)

        db.update(
            DbStatic.TABLE_USER,
            values,
            "${DbStatic.COL_FRIEND_EMAIL} = ? AND ${DbStatic.COL_EMAIL} = ?",
            arrayOf(user.friendEmail, user.email)
        )
    }

    fun updateChatRoomStatus(roomId: String, chat: LocalChat) {
        val values = ContentValues()
        values.put(DbStatic.COL_LAST_MSG, chat.content)
        values.put(DbStatic.COL_LAST_MSG_ID, chat.id)
        values.put(DbStatic.COL_LAST_MSG_TIME, chat.sendTime)

        db.update(
            DbStatic.TABLE_ROOM_TABLE_LIST,
            values,
            "${DbStatic.COL_ROOM_TABLE_ID} = ?",
            arrayOf(roomId)
        )
    }

    fun withdrawUser(email: String): Boolean {
        if (deleteUserByEmail(email)) {
            val friendsList = selectAllFriends(email)
            for (friend in friendsList) {
                deleteUserByEmail(friend.email)
            }

            //delete chatroom, chat required
            val roomList = selectAllChatRoom(email)
            for (room in roomList) {
                dropChatTable(room.id!!)
                deleteChatRoomInfo(room.id!!)
            }
            return true
        }

        return false
    }

    fun syncAllFriends(email: String, friendsList: ArrayList<RemoteUser>) {
        for (friend in friendsList) {
            val localFriend = Utils.remoteUserToLocalUser(friend, email)
            when(isNewInsertFriend(email, localFriend)) {
                true -> insertUser(localFriend)
                false -> updateUserInfo(localFriend)
            }
        }
    }

    fun deleteUserByEmail(email: String): Boolean
            = db.delete(DbStatic.TABLE_USER, "${DbStatic.COL_EMAIL} = ?", arrayOf(email)) > 0

    fun deleteChatRoomInfo(chatRoomId: String): Boolean
            = db.delete(DbStatic.TABLE_ROOM_TABLE_LIST, "${DbStatic.COL_ROOM_TABLE_ID} = ?", arrayOf(chatRoomId)) > 0

    fun deleteChatByUser(tableName: String, column: String, colNum: Int): Boolean
            = db.delete(tableName, "${DbStatic.COL_NUM} = ?", arrayOf(colNum.toString())) > 0

    /*fun deleteChatByDay(email: String): Boolean {
        val roomList = selectAllChatRoom(email)
        for (room in roomList) {
            dropChatTable(room.id!!)
            deleteChatRoomInfo(room.id!!)
        }
    }*/

    fun isNewInsertUser(email: String): Boolean {
        val cursor = db.query(
            DbStatic.TABLE_USER,
            arrayOf(DbStatic.COL_EMAIL),
            "${DbStatic.COL_FRIEND_EMAIL} = ? AND ${DbStatic.COL_EMAIL} = ?",
            arrayOf(email, email),
            null,
            null,
            null)
        return if (cursor.count > 0) {
            cursor.close()
            false
        } else {
            cursor.close()
            true
        }
    }

    fun isNewInsertFriend(userEmail: String, friend: LocalUser): Boolean {
        val cursor = db.query(
            DbStatic.TABLE_USER,
            arrayOf(DbStatic.COL_EMAIL),
            "${DbStatic.COL_FRIEND_EMAIL} = ? AND ${DbStatic.COL_EMAIL} = ?",
            arrayOf(userEmail, friend.email),
            null,
            null,
            null)
        return if (cursor.count > 0) {
            cursor.close()
            false
        } else {
            cursor.close()
            true
        }
    }

    fun isChatRoomExistsByTableId(tableName: String): Boolean {
        val cursor = db.query(
            DbStatic.TABLE_ROOM_TABLE_LIST,
            arrayOf(DbStatic.COL_ROOM_TABLE_ID),
            "${DbStatic.COL_ROOM_TABLE_ID} = ?",
            arrayOf("\'$tableName\'"),
            null,
            null,
            null)
        return if (cursor.count > 0) {
            cursor.close()
            false
        } else {
            cursor.close()
            true
        }
    }

    fun isChatRoomExistsByUserEmailJson(userEmailJson: String): Boolean {
        val cursor = db.query(
            DbStatic.TABLE_ROOM_TABLE_LIST,
            arrayOf(DbStatic.COL_USER_LIST_JSON),
            "${DbStatic.COL_USER_LIST_JSON} = ?",
            arrayOf(userEmailJson),
            null,
            null,
            null)
        return if (cursor.count > 0) {
            cursor.close()
            true
        } else {
            cursor.close()
            false
        }
    }
}