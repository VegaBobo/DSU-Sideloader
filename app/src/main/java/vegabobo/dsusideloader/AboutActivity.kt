package vegabobo.dsusideloader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        findViewById<TextView>(R.id.tv_version).text =
            getString(R.string.version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }

    private fun launchUrlIntent(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    fun btnGitHubSource(view: View) {
        launchUrlIntent("https://github.com/VegaBobo/DSU-Sideloader")
    }

    fun btnAuthorLink(view: View) {
        launchUrlIntent("https://github.com/VegaBobo/")
    }

    fun btnXzUtils(view: View) {
        launchUrlIntent("https://tukaani.org/xz/")
    }

    fun btnLibsu(view: View) {
        launchUrlIntent("https://github.com/topjohnwu/libsu")
    }

    fun btnGoogle(view: View) {
        launchUrlIntent("https://developer.android.com/")
    }
}
