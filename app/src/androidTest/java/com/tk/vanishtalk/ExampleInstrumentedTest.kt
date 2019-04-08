package com.tk.vanishtalk

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.tk.vanishtalk.model.SQLiteAdapter
import com.tk.vanishtalk.model.data.local.LocalUser

import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun sql() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val db = SQLiteAdapter.getInstance(appContext)

        val user = LocalUser(
            "email",
            "name",
            null,
            "email",
            null
        )
        db.updateUserInfo(user)
    }

    fun handle(list: Response<String>) {
        if (list.code() == 200) {
            val jsonParser = JsonParser()
            for (item in jsonParser.parse(list.body()) as JsonArray) {
                Log.e("!!!", item.asString)
            }
        } else {
            Log.e("!!!", list.code().toString())
        }

    }
}
