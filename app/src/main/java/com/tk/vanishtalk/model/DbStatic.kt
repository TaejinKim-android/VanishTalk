package com.tk.vanishtalk.model

class DbStatic {

    companion object {
        const val SHARED_CURRENT_USER_EMAIL_KEY = "email"
        const val SHARED_CURRENT_USER_NAME_KEY = "name"
        const val SHARED_CURRENT_USER_IMG_URI_KEY = "img_uri"
        const val SHARED_CURRENT_USER_PHONE_NUM_KEY = "phone"

        const val DB_NAME = "vanish_talk.db"
        const val DB_VER = 1

        const val TABLE_USER = "user"
        const val TABLE_ROOM_TABLE_LIST = "room_table_list"

        const val COL_NUM = "num" //모든 테이블의 PK, auto_increment
        const val COL_EMAIL = "email"
        const val COL_NAME = "name"
        const val COL_PROFILE_IMG_URI = "img_uri"
        const val COL_FRIEND_EMAIL = "friend_email"
        const val COL_PHONE_NUM = "phone_num" //nullable

        const val COL_ROOM_TABLE_ID = "room_table_id"
        const val COL_ROOM_TABLE_NAME = "room_table_name"
        const val COL_USER_LIST_JSON = "user_list_json" //json array
        const val COL_LAST_MSG = "last_msg" //대화창 리스트에 미리보기로 띄울 가장 최근 메시지
        const val COL_LAST_MSG_ID = "last_msg_id" //mongoDB chat document id used to determine starting point of chat sync.
        const val COL_LAST_MSG_TIME = "last_msg_time" //LAST_MSG가 온 시각

        const val COL_MSG_ID = "msg_id" //set by server
        const val COL_MSG_TYPE = "msg_type" //0 == text, 1 == image
        const val COL_MSG_CONTENT = "msg_content"
        const val COL_MSG_SEND_TIME = "msg_send_time"
        const val COL_MSG_READER_COUNT = "msg_reader_count"

        const val CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + "(" +
                "$COL_NUM INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                "$COL_EMAIL TEXT NOT NULL, " +
                "$COL_NAME TEXT NOT NULL, " +
                "$COL_PROFILE_IMG_URI TEXT, " +
                "$COL_FRIEND_EMAIL TEXT NOT NULL, "+
                "$COL_PHONE_NUM TEXT" + ")"

        const val CREATE_ROOM_LIST_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ROOM_TABLE_LIST + "(" +
                "$COL_NUM INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                "$COL_ROOM_TABLE_ID TEXT NOT NULL, " +
                "$COL_ROOM_TABLE_NAME TEXT NOT NULL, " +
                "$COL_USER_LIST_JSON TEXT NOT NULL, " +
                "$COL_LAST_MSG TEXT, " +
                "$COL_LAST_MSG_ID INTEGER, " +
                "$COL_LAST_MSG_TIME INTEGER" + ")"

        //방별 채팅 내용 기록할 table은 roomId를 그대로 테이블명으로 쓸 것이므로 값이 유동적이기에 테이블명이 들어갈 자리를 분리하여 2개로 작성
        const val CREATE_CHAT_TABLE0 = "CREATE TABLE IF NOT EXISTS "
        const val CREATE_CHAT_TABLE1 = "(" +
                "$COL_NUM INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                "$COL_MSG_ID INTEGER NOT NULL, " +
                "$COL_EMAIL TEXT NOT NULL, " +
                "$COL_MSG_TYPE INTEGER NOT NULL, " + //0 == text, 1 == image
                "$COL_MSG_CONTENT TEXT NOT NULL, " +
                "$COL_MSG_SEND_TIME INTEGER NOT NULL, " +
                "$COL_MSG_READER_COUNT INTEGER NOT NULL" + ")" //SQLite는 boolean형을 따로 지원하지 않음, 1 == read, 0 == not yet

        const val DROP_TABLE = "DROP TABLE IF EXISTS "

        const val VALUE_MSG_TYPE_TEXT = 0
        const val VALUE_MSG_TYPE_IMG = 1
    }
}