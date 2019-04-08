package com.tk.vanishtalk.maintab

interface MainTabContract {

    interface View {

    }

    interface Presenter {
        fun connectSocket()
        fun close()
    }
}