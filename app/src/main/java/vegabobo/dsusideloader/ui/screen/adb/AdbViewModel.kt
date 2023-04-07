package vegabobo.dsusideloader.ui.screen.adb

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import vegabobo.dsusideloader.model.Session

@HiltViewModel
class AdbViewModel @Inject constructor(
    private val session: Session,
) : ViewModel() {

    fun obtainScriptPath(): String = session.installationScriptPath
}
