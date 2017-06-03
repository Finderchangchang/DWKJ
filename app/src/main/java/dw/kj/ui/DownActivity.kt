package dw.kj.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadTask
import com.lzy.okgo.OkGo
import com.lzy.okserver.download.DownloadManager
import com.lzy.okserver.download.DownloadService
import dw.kj.BaseActivity

import dw.kj.R
import dw.kj.model.ApkModel
import kotlinx.android.synthetic.main.activity_down.*

/**
 * 下载页
 * */
class DownActivity : BaseActivity() {
    internal var downloadManager: DownloadManager? = null
    override fun setLayout(): Int {
        return R.layout.activity_down
    }

    private var apkModel: ApkModel = ApkModel()
    val url = "http://gr.rungo.net/uploads/video/8/14962033874568.mp4"
    override fun initViews() {
        apkModel.name = "美丽加"
        apkModel.iconUrl = "http://pic3.apk8.com/small2/14325422596306671.png"
        apkModel.url = "http://gr.rungo.net/uploads/video/8/14962033874568.mp4"
        downloadManager = DownloadService.getDownloadManager()
        downloadManager!!.targetFolder = Environment.getExternalStorageDirectory().absolutePath + "/aaa/"
        down_btn.setOnClickListener {
            if (downloadManager!!.getDownloadInfo(url) != null) {
                Toast.makeText(applicationContext, "任务已经在下载列表中", Toast.LENGTH_SHORT).show()
            } else {
                val request = OkGo.get(url)
                downloadManager!!.addTask(url, apkModel, request, null)
            }
        }
        down_manage_btn.setOnClickListener {
            startActivity(Intent(this@DownActivity, DownManageActivity::class.java))
        }
    }

    override fun initEvents() {

    }

}
