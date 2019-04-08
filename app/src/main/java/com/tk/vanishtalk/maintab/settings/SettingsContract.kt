package com.tk.vanishtalk.maintab.settings

import com.tk.vanishtalk.model.data.local.LocalUser
import io.reactivex.subjects.Subject

interface SettingsContract {

    interface View {
        fun setViewUserInfoModifyMode()
        fun setViewUserInfoViewMode()
        fun setValuesUserInfoChangeMode(user: LocalUser)
        fun setValuesUserInfoViewMode(user: LocalUser)
        fun ableModify(canModify: Boolean)
        fun setThumbnailFromUri(uri: String?)
        fun showFullImgFromUri(uri: String?)
        fun showLogout()
        fun showWithdraw()
        fun showWithdrawConfirm()
        fun showWithdrawCancel()
        fun startLoginActivity()
        fun showProgress()
        fun hideProgress()
        fun showAlert()
    }

    interface Presenter {
        fun setUserInfoViewMode()
        fun setUserInfoChangeMode()
        fun startTextWatch(textSubject: Subject<String>)
        fun loadProfileImgUri(): String?
        fun handleImgFromGallery(imgFromGalleryUri: String)
        fun updateUserInfo(userName: String, tmpImgUri: String?)
        fun logout()
        fun onWithdrawClick()
        fun withdraw()
        fun close()
    }
}