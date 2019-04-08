package com.tk.vanishtalk.initiate

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.tk.vanishtalk.BaseActivity
import com.tk.vanishtalk.R
import com.tk.vanishtalk.login.LoginActivity
import com.tk.vanishtalk.maintab.MainTabActivity
import com.tk.vanishtalk.model.SQLiteAdapter
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class InitActivity : BaseActivity(), InitContract.View, EasyPermissions.PermissionCallbacks {

    private lateinit var presenter: InitPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        presenter = InitPresenter().apply {
            view = this@InitActivity
            localSQLiteAdapter = SQLiteAdapter.getInstance(this@InitActivity)
        }
        checkPermission()
    }



    override fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun startMainTabActivity() {
        val intent = Intent(this, MainTabActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        EasyPermissions.requestPermissions(this,
            getString(R.string.basic_request_permission),
            PERMISSION_REQUEST_CODE)
        finish()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    @AfterPermissionGranted(PERMISSION_REQUEST_CODE)
    private fun checkPermission() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            presenter.checkLoggedIn()
        } else {
            //사용자가 권한을 아직 허용하지 않았을 때 뜨는 Dialog
            EasyPermissions.requestPermissions(this,
                getString(R.string.basic_request_permission),
                PERMISSION_REQUEST_CODE,
                *perms)
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 99
    }
}
