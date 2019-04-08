package com.tk.vanishtalk.login


import com.google.firebase.auth.FirebaseAuth
import com.tk.vanishtalk.model.data.local.LocalUser
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.data.local.LocalUserForNetwork
import com.tk.vanishtalk.model.data.remote.RemoteUser
import com.tk.vanishtalk.model.repository.ChatRepository
import com.tk.vanishtalk.model.repository.UserRepository
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class LoginPresenter : LoginContract.Presenter {

    var view: LoginContract.View? = null
    lateinit var localSPModel: SharedPreferencesAdapter
    lateinit var localSQLModel: SQLiteAdapter

    var compositeDisposable: CompositeDisposable? = CompositeDisposable()

    override fun setMyInfo(phoneNum: String?) {
        view?.showProgress()
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val email = firebaseUser?.email
        if (email != null) {
            val user = LocalUser(
                email,
                firebaseUser.displayName!!,
                null,
                email,
                phoneNum
            )

            identifyNewUser(firebaseUser.uid, user)
        }
    }

    private fun identifyNewUser(uid: String, user: LocalUser) {
        compositeDisposable?.add(UserRepository
            .getUserInfo(uid, object: UserRepository.RetrofitGetCallback {
                override fun onSuccess(userResult: Response<RemoteUser>) {
                    when(userResult.code()) {
                        //user already signed
                        200 -> {
                            localSQLModel.insertUser(user) //for user who already signed, but changed device
                            localSPModel.setCurrentUserData(user)

                            view?.hideProgress()
                            view?.startMainTabActivity()
                        }

                        //new user signing in
                        404 -> saveUserToServer(uid, user)
                    }

//                    val socket = ChatRepository
//                    socket.connect()
//
//                    //to enroll socket id to server
//                    val userForNetwork = LocalUserForNetwork(user.email, null, null, null)
//                    socket.enrollSocket(userForNetwork)
//                    val roomList = localSQLModel.selectAllChatRoom(user.email)
//                    socket.connectRooms(roomList)
                }

                override fun onError() {
                    //server error, code 500
                    view?.hideProgress()
                    view?.showAlert()
                }
            })
        )
    }

    private fun saveUserToServer(uid: String, user: LocalUser) {
        compositeDisposable?.add(UserRepository
            .setUserInfo(uid, object : UserRepository.RetrofitSetCallback {
                override fun onSuccess() {
                    localSQLModel.insertUser(user)
                    localSPModel.setCurrentUserData(user)
                    view?.hideProgress()
                    view?.startMainTabActivity()
                }

                override fun onError() {
                    view?.hideProgress()
                    view?.showAlert()
                }
            })
        )
    }

    override fun close() {
        view = null
        compositeDisposable?.clear()
    }
}