package vegabobo.dsusideloader.ui.screen.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.core.InstallationSession
import vegabobo.dsusideloader.core.StorageManager
import vegabobo.dsusideloader.installation.GenerateInstallationScript
import vegabobo.dsusideloader.installation.InstallationHandler
import vegabobo.dsusideloader.preferences.CorePreferences
import vegabobo.dsusideloader.preferences.UserPreferences
import vegabobo.dsusideloader.preparation.Preparation
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val application: Application,
    override val dataStore: DataStore<Preferences>,
    val storageAccess: StorageManager,
    val installationSession: InstallationSession,
) : BaseViewModel(dataStore) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    val homeViewAction = MutableStateFlow(HomeViewAction.NONE)

    var checkDynamicPartitions = true
    var checkUnavaiableStorage = true

    //
    // Home startup and checks
    //

    private fun initialChecks() {

        updateAdditionalCardState(AdditionalCard.NONE)

        if (checkDynamicPartitions && !VerificationUtils.hasDynamicPartitions()) {
            updateAdditionalCardState(AdditionalCard.NO_DYNAMIC_PARTITIONS)
            return
        }

        if (checkUnavaiableStorage && !StorageUtils.hasAvailableStorage()) {
            updateAdditionalCardState(AdditionalCard.UNAVAIABLE_STORAGE)
            return
        }

        readStringPref(CorePreferences.SAF_PATH) { result ->
            if (!storageAccess.arePermissionsGrantedToFolder(result))
                updateAdditionalCardState(AdditionalCard.UNAVAIABLE_STORAGE)
        }

        _uiState.update { it.copy(canInstall = true) }
    }

    private fun setupUserPreferences() {
        readBoolPref(UserPreferences.KEEP_SCREEN_ON) { result ->
            _uiState.update { it.copy(shouldKeepScreenOn = result) }
        }
    }

    fun overrideDynamicPartitionCheck() {
        checkDynamicPartitions = false
        initialChecks()
    }

    fun overrideUnavaiableStorage() {
        checkUnavaiableStorage = false
        initialChecks()
    }

    init {
        initialChecks()
        setupUserPreferences()
    }

    //
    // Helper methods used for controlling UI State
    //

    private fun updateAdditionalCardState(additionalCard: AdditionalCard) =
        _uiState.update { it.copy(additionalCard = additionalCard) }

    private fun updateUserdataCard(update: (UserDataCardState) -> UserDataCardState) =
        _uiState.update { it.copy(userDataCard = update(it.userDataCard.copy())) }

    private fun updateInstallationCard(update: (InstallationCardState) -> InstallationCardState) =
        _uiState.update { it.copy(installationCard = update(it.installationCard.copy())) }

    private fun updateImageSizeCard(update: (ImageSizeCardState) -> ImageSizeCardState) =
        _uiState.update { it.copy(imageSizeCard = update(it.imageSizeCard.copy())) }

    private fun updateDialogState(dialogDisplay: DialogDisplay) =
        _uiState.update { it.copy(dialogDisplay = dialogDisplay) }

    private fun viewAction(action: HomeViewAction) {
        homeViewAction.value = action
    }

    fun dismissDialog() = updateDialogState(DialogDisplay.NONE)
    fun resetViewAction() = viewAction(HomeViewAction.NONE)

    //
    // Installation
    //

    fun obtainSelectedFilename(): String = installationSession.selectedFilename

    fun onClickInstallOrCancelButton() {
        if (uiState.value.isInstalling) {
            updateDialogState(DialogDisplay.CANCEL_INSTALLATION)
            return
        }
        installationSession.setGsiUserdataSize(uiState.value.userDataCard.content)
        installationSession.setGsiFileSize(uiState.value.imageSizeCard.content)
        updateDialogState(DialogDisplay.CONFIRM_INSTALLATION)
    }

    fun onConfirmInstallationDialog() {
        _uiState.update { it.copy(dialogDisplay = DialogDisplay.NONE, isInstalling = true) }
        installationSession.newJob()
        viewModelScope.launch(Dispatchers.IO + installationSession.job) {
            readBoolPref(UserPreferences.DEBUG_INSTALLATION) {
                installationSession.preferences.isDebugInstallation = it
            }
            readBoolPref(UserPreferences.UMOUNT_SD) {
                installationSession.preferences.isUnmountSdCard = it
            }
            Preparation(
                storageManager = storageAccess,
                session = installationSession,
                onProgressChange = { progress ->
                    updateInstallationCard { it.copy(installationProgress = progress) }
                },
                onInstallationStepChange = { step ->
                    updateInstallationCard { it.copy(installationStep = step) }
                },
                onPreparationSuccess = { sessionReadyToInstall ->
                    onClickCancelInstallationButton()
                    if (sessionReadyToInstall.operationMode == OperationMode.UNROOTED) {
                        adbInstallationHandler()
                        return@Preparation
                    }
                    if (sessionReadyToInstall.preferences.isDebugInstallation) {
                        viewAction(HomeViewAction.NAVIGATE_TO_DIAGNOSE_SCREEN)
                        return@Preparation
                    }
                    InstallationHandler(sessionReadyToInstall).start()
                })
        }
    }

    fun onCancelInstallationDialog() {
        installationSession.reset()
        _uiState.update { it.copy(isInstalling = false) }
        dismissDialog()
    }

    fun resetInstallationCard() =
        _uiState.update {
            it.copy(installationCard = InstallationCardState(), isInstalling = false)
        }

    fun onClickCancelInstallationButton() {
        if (installationSession.isSessionActive())
            installationSession.cancelJob()
        resetInstallationCard()
    }

    private fun adbInstallationHandler() {
        val installationScriptPath = GenerateInstallationScript(
            storageAccess,
            installationSession.preferences,
            installationSession.gsi
        ).writeToFile()
        installationSession.installationScriptFilePath = installationScriptPath
        viewAction(HomeViewAction.NAVIGATE_TO_ADB_SCREEN)
    }

    //
    // Userdata card
    //

    fun onCheckUserdataCard() {
        if (uiState.value.userDataCard.isSelected)
            updateUserdataCard { it.copy(content = "") }
        updateUserdataCard { it.copy(isSelected = it.isSelected.not()) }
    }

    fun updateUserdataSize(input: String) {
        val maxAllocationUserdata = StorageUtils.maximumAllowedAllocation()
        val selectedSize = FilenameUtils.getDigits(input)
        var sizeWithSuffix = FilenameUtils.appendToString(input, "GB")

        if (selectedSize.isNotEmpty() && selectedSize.toInt() > maxAllocationUserdata) {
            sizeWithSuffix = "${maxAllocationUserdata}GB"
            updateUserdataCard { it.copy(isError = true, maximumAllowed = maxAllocationUserdata) }
            viewModelScope.launch(Dispatchers.IO) {
                Thread.sleep(5000)
                updateUserdataCard { it.copy(isError = false) }
            }
        }

        updateUserdataCard { it.copy(content = sizeWithSuffix) }
    }

    //
    // Imagesize card
    //

    fun onCheckImageSizeCard() {
        if (!uiState.value.imageSizeCard.isSelected) {
            updateDialogState(DialogDisplay.IMAGESIZE_WARNING)
        } else {
            dismissDialog()
            updateImageSizeCard { it.copy(content = "") }
        }
        updateImageSizeCard { it.copy(isSelected = it.isSelected.not()) }
    }

    fun updateImageSize(input: String) {
        val inputWithSuffix = FilenameUtils.appendToString(input, "b")
        updateImageSizeCard { it.copy(content = inputWithSuffix) }
    }

    //
    //  About DSU Card
    //

    private fun launchUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.flags += Intent.FLAG_ACTIVITY_NEW_TASK
        i.data = Uri.parse(url)
        application.startActivity(i)
    }

    fun onClickViewDocs() =
        launchUrl("https://source.android.com/devices/tech/ota/dynamic-system-updates")

    fun onClickLearnMore() =
        launchUrl("https://developer.android.com/topic/dsu")

    //
    // File selection
    //

    fun takeUriPermission(uri: Uri) {
        application.contentResolver.takePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        viewModelScope.launch {
            if (storageAccess.arePermissionsGrantedToFolder(uri.toString()))
                DataStoreUtils.updateStringPref(dataStore, CorePreferences.SAF_PATH, uri.toString())
        }
        initialChecks()
    }

    fun onFileSelectionResult(uri: Uri) {
        if (uri == Uri.EMPTY)
            return

        val filename = FilenameUtils.queryName(application.contentResolver, uri)
        if (!FilenameUtils.isFileSupported(filename))
            return onSelectFileError()

        installationSession.selectedFilename = filename
        installationSession.gsi.uri = uri
        updateInstallationCard {
            it.copy(
                content = filename,
                isTextFieldEnabled = false,
                isInstallable = true
            )
        }
    }

    fun onSelectFileError() {
        viewModelScope.launch {
            updateInstallationCard { it.copy(isError = true, isTextFieldEnabled = false) }
            delay(2000)
            updateInstallationCard { it.copy(isError = false, isTextFieldEnabled = true) }
        }
    }

}