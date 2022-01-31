package vegabobo.dsusideloader

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RunOnAdbActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_on_adb)

        val value = intent.getStringExtra("cmdline")

        val tvAdbCommandFull = findViewById<TextView>(R.id.tv_adbcommandfull)
        val tvAdbCommandQuick = findViewById<TextView>(R.id.tv_adbquick)

        tvAdbCommandFull.text = value
        tvAdbCommandQuick.text = value!!.replace("adb shell ", "")

    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.close))
            .setMessage(R.string.close_app_question)
            .setPositiveButton(R.string.close) { _, _ -> finishAffinity() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

}