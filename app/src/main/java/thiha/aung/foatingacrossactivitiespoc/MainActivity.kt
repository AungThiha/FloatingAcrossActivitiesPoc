package thiha.aung.foatingacrossactivitiespoc

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.jack8.floatwindow.Window.WindowStruct
import com.jack8.floatwindow.Window.WindowStruct.constructionAndDeconstructionWindow
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        startService.setOnClickListener {
            startWindowService()
        }
        startActivityA.setOnClickListener {
            startActivity(Intent(this, AActivity::class.java))
        }
        startActivityB.setOnClickListener {
            startActivity(Intent(this, BActivity::class.java))
        }
    }

    private fun startWindowService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            &&
            !Settings.canDrawOverlays(this@MainActivity)
        ) {
            startActivityForResult(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this@MainActivity.packageName)
                ), 1
            )
        } else {
            startFloatWindow()
        }
    }

    private fun startFloatWindow() {
        WindowStruct.Builder(this@MainActivity, getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .windowPages(intArrayOf(R.layout.hello_page_1, R.layout.hello_page_2))
            .windowPageTitles(arrayOf("Hello FloatWindow", "Submit Hello"))
            .constructionAndDeconstructionWindow(object : constructionAndDeconstructionWindow {

                var helloString = ""

                override fun Construction(
                    context: Context?,
                    view: View,
                    i: Int,
                    objects: Array<Any?>?,
                    windowStruct: WindowStruct
                ) {
                    when (i) {
                        0 -> view.findViewById<Button>(R.id.get_hello)
                            .setOnClickListener { windowStruct.showPage(1) }
                        1 -> view.findViewById<Button>(R.id.submit)
                            .setOnClickListener(object : View.OnClickListener {

                                var helloEdit: EditText =
                                    view.findViewById(R.id.hello_string)

                                override fun onClick(v: View?) {
                                    helloString = helloEdit.text.toString()
                                    windowStruct.showPage(0)
                                }

                            })
                    }
                }

                override fun Deconstruction(
                    context: Context?,
                    view: View?,
                    i: Int,
                    windowStruct: WindowStruct?
                ) {
                }

                override fun onResume(
                    context: Context?,
                    view: View,
                    i: Int,
                    windowStruct: WindowStruct?
                ) {
                    if (i == 0) {
                        view.findViewById<TextView>(R.id.hello_string_view).text =
                            "Hello $helloString"
                    }
                }

                override fun onPause(
                    context: Context?,
                    view: View?,
                    i: Int,
                    windowStruct: WindowStruct?
                ) {
                }

            }).show()
    }

    /*private fun startFloatWindow() {
        val intent = Intent(this, FloatingWindowService::class.java)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(intent)
        } else {
            startForegroundService(intent)
        }
        finish()
    }*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (Settings.canDrawOverlays(this)) startFloatWindow() else finish()
        }
    }

}
