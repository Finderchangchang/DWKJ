package dw.kj.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import dw.kj.BaseActivity

import dw.kj.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_main
    }

    override fun initViews() {
        //跳转到设置ip端口页面
        set_ip_ib.setOnClickListener { startActivity(Intent(this, SetIpActivity::class.java)) }
    }

    override fun initEvents() {
    }
}
