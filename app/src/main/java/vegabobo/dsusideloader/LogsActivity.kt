package vegabobo.dsusideloader

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import vegabobo.dsusideloader.dsuhelper.GsiDsuObject
import vegabobo.dsusideloader.dsuhelper.RootDSUDeployer
import java.io.IOException
import java.io.OutputStream


class LogsActivity : AppCompatActivity() {

    var logsRaw = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        RootDSUDeployer(intent.extras!!.get("dsu") as GsiDsuObject)

        val tvLog = findViewById<TextView>(R.id.tv_logs)
        val btnSaveLogs = findViewById<Button>(R.id.btnSaveLogs)

        var logs = ""

        val callbackList: CallbackList<String?> = object : CallbackList<String?>() {
            override fun onAddElement(s: String?) {
                logsRaw = logsRaw + s + "\n"
                val p = (s as String).split(" ", limit = 5)[4]
                logs = logs + p + "\n"
                tvLog.text = logs
            }
        }

        Shell.su("logcat | grep -e gsid -e dynsys | grep -v SHELLOUT | grep -v SHELLIN | grep -v SHELL_OUT | grep -v SHELL_IN")
            .to(callbackList)
            .submit {}

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data!!.data
                    writeFile(uri!!)
                }
            }

        btnSaveLogs.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TITLE, "logs")
            resultLauncher.launch(intent)
        }

    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@LogsActivity)
            .setTitle(R.string.close_app)
            .setMessage(R.string.close_app_question)
            .setPositiveButton(R.string.close_app) { _, _ -> finishAffinity() }
            .setCancelable(true)
            .show()
    }

    private fun writeFile(uri: Uri) {
        try {
            val output: OutputStream? =
                this@LogsActivity.contentResolver.openOutputStream(uri)
            output!!.write(logsRaw.toByteArray())
            output.flush()
            output.close()
            Toast.makeText(this@LogsActivity, getString(R.string.logs_saved), Toast.LENGTH_SHORT)
                .show()
        } catch (e: IOException) {
            Toast.makeText(this@LogsActivity, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

}