package com.tk.vanishtalk.maintab.settings

import com.google.firebase.auth.FirebaseAuth
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.data.local.LocalUserForNetwork
import com.tk.vanishtalk.model.data.local.LocalUser
import com.tk.vanishtalk.model.repository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.Subject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.Serializable
import java.util.concurrent.TimeUnit

class SettingsPresenter: SettingsContract.Presenter, Serializable {

    var view: SettingsContract.View? = null
    lateinit var localSQLModel: SQLiteAdapter
    lateinit var localSPModel: SharedPreferencesAdapter
    var imgFolderUri: String? = null
    var compositeDisposable = CompositeDisposable()

    override fun setUserInfoViewMode() {
        val user: LocalUser = localSPModel.getCurrentUserData()
        view?.setValuesUserInfoViewMode(user)
        view?.setViewUserInfoViewMode()
    }

    override fun setUserInfoChangeMode() {
        val user = localSPModel.getCurrentUserData()
        view?.setValuesUserInfoChangeMode(user)
        view?.setViewUserInfoModifyMode()
    }

    override fun startTextWatch(textSubject: Subject<String>) {
        compositeDisposable.add(textSubject.debounce(200, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                if (it.isNullOrEmpty()) {
                    view?.ableModify(false)
                } else {
                    view?.ableModify(true)
                }
            }
        )
    }

    override fun loadProfileImgUri() = localSPModel.getCurrentUserData().imgUri

    override fun handleImgFromGallery(imgFromGalleryUri: String) {
        view?.setThumbnailFromUri(imgFromGalleryUri)
    }

    override fun updateUserInfo(userName: String, tmpImgUri: String?) {
        view?.showProgress()
        FirebaseAuth.getInstance().currentUser?.let {
            val userData = LocalUserForNetwork(null, userName, null, null)
            val currentUser = localSPModel.getCurrentUserData()
            val user = LocalUser(
                currentUser.email,
                userName,
                tmpImgUri,
                currentUser.email,
                null
            )

            compositeDisposable.add(UserRepository
                .updateUserInfo(it.uid, userData, object : UserRepository.RetrofitSetCallback {

                    override fun onSuccess() {
                        if (tmpImgUri != null) {
                            val file = File(tmpImgUri)
                            val reqBody = RequestBody.create(MediaType.parse("image/*"), file)
                            val part = MultipartBody.Part.createFormData("image", file.name, reqBody)
                            compositeDisposable.add(UserRepository
                                .setUserImg(it.uid, part, object: UserRepository.RetrofitSetCallback {

                                    override fun onSuccess() {
                                        user.imgUri = tmpImgUri
                                        localSQLModel.updateUserInfo(user)
                                        localSPModel.setUpdatedUserData(user)
                                        setUserInfoViewMode()
                                        view?.hideProgress()
                                    }

                                    override fun onError() {
                                        view?.hideProgress()
                                        view?.showAlert()
                                    }
                                })
                            )
                        } else {
                            localSQLModel.updateUserInfo(user)
                            localSPModel.setUpdatedUserData(user)
                            setUserInfoViewMode()
                            view?.hideProgress()
                        }
                    }

                    override fun onError() {
                        view?.hideProgress()
                        view?.showAlert()
                    }
                })
            )
        }
    }

    override fun logout() {
        FirebaseAuth.getInstance().signOut()
        localSPModel.clearCurrentUserData()
        view?.startLoginActivity()
    }

    override fun onWithdrawClick() {
        view?.showWithdraw()
    }

    override fun withdraw() {
        view?.showProgress()
        val user = FirebaseAuth.getInstance().currentUser!!
        compositeDisposable.add(UserRepository
                .deleteUserInfo(user.uid, object: UserRepository.RetrofitSetCallback {
                    override fun onSuccess() {
                        //server successfully removed user account from back-end DB & firebase auth server

                        //if removing information about user failed
                        if (!localSQLModel.withdrawUser(user.email!!)) {
                            view?.hideProgress()
                            view?.showAlert()
                        }

                        localSPModel.clearCurrentUserData()
                        view?.hideProgress()
                        view?.startLoginActivity()
                    }

                    override fun onError() {
                        //user not found or server error(firebaseAuth admin may not work well, token problem, etc)
                        view?.hideProgress()
                        view?.showAlert()
                    }
                })
        )
    }

    override fun close() {
        view = null
        compositeDisposable.clear()
    }
}