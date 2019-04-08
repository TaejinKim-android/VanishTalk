package com.tk.vanishtalk.initiate

import com.google.firebase.auth.FirebaseAuth
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.repository.ChatRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InitPresenter: InitContract.Presenter {

    lateinit var view: InitContract.View
    lateinit var localSQLiteAdapter: SQLiteAdapter
    private var auth: FirebaseAuth? = FirebaseAuth.getInstance()

    override fun checkLoggedIn() {
        when (auth?.currentUser) { //auth.currentUser가 null이면 로그인한 사용자가 없는 것, 자동로그인 불가능 상태
            null -> view.startLoginActivity()
            else -> {
                view.startMainTabActivity()
            }
        }
    }
}