package com.tk.vanishtalk.maintab.friends.add

import com.google.firebase.auth.FirebaseAuth
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.data.local.LocalUserForNetwork
import com.tk.vanishtalk.model.data.remote.RemoteUser
import com.tk.vanishtalk.model.repository.UserRepository
import com.tk.vanishtalk.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.Subject
import retrofit2.Response
import java.util.concurrent.TimeUnit

class AddFriendsPresenter: AddFriendsContract.Presenter {

    var view: AddFriendsContract.View? = null
    lateinit var localSQLModel: SQLiteAdapter
    lateinit var localSpModel: SharedPreferencesAdapter

    var compositeDisposable: CompositeDisposable? = CompositeDisposable()

    override fun startTextWatch(textSubject: Subject<String>) {
        val myEmail = localSpModel.getEmail()
        compositeDisposable?.add(textSubject.debounce(200, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                if (it == myEmail) {
                    view?.showInputValueStatus(true)
                } else {
                    view?.showInputValueStatus(false)
                }
            }
        )
    }

    override fun addFriend(friendEmail: String) {
        view?.showProgress()
        val user = FirebaseAuth.getInstance().currentUser!!
        compositeDisposable?.add(
            UserRepository.updateUserFriendsInfo(user.uid, LocalUserForNetwork(
                friendEmail,
                null,
                null,
                null
            ), object: UserRepository.RetrofitGetCallback {

                override fun onSuccess(userResult: Response<RemoteUser>) {
                    userResult.body()?.let {
                        localSpModel.getEmail()?.let { myEmail ->
                            val friend = Utils.remoteUserToLocalUser(it, myEmail)
                            localSQLModel.insertUser(friend)
                            view?.sendResultToFriendsFragment(friend)
                            view?.hideProgress()
                        }
                    }
                }

                override fun onError() {
                    //user not found or server/DB failure
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