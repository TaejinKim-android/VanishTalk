package com.tk.vanishtalk.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.tk.vanishtalk.BaseActivity
import com.tk.vanishtalk.R
import com.tk.vanishtalk.maintab.MainTabActivity
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.SharedPreferencesAdapter

class LoginActivity : BaseActivity(), LoginContract.View {

    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter().apply {
            view = this@LoginActivity
            localSPModel = SharedPreferencesAdapter.getInstance(this@LoginActivity)
            localSQLModel = SQLiteAdapter.getInstance(this@LoginActivity)
        }

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val response: IdpResponse? = IdpResponse.fromResultIntent(data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            presenter.setMyInfo(null)
        } else {
            if (response == null) {
                Toast.makeText(this, getString(R.string.fui_cancel), LENGTH_SHORT).show()
                finish()
                return
            }

            if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, getString(R.string.basic_error_no_network), LENGTH_SHORT).show()
                finish()
                return
            }
        }
    }

    override fun startMainTabActivity() {
        val intent = Intent(this, MainTabActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showProgress() {
        showProgressDialog(R.string.basic_loading)
    }

    override fun hideProgress() {
        hideProgressDialog()
    }

    override fun showAlert() {
        showAlertDialog(R.string.basic_alert_dialog_msg) { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.close()
    }

    companion object {
        private const val RC_SIGN_IN = 101
    }
}
