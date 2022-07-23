package vegabobo.dsusideloader.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.Base64
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.installation.Deploy
import vegabobo.dsusideloader.model.TargetGSI
import vegabobo.dsusideloader.preparation.PrepareFile
import vegabobo.dsusideloader.preparation.StorageManager
import vegabobo.dsusideloader.ui.Destinations
import vegabobo.dsusideloader.ui.screens.settings.Preference
import vegabobo.dsusideloader.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val storageAccess: StorageManager,
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val adbInstallation = MutableStateFlow("")

    init {
        isDynamicPartition()
        hasAvailableStorage()
        isStorageAccessAllowed()
    }

    var installationJob = Job()
    var gsiToBeInstalled = TargetGSI()

    var maxAllocationUserdata = StorageUtils.maximumAllowedAllocation()

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
        _uiState.update {
            it.copy(isCustomUserdataSelected = it.isCustomUserdataSelected.not())
        }
    }

    fun updateUserdataSize(input: String) {
        val selectedSize = FilenameUtils.getDigits(input)
        var sizeWithSuffix = FilenameUtils.appendToString(input, "GB")
        if (selectedSize.isNotEmpty() && selectedSize.toInt() > maxAllocationUserdata) {
            sizeWithSuffix = "${maxAllocationUserdata}GB"
            _uiState.update {
                it.copy(
                    isCustomUserdataError = true,
                    maximumAllowedAlloc = maxAllocationUserdata
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                Thread.sleep(5000)
                _uiState.update { it.copy(isCustomUserdataError = false) }
            }
        }
        _uiState.update { it.copy(userdataFieldText = sizeWithSuffix) }
    }

    // Imagesize card
    fun onCheckImageSizeCard() {
        _uiState.update {
            it.copy(
                isCustomImageSizeSelected = it.isCustomImageSizeSelected.not(),
                showImageSizeDialog = !uiState.value.isCustomImageSizeSelected
            )
        }
    }

    fun updateImageSize(input: String) {
        val inputWithSuffix = FilenameUtils.appendToString(input, "b")
        _uiState.update { it.copy(imageSizeFieldText = inputWithSuffix) }
    }

    // Installation dialog
    fun onConfirmInstallationDialog() {
        _uiState.update { it.copy(showInstallationDialog = false, isInstalling = true) }
        if (installationJob.isCancelled)
            installationJob = Job()
        viewModelScope.launch(Dispatchers.IO + installationJob) {
            PrepareFile(
                storageAccess, gsiToBeInstalled, installationJob,
                onProgressChange = { progress ->
                    _uiState.update { it.copy(installationProgress = progress) }
                },
                onInstallationStepChange = { step ->
                    _uiState.update { it.copy(installationStep = step) }
                },
                onPreparationSuccess = { gsiReadyToInstall ->
                    onClickCancelInstallationButton()
                    onClickClearButton()
                    if (OperationMode.getOperationMode() == OperationMode.Constants.UNROOTED) {
                        val installationCmd =
                            Deploy(storageAccess, gsiReadyToInstall).getInstallationCommand()
                        adbInstallation.value = Base64.encodeToString(installationCmd.toByteArray(), Base64.DEFAULT)
                        return@PrepareFile
                    }
                    Deploy(storageAccess, gsiReadyToInstall)
                }).invoke()
        }
    }

    fun onCancelInstallationDialog() {
        gsiToBeInstalled = TargetGSI()
        _uiState.update { it.copy(showInstallationDialog = false, isInstalling = false) }
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
    fun onSelectFileResult(launcher: ActivityResultLauncher<Intent>) {
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
        launcher.launch(chooseFile)
    }

    fun onSelectFileSuccessfully(uri: Uri, filename: String) {
        gsiToBeInstalled.targetUri = uri
        gsiToBeInstalled.name = filename
        _uiState.update {
            it.copy(
                installationFieldText = gsiToBeInstalled.name,
                isInstallationFieldEnabled = false,
                isInstallable = true,
            )
        }
    }

    // Warning cards
    fun onClickIgnoreStorageWarning(hasAvailableStorage: Boolean) {
        _uiState.update { it.copy(showLowStorageCard = hasAvailableStorage) }
    }

    fun isDeviceCompatible(): Boolean {
        return !uiState.value.showUnsupportedCard &&
                !uiState.value.showSetupStorageCard &&
                !uiState.value.showLowStorageCard
    }

    // Image size dialog
    fun onClickCancelImageSizeDialog() {
        _uiState.update { it.copy(showImageSizeDialog = false, isCustomImageSizeSelected = false) }
    }

    fun onClickConfirmImageSizeDialog() {
        _uiState.update { it.copy(showImageSizeDialog = false) }
    }

    // Intents
    fun launchUrl(context: Context, url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url);
        context.startActivity(i);
    }

    fun onClickViewDocs(context: Context) {
        val url = "https://source.android.com/devices/tech/ota/dynamic-system-updates"
        launchUrl(context, url)
    }

    fun onClickLearnMore(context: Context) {
        val url = "https://developer.android.com/topic/dsu"
        launchUrl(context, url)
    }

    // Setup storage
    fun onClickSetupStorage(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        launcher.launch(i)
    }

    fun isStorageAccessAllowed() {
        viewModelScope.launch {
            val path = DataStoreUtils.readStringPref(dataStore, Preference.SAF_PATH, "")
            val arePermissionsGranted = storageAccess.arePermissionsGrantedToFolder(path)
            _uiState.update { it.copy(showSetupStorageCard = !arePermissionsGranted) }
        }
    }

    fun onSetupStorageSuccess(path: String) {
        viewModelScope.launch {
            DataStoreUtils.updateStringPref(dataStore, Preference.SAF_PATH, path)
        }
        _uiState.update { it.copy(showSetupStorageCard = false) }
    }

    // Verifications
    fun isDynamicPartition() {
        if (!VerificationUtils.hasDynamicPartitions())
            _uiState.update { it.copy(showUnsupportedCard = true) }
    }

    fun hasAvailableStorage() {
        if (!StorageUtils.hasAvailableStorage())
            _uiState.update { it.copy(showLowStorageCard = true) }
    }

}