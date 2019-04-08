package com.tk.vanishtalk.initiate

interface InitContract {

    interface View {
        fun startLoginActivity()
        fun startMainTabActivity()
    }

    interface Presenter {
        fun checkLoggedIn()
    }
}