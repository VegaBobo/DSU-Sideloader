package vegabobo.dsusideloader.ui.screen.adb

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import vegabobo.dsusideloader.model.Session
import javax.inject.Inject

@HiltViewModel
class AdbViewModel @Inject constructor(
    private val session: Session
) : ViewModel() {

    fun obtainScriptPath(): String = session.installationScript

}
