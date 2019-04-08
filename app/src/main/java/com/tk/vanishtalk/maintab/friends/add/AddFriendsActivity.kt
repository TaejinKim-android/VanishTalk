package com.tk.vanishtalk.maintab.friends.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.tk.vanishtalk.BaseActivity
import com.tk.vanishtalk.R
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter
import com.tk.vanishtalk.model.data.local.LocalUser
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : BaseActivity(), AddFriendsContract.View, View.OnClickListener {

    private var presenter: AddFriendsPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        presenter = AddFriendsPresenter().apply {
            view = this@AddFriendsActivity
            localSQLModel = SQLiteAdapter.getInstance(this@AddFriendsActivity)
            localSpModel = SharedPreferencesAdapter.getInstance(this@AddFriendsActivity)
        }

        add_friends_inputfriendemailtext.addTextChangedListener(object : TextWatcher {
            private val changedTextSubject = PublishSubject.create<String>().toSerialized()
            init {
                changedTextSubject.onNext("")
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

        add_friends_cancelbtn.setOnClickListener(this)
        add_friends_addbtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.add_friends_cancelbtn -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            R.id.add_friends_addbtn -> presenter?.addFriend(add_friends_inputfriendemailtext.text.toString())
        }
    }

    override fun showInputValueStatus(isMyEmail: Boolean) {
        when(isMyEmail) {
            true -> {
                add_friends_warntext.text = getString(R.string.basic_add_friends_warntext)
                add_friends_addbtn.isEnabled = false
            }
            else -> {
                add_friends_warntext.text = null
                add_friends_addbtn.isEnabled = true
            }
        }
    }

    override fun showProgress() {
        showProgressDialog(R.string.basic_loading)
    }

    override fun hideProgress() {
        hideProgressDialog()
    }

    override fun showAlert() {
        showAlertDialog(R.string.basic_add_friends_no_email, null)
    }

    override fun sendResultToFriendsFragment(friend: LocalUser) {
        val intent = Intent()
        intent.putExtra("friend", friend)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.close()
    }
}
