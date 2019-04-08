package com.tk.vanishtalk.maintab.settings


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.tk.vanishtalk.R
import com.tk.vanishtalk.login.LoginActivity
import com.tk.vanishtalk.maintab.MainTabActivity
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.util.ImgFullScreenActivity
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.data.local.LocalUser
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment(), SettingsContract.View, View.OnClickListener {

    private var presenter: SettingsPresenter? = null
    lateinit var fragmentContext: Context
    var isUserInfoViewMode = true
    var tmpImgUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContext = requireContext()

        if (presenter == null) {
            presenter = SettingsPresenter().apply {
                view = this@SettingsFragment
                localSQLModel = SQLiteAdapter.getInstance(fragmentContext)
                localSPModel = SharedPreferencesAdapter.getInstance(fragmentContext)
                imgFolderUri = fragmentContext.getDir(getString(R.string.basic_img_folder_name), Context.MODE_PRIVATE).toString()
            }
        }

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        settings_userimage.setOnClickListener(this)
        settings_modifybtn.setOnClickListener(this)
        settings_withdrawbtn.setOnClickListener(this)
        settings_logoutbtn.setOnClickListener(this)
        settings_modifycancelbtn.setOnClickListener(this)
        settings_modifyconfirmbtn.setOnClickListener(this)
        settings_usernameedittext.addTextChangedListener(object: TextWatcher {
            private val changedTextSubject = PublishSubject.create<String>().toSerialized()
            init {
                presenter?.startTextWatch(changedTextSubject)
            }
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    changedTextSubject.onNext("")
                } else {
                    changedTextSubject.onNext(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        presenter?.setUserInfoViewMode()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.settings_userimage -> {
                if (isUserInfoViewMode) {
                    //show full img when user clicks thumbnail
                    val uri = presenter?.loadProfileImgUri()
                    uri?.let {
                        val intent = Intent(fragmentContext, ImgFullScreenActivity::class.java)
                        intent.putExtra(getString(R.string.basic_img_key), it)
                        startActivity(intent)
                    }
                } else {
                    //load gallery to change user profile img
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = MediaStore.Images.Media.CONTENT_TYPE
                    startActivityForResult(intent, IMG_FROM_GALLERY)
                }
            }

            R.id.settings_modifybtn -> presenter?.setUserInfoChangeMode()
            R.id.settings_withdrawbtn -> presenter?.onWithdrawClick()
            R.id.settings_logoutbtn -> presenter?.logout()

            R.id.settings_modifycancelbtn -> {
                val activity = activity as MainTabActivity
                activity.hideKeyboard(this@SettingsFragment.view?.rootView)
                setThumbnailFromUri(presenter?.loadProfileImgUri())
                presenter?.setUserInfoViewMode()
            }

            R.id.settings_modifyconfirmbtn -> {
                val activity = activity as MainTabActivity
                activity.hideKeyboard(this@SettingsFragment.view?.rootView)
                presenter?.updateUserInfo(settings_usernameedittext.text.toString(), tmpImgUri)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when(requestCode) {
                IMG_FROM_GALLERY -> {
                    val photoUri = data?.data
                    val cursor = fragmentContext.contentResolver.query(photoUri,
                        arrayOf(MediaStore.Images.Media.DATA), null, null, null)
                    val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                    cursor.moveToFirst()
                    presenter?.handleImgFromGallery(cursor.getString(index))
                    cursor.close()
                }
            }
        }
    }

    override fun setViewUserInfoModifyMode() {
        isUserInfoViewMode = false
        settings_usernametext.visibility = GONE
        settings_modifybtn.visibility = GONE
        settings_withdrawbtn.visibility = GONE
        settings_logoutbtn.visibility = GONE
        settings_usernameedittext.visibility = VISIBLE
        settings_modifycancelbtn.visibility = VISIBLE
        settings_modifyconfirmbtn.visibility = VISIBLE
    }

    override fun setViewUserInfoViewMode() {
        isUserInfoViewMode = true
        settings_usernametext.visibility = VISIBLE
        settings_modifybtn.visibility = VISIBLE
        settings_withdrawbtn.visibility = VISIBLE
        settings_logoutbtn.visibility = VISIBLE
        settings_usernameedittext.visibility = GONE
        settings_modifycancelbtn.visibility = GONE
        settings_modifyconfirmbtn.visibility = GONE
    }

    override fun setValuesUserInfoChangeMode(user: LocalUser) {
        settings_usernameedittext.setText(user.name)
    }

    override fun setValuesUserInfoViewMode(user: LocalUser) {
        settings_useridtext.text = user.email
        settings_usernametext.text = user.name
        user.imgUri?.let {
            setThumbnailFromUri(it)
        }
    }

    override fun ableModify(canModify: Boolean) {
        when(canModify) {
            true -> {
                settings_modifyconfirmbtn.isEnabled = true
            }
            false -> {
                settings_usernameedittext.hint = getString(R.string.basic_null)
                settings_modifyconfirmbtn.isEnabled = false
            }
        }
    }

    override fun setThumbnailFromUri(uri: String?) {
        tmpImgUri = uri
        Glide.with(fragmentContext)
            .load(uri)
            .apply(RequestOptions.overrideOf(128, 128))
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .thumbnail(0.5F)
            .circleCrop()
            .into(settings_userimage)
    }

    override fun showFullImgFromUri(uri: String?) {
        uri?.let {
            val intent = Intent(fragmentContext, ImgFullScreenActivity::class.java)
            intent.putExtra(getString(R.string.basic_img_key), uri)
            startActivity(intent)
        }
    }

    override fun showLogout() {
        val activity = activity as MainTabActivity
        activity.showAlertDialog(R.string.basic_settings_logout_dialog_msg) {
            startLoginActivity()
        }
    }

    override fun showWithdraw() {
        val activity = activity as MainTabActivity
        activity.showAlertDialogWithConfirmCancel(
            R.string.basic_settings_withdraw_dialog_msg,
            R.string.basic_settings_withdraw_dialog_confirmbtn_name,
            R.string.basic_dialog_cancelbtn_name
        ) { presenter?.withdraw() }
    }

    override fun showWithdrawConfirm() {
        val activity = activity as MainTabActivity
        activity.showAlertDialog(R.string.basic_settings_withdraw_confirm_dialog_msg) {
            startLoginActivity()
        }
    }

    override fun showWithdrawCancel() {
        val activity = activity as MainTabActivity
        activity.showAlertDialog(R.string.basic_settings_withdraw_cancel_dialog_msg, null)
    }

    override fun startLoginActivity() {
        val activity = activity as MainTabActivity
        val intent = Intent(fragmentContext, LoginActivity::class.java)
        startActivity(intent)
        activity.finish()
    }

    override fun showProgress() {
        val activity = activity as MainTabActivity
        activity.showProgressDialog(R.string.basic_loading)
    }

    override fun hideProgress() {
        val activity = activity as MainTabActivity
        activity.hideProgressDialog()
    }

    override fun showAlert() {
        val activity = activity as MainTabActivity
        activity.showAlertDialog(R.string.basic_alert_dialog_msg, null)
    }

    override fun onDetach() {
        super.onDetach()
        presenter?.close()
    }

    companion object {
        const val IMG_FROM_GALLERY = 10
        const val IMG_FROM_CAMERA = 11
    }
}
