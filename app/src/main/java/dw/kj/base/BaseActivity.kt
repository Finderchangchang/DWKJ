package dw.kj

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

/**
 * BaseActivity声明相关通用方法
 *
 *
 * Created by LiuWeiJie on 2015/7/22 0022.
 */
abstract class BaseActivity : AppCompatActivity() {
    internal var layoutid: Int = 0
    internal var dialog: ProgressDialog? = null

    abstract fun setLayout(): Int

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutid = setLayout()
        if (layoutid != 0) {
            setContentView(layoutid)
        }
        initViews()
        initEvents()
    }

    abstract fun initViews()

    abstract fun initEvents()

    private var toast: Toast? = null

    fun toast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg)
        }
        toast!!.show()
    }
}
