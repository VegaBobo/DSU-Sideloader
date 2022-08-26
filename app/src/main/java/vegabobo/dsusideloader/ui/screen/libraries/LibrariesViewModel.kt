package vegabobo.dsusideloader.ui.screen.libraries

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibrariesViewModel @Inject constructor(
    val application: Application
) : ViewModel() {

    fun openUrl(url: String) {
        if (url.isEmpty())
            return
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
        application.startActivity(intent)
    }

}