package thiha.aung.foatingacrossactivitiespoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initViews()
  }

  private fun initViews() {
    startService.setOnClickListener {
      // TODO start the floating service
    }
    startActivityA.setOnClickListener {

    }
    startActivityB.setOnClickListener {

    }
  }

}
