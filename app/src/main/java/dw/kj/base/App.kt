package dw.kj

import android.app.Application
import android.content.Context
import com.lzy.okgo.OkGo
import com.lzy.okgo.cookie.store.MemoryCookieStore
import com.lzy.okgo.cookie.store.PersistentCookieStore


/**
 * Created by Finder丶畅畅 on 2017/1/14 21:25
 * QQ群481606175
 */

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        OkGo.init(this)
    }

    companion object {
        var context: Context? = null
        var wx_id: String = "wxc281dccd97667c78"//微信的id
        var wx_secret = "6992b3bac3a2594835eb7bc7e3791c78"
        var qq_id = "1106115761"
    }
}
