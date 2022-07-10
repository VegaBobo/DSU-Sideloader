package vegabobo.dsusideloader.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vegabobo.dsusideloader.ActivityAction
import vegabobo.dsusideloader.NewMainActivity
import vegabobo.dsusideloader.dsuhelper.GSI
import vegabobo.dsusideloader.dsuhelper.PrepareDsu
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.SPUtils

data class HomeUiState(

    // Installation card
    val installationFieldText: String = "",
    val isInstallationFieldEnabled: Boolean = true,
    val isInstallable: Boolean = false,

    // Userdata card
    val isCustomUserdataSelected: Boolean = false,
    val userdataFieldText: String = "",

    // ImageSize card
    val isCustomImageSizeSelected: Boolean = false,
    val imageSizeFieldText: String = "",

    // Warning cards
    val showSetupStorageCard: Boolean = false,
    val showLowStorageCard: Boolean = false,
    val showUnsupportedCard: Boolean = false,

    // Installation
    val showInstallationDialog: Boolean = false,
    val isInstalling: Boolean = false,
    val installationText: String = "",
    val installationProgress: Float = 0.0f,
    val showCancelDialog: Boolean = false
)

class HomeViewModel : ViewModel() {

    var installationJob = Job()

    // UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // used to call actions in MainActivity
    val activityAction: MutableStateFlow<Int> = MutableStateFlow(ActivityAction.NONE)

    var gsiToBeInstalled = GSI()

    // Installation card

    fun onClickInstallOrCancelButton(isInstalling: Boolean) {
        if (isInstalling) {
            _uiState.update { it.copy(showCancelDialog = true) }
            return
        }
        gsiToBeInstalled.setUserdataSize(uiState.value.userdataFieldText)
        gsiToBeInstalled.setFileSize(uiState.value.imageSizeFieldText)
        _uiState.update { it.copy(showInstallationDialog = true) }

    }

    fun onClickClearButton() {
        _uiState.update {
            it.copy(
                installationFieldText = "",
                isInstallationFieldEnabled = true,
                isInstallable = false,
            )
        }
    }

    // Userdata card

    fun onCheckUserdataCard() {
        _uiState.update { it.copy(isCustomUserdataSelected = it.isCustomUserdataSelected.not()) }
    }

    fun updateUserdataSize(input: String) {
        val inputWithSuffix = FilenameUtils.appendToString(input, "GB")
        _uiState.update { it.copy(userdataFieldText = inputWithSuffix) }
    }

    // Imagesize card

    fun onCheckImageSizeCard() {
        _uiState.update { it.copy(isCustomImageSizeSelected = it.isCustomImageSizeSelected.not()) }
    }

    fun updateImageSize(input: String) {
        val inputWithSuffix = FilenameUtils.appendToString(input, "b")
        _uiState.update { it.copy(imageSizeFieldText = inputWithSuffix) }
    }

    // Installation dialog

    fun onConfirmInstallationDialog() {
        activityAction.value = ActivityAction.INSTALL_GSI
    }

    fun onCancelInstallationDialog() {
        _uiState.update { it.copy(showInstallationDialog = false, isInstalling = false) }
    }

    fun onConfirmInstallationAction(appContext: Context) {
        _uiState.update { it.copy(showInstallationDialog = false, isInstalling = true) }
        if (installationJob.isCancelled)
            installationJob = Job()
        viewModelScope.launch(Dispatchers.IO + installationJob) {
            runBlocking(Dispatchers.IO) {
                PrepareDsu(appContext, gsiToBeInstalled, this@HomeViewModel).run()
                onClickClearButton()
            }
        }
    }

    // Cancel dialog

    fun onClickCancelInstallationButton() {
        if (installationJob.isActive)
            installationJob.cancel()
        _uiState.update {
            it.copy(
                isInstalling = false,
                isInstallable = true,
                showCancelDialog = false
            )
        }
    }

    fun onDismissCancelDialog() {
        _uiState.update { it.copy(showCancelDialog = false) }
    }

    // File selection (installation card)

    fun onSelectFileAction() {
        activityAction.value = ActivityAction.OPEN_FILE_SELECTION
    }

    fun onSelectFileResult(arl: ActivityResultLauncher<Intent>) {
        var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
        chooseFile.type = "*/*"
        val mimetypes = arrayOf(
            "application/gzip",
            "application/x-gzip",
            "application/x-xz",
            "application/zip",
            "application/octet-stream"
        )
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        chooseFile = Intent.createChooser(chooseFile, "")
        arl.launch(chooseFile)
    }

    fun onSelectFileSuccessfully(uri: Uri, activity: NewMainActivity) {
        gsiToBeInstalled.targetUri = uri
        gsiToBeInstalled.name = FilenameUtils.queryName(activity.contentResolver, uri)
        _uiState.update {
            it.copy(
                installationFieldText = gsiToBeInstalled.name,
                isInstallationFieldEnabled = false,
                isInstallable = true,
            )
        }
    }

    // Setup storage

    fun onSetupStorageAction() {
        activityAction.value = ActivityAction.SETUP_FILE_ACCESS
    }

    fun onSetupStorageResult(arl: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        arl.launch(intent)
    }

    fun onSetupStorageSuccessfully(data: Intent, activity: NewMainActivity) {
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        activity.contentResolver.takePersistableUriPermission(
            data.data!!,
            takeFlags
        )
        SPUtils.writeSafRwPath(activity, data.data.toString())
        _uiState.update { it.copy(showSetupStorageCard = false) }
    }

    // Installation

    fun updateInstallationText(text: String) {
        _uiState.update { it.copy(installationText = text) }
    }

    // Warning cards

    fun showSetupStorageCard(arePermissionsGranted: Boolean) {
        _uiState.update { it.copy(showSetupStorageCard = arePermissionsGranted) }
    }

    fun showNoDynamicPartitionsCard(isDeviceSupported: Boolean) {
        _uiState.update { it.copy(showUnsupportedCard = isDeviceSupported) }
    }

    fun showNoAvailStorageCard(hasAvailableStorage: Boolean) {
        _uiState.update { it.copy(showLowStorageCard = hasAvailableStorage) }
    }

    fun isDeviceCompatible(): Boolean {
        return !uiState.value.showUnsupportedCard &&
                !uiState.value.showSetupStorageCard &&
                !uiState.value.showLowStorageCard
    }

    // Other

    fun finishAppAction() {
        activityAction.value = ActivityAction.FINISH_APP
    }

    fun updateProgress(progress: Float) {
        _uiState.update { it.copy(installationProgress = progress) }
    }

}