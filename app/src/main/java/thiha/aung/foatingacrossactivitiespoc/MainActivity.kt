package thiha.aung.foatingacrossactivitiespoc

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import thiha.aung.foatingacrossactivitiespoc.floatwindow.FloatWindowManager
import thiha.aung.foatingacrossactivitiespoc.floatwindow.FloatWindowService

const val REQUEST_PERMISSION_SYSTEM_ALERT_WINDOW = 120

class MainActivity : AppCompatActivity() {

    private lateinit var floatWindowManager: FloatWindowManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        floatWindowManager = FloatWindowManager.getInstance(this)
        initViews()
        setupPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_SYSTEM_ALERT_WINDOW) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.i("", "Permission has been denied by user")
            } else {
                Log.i("", "Permission has been granted by user")
            }
        }
    }

    private fun initViews() {
        startService.setOnClickListener {
            val intent = Intent(this, FloatingWebViewService::class.java)
                .putExtra("url", "https://www.youtube.com/watch?v=zuKgWw0cTkE")
            startService(intent)
        }
        showSmall.setOnClickListener {
            // 需要传递小悬浮窗布局，以及根布局的id，启动后台服务
            val intent = Intent(this, FloatWindowService::class.java)
            intent.putExtra(
                FloatWindowService.LAYOUT_RES_ID,
                R.layout.float_window_small
            )
            intent.putExtra(
                FloatWindowService.ROOT_LAYOUT_ID,
                R.id.small_window_layout
            )
            startService(intent)
        }
        showBig.setOnClickListener {
            // 设置小悬浮窗的单击事件
            floatWindowManager.setOnClickListener {
                floatWindowManager.createBigWindow(this@MainActivity)
            }
        }
        startActivityA.setOnClickListener {
            startActivity(Intent(this, AActivity::class.java))
        }
        startActivityB.setOnClickListener {
            startActivity(Intent(this, BActivity::class.java))
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SYSTEM_ALERT_WINDOW
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
            REQUEST_PERMISSION_SYSTEM_ALERT_WINDOW
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        // 返回键移除二级悬浮窗
        if (keyCode == KeyEvent.KEYCODE_BACK
            && event.action == KeyEvent.ACTION_DOWN
        ) {
            floatWindowManager.removeBigWindow()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
