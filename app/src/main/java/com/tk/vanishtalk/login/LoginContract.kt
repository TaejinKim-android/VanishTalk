package com.tk.vanishtalk.login

interface LoginContract {

    interface View {
        fun showProgress()
        fun hideProgress()
        fun showAlert()
        fun startMainTabActivity()
    }

    interface Presenter {
        fun setMyInfo(phoneNum: String?)
        fun close()
    }
}