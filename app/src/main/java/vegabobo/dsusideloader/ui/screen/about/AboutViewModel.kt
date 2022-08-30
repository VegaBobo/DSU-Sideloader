package vegabobo.dsusideloader.ui.screen.about

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import vegabobo.dsusideloader.BuildConfig
import vegabobo.dsusideloader.preferences.AppPrefs
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

@Serializable
data class UpdaterResponse(
    val identifier: String = "",
    val versionCode: Int = -1,
    val versionName: String = "",
    val apkUrl: String = "",
    val changelogUrl: String = ""
)

@HiltViewModel
class AboutViewModel @Inject constructor(
    val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutScreenUiState())
    val uiState: StateFlow<AboutScreenUiState> = _uiState.asStateFlow()
    var response = UpdaterResponse()

    private fun updateUpdaterCard(update: (UpdaterCardState) -> UpdaterCardState) =
        _uiState.update { it.copy(updaterCardState = update(it.updaterCardState.copy())) }

    fun onClickCheckUpdates() {
        updateUpdaterCard { it.copy(updateStatus = UpdateStatus.CHECKING_FOR_UPDATES) }
        viewModelScope.launch(Dispatchers.IO) {
            val apiResponse = try {
                URL(AppPrefs.UPDATE_CHECK_URL).readText()
            } catch (e: Exception) {
                updateUpdaterCard { it.copy(updateStatus = UpdateStatus.NO_UPDATE_FOUND) }
                return@launch
            }
            response = Json.decodeFromString(UpdaterResponse.serializer(), apiResponse)
            updateUpdaterCard { it.copy(updateVersion = response.versionName) }
            if (response.versionCode > BuildConfig.VERSION_CODE)
                updateUpdaterCard { it.copy(updateStatus = UpdateStatus.UPDATE_FOUND) }
            else
                updateUpdaterCard { it.copy(updateStatus = UpdateStatus.NO_UPDATE_FOUND) }

        }
    }

    fun onClickDownloadUpdate() {
        if (uiState.value.updaterCardState.isDownloading)
            return
        updateUpdaterCard { it.copy(isDownloading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val finalFile = File(application.filesDir.path + "/update.apk")
            val length = try {
                URL(response.apkUrl).openConnection().contentLengthLong
            } catch (e: Exception) {
                updateUpdaterCard { it.copy(isDownloading = false) }
                return@launch
            }
            val input = try {
                URL(response.apkUrl).openStream()
            } catch (e: Exception) {
                updateUpdaterCard { it.copy(isDownloading = false) }
                return@launch
            }
            val output = FileOutputStream(finalFile)

            val buffer = ByteArray(8 * 1024)
            var n: Int
            var readed: Long = 0
            while (-1 != input.read(buffer)
                    .also { n = it }
            ) {
                readed += buffer.size
                output.write(buffer, 0, n)
                updateUpdaterCard { it.copy(progressBar = readed.toFloat() / length.toFloat()) }
            }
            input.close()
            output.close()

            updateUpdaterCard { it.copy(isDownloading = false) }
            val apkUri = FileProvider.getUriForFile(
                application,
                BuildConfig.APPLICATION_ID + ".provider",
                finalFile
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            intent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags += Intent.FLAG_GRANT_READ_URI_PERMISSION
            application.startActivity(intent)
        }
    }

}