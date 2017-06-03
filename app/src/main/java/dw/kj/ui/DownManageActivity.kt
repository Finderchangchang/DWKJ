package dw.kj.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadTask
import com.bumptech.glide.Glide
import com.lzy.okserver.download.DownloadInfo
import com.lzy.okserver.download.DownloadManager
import com.lzy.okserver.download.DownloadService
import com.lzy.okserver.listener.DownloadListener
import com.lzy.okserver.task.ExecutorWithListener

import dw.kj.R
import dw.kj.model.ApkModel
import dw.kj.view.NumberProgressBar
import kotlinx.android.synthetic.main.activity_down_manage.*
import java.io.File

/**
 * 下载管理页
 * */
class DownManageActivity : AppCompatActivity(), ExecutorWithListener.OnAllTaskEndListener {
    private var allTask: List<DownloadInfo>? = null
    override fun onAllTaskEnd() {
        for (downloadInfo in allTask!!) {
            if (downloadInfo.state != DownloadManager.FINISH) {
                Toast.makeText(this, "所有下载线程结束，部分下载未完成", Toast.LENGTH_SHORT).show()
                return
            }
        }
        Toast.makeText(this, "所有下载任务完成", Toast.LENGTH_SHORT).show()
    }

    private var downloadManager: DownloadManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_down_manage)
        downloadManager = DownloadService.getDownloadManager()
        allTask = downloadManager!!.getAllTask()
        adapter = MyAdapter()
        main_lv.setAdapter(adapter)
        val s = ""
        downloadManager!!.threadPool.executor.addOnAllTaskEndListener(this)
        del_btn.setOnClickListener {
            downloadManager!!.removeAllTask()
            adapter!!.notifyDataSetChanged()  //移除的时候需要调用
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //记得移除，否者会回调多次
        downloadManager!!.threadPool.executor.removeOnAllTaskEndListener(this)
    }

    private inner class MyAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return allTask!!.size
        }

        override fun getItem(position: Int): DownloadInfo {
            return allTask!!.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val downloadInfo = getItem(position)
            val holder: ViewHolder
            if (convertView == null) {
                convertView = View.inflate(this@DownManageActivity, R.layout.item_download_manager, null)
                holder = ViewHolder(convertView)
                convertView!!.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            val apk = downloadInfo.data as ApkModel
            Glide.with(this@DownManageActivity).load(apk.iconUrl).error(R.mipmap.ic_launcher).into(holder.icon)
            holder.name.text = apk.name
            holder.refresh(downloadInfo)
            //对于非进度更新的ui放在这里，对于实时更新的进度ui，放在holder中
            holder.download!!.setOnClickListener(holder)
            holder.remove!!.setOnClickListener(holder)
            holder.restart!!.setOnClickListener(holder)
            val downloadListener = MyDownloadListener()
            downloadListener.userTag = holder
            downloadInfo.listener = downloadListener
            return convertView
        }
    }

    private inner class ViewHolder(convertView: View) : View.OnClickListener {
        var downloadInfo: DownloadInfo? = null
        val icon: ImageView
        var name: TextView
        val downloadSize: TextView
        val tvProgress: TextView
        val netSpeed: TextView
        val pbProgress: NumberProgressBar
        val download: Button
        val remove: Button
        val restart: Button

        init {
            icon = convertView.findViewById(R.id.icon) as ImageView
            name = convertView.findViewById(R.id.name) as TextView
            downloadSize = convertView.findViewById(R.id.downloadSize) as TextView
            tvProgress = convertView.findViewById(R.id.tvProgress) as TextView
            netSpeed = convertView.findViewById(R.id.netSpeed) as TextView
            pbProgress = convertView.findViewById(R.id.pbProgress) as NumberProgressBar
            download = convertView.findViewById(R.id.start) as Button
            remove = convertView.findViewById(R.id.remove) as Button
            restart = convertView.findViewById(R.id.restart) as Button
        }

        fun refresh(downloadInfo: DownloadInfo) {
            this.downloadInfo = downloadInfo
            refresh()
        }

        //对于实时更新的进度ui，放在这里，例如进度的显示，而图片加载等，不要放在这，会不停的重复回调
        //也会导致内存泄漏
        fun refresh() {
            val downloadLength = Formatter.formatFileSize(this@DownManageActivity, downloadInfo!!.downloadLength)
            val totalLength = Formatter.formatFileSize(this@DownManageActivity, downloadInfo!!.totalLength)
            downloadSize.text = downloadLength + "/" + totalLength
            if (downloadInfo!!.state == DownloadManager.NONE) {
                netSpeed.text = "停止"
                download.text = "下载"
            } else if (downloadInfo!!.state == DownloadManager.PAUSE) {
                netSpeed.text = "暂停中"
                download.text = "继续"
            } else if (downloadInfo!!.state == DownloadManager.ERROR) {
                netSpeed.text = "下载出错"
                download.text = "出错"
            } else if (downloadInfo!!.state == DownloadManager.WAITING) {
                netSpeed.text = "等待中"
                download.text = "等待"
            } else if (downloadInfo!!.state == DownloadManager.FINISH) {
//                if (ApkUtils.isAvailable(this@DownManageActivity, File(downloadInfo!!.targetPath))) {
//                    download.text = "卸载"
//                } else {
//                    download.text = "安装"
//                }
                netSpeed.text = "下载完成"
            } else if (downloadInfo!!.state == DownloadManager.DOWNLOADING) {
                val networkSpeed = Formatter.formatFileSize(this@DownManageActivity, downloadInfo!!.networkSpeed)
                netSpeed.text = networkSpeed + "/s"
                download.text = "暂停"
            }
            tvProgress.text = (Math.round(downloadInfo!!.progress * 10000) * 1.0f / 100).toString() + "%"
            pbProgress.max = downloadInfo!!.totalLength.toInt()
            pbProgress.progress = downloadInfo!!.downloadLength.toInt()
        }

        override fun onClick(v: View) {
            if (v.id == download.id) {
                when (downloadInfo!!.state) {
                    DownloadManager.PAUSE, DownloadManager.NONE, DownloadManager.ERROR -> downloadManager!!.addTask(downloadInfo!!.url, downloadInfo!!.request, downloadInfo!!.listener)
                    DownloadManager.DOWNLOADING -> downloadManager!!.pauseTask(downloadInfo!!.url)
                }
                refresh()
            } else if (v.id == remove.id) {
                downloadManager!!.removeTask(downloadInfo!!.url)
                adapter!!.notifyDataSetChanged()
            } else if (v.id == restart.id) {
                downloadManager!!.restartTask(downloadInfo!!.url)
            }
        }
    }

    private var adapter: MyAdapter? = null

    private inner class MyDownloadListener : DownloadListener() {

        override fun onProgress(downloadInfo: DownloadInfo) {
            if (userTag == null) return
            val holder = userTag as ViewHolder
            holder.refresh()  //这里不能使用传递进来的 DownloadInfo，否者会出现条目错乱的问题
        }

        override fun onFinish(downloadInfo: DownloadInfo) {
            Toast.makeText(this@DownManageActivity, "下载完成:" + downloadInfo.targetPath, Toast.LENGTH_SHORT).show()
        }

        override fun onError(downloadInfo: DownloadInfo, errorMsg: String?, e: Exception) {
            if (errorMsg != null) Toast.makeText(this@DownManageActivity, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

}
