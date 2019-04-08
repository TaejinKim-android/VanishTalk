package com.tk.vanishtalk.model.repository

import com.tk.vanishtalk.model.data.local.LocalUserForNetwork
import com.tk.vanishtalk.model.data.remote.RemoteUser
import com.tk.vanishtalk.network.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import retrofit2.Response

object UserRepository {

    private val retrofitClient = RetrofitClient.client

    fun getUserInfo(uid: String, callback: RetrofitGetCallback): Disposable
            = retrofitClient.getUserInfo(uid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when(it.code()) {
                500 -> callback.onError()
                else -> callback.onSuccess(it)
            }
        }

    fun getFriendsList(uid: String, callback: RetrofitGetListCallback): Disposable
            = retrofitClient.getFriendsList(uid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when(it.code()) {
                200 -> callback.onSuccess(it)
                else -> callback.onError()
            }
        }

    fun setUserInfo(uid: String, callback: RetrofitSetCallback): Disposable
            = retrofitClient.setUserInfo(uid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when(it.code()) {
                200 -> callback.onSuccess()
                else -> callback.onError()
            }
        }

    fun updateUserInfo(uid: String, user: LocalUserForNetwork, callback: RetrofitSetCallback): Disposable
            = retrofitClient.updateUserInfo(uid, user)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when(it.code()) {
                200 -> callback.onSuccess()
                else -> callback.onError()
            }
        }

    fun updateUserFriendsInfo(uid: String, friend: LocalUserForNetwork, callback: RetrofitGetCallback): Disposable
            = retrofitClient.updateUserFriendsInfo(uid, friend)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when(it.code()) {
                200 -> callback.onSuccess(it)
                else -> callback.onError()
            }
        }

    fun deleteUserInfo(uid: String, callback: RetrofitSetCallback): Disposable
            = retrofitClient.deleteUserInfo(uid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when(it.code()) {
                200 -> callback.onSuccess()
                else -> callback.onError()
            }
        }

    fun setUserImg(uid: String, file: MultipartBody.Part, callback: RetrofitSetCallback): Disposable
            = retrofitClient.setUserImg(uid, file)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when(it.code()) {
                200 -> callback.onSuccess()
                else -> callback.onError()
            }
        }

    interface RetrofitGetCallback {
        fun onSuccess(userResult: Response<RemoteUser>)
        fun onError()
    }

    interface RetrofitGetListCallback {
        fun onSuccess(userResult: Response<ArrayList<RemoteUser>>)
        fun onError()
    }

    interface RetrofitSetCallback {
        fun onSuccess()
        fun onError()
    }
}