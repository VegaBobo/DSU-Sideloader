package vegabobo.dsusideloader.ui.screen.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.core.StorageManager
import vegabobo.dsusideloader.installation.InstallationHandler
import vegabobo.dsusideloader.preferences.CorePreferences
import vegabobo.dsusideloader.preferences.UserPreferences
import vegabobo.dsusideloader.preparation.Preparation
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.logging.LogcatDiagnostic
import vegabobo.dsusideloader.model.DSUInstallation
import vegabobo.dsusideloader.preparation.InstallationSteps
import vegabobo.dsusideloader.privapi.PrivilegedProvider
import vegabobo.dsusideloader.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val application: Application,
    override val dataStore: DataStore<Preferences>,
    val storageAccess: StorageManager,
    var session: Session,
) : BaseViewModel(dataStore) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    val homeViewAction = MutableStateFlow(HomeViewAction.NONE)

    var checkDynamicPartitions = true
    var checkUnavaiableStorage = true
    var installationJob: Job = Job()

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
                updateAdditionalCardState(AdditionalCard.SETUP_STORAGE)
            else
                _uiState.update { it.copy(canInstall = true) }
        }
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

    fun obtainSelectedFilename(): String = session.userSelection.selectedFileName

    fun onClickCancel() {
        if (uiState.value.isInstalling) {
            updateDialogState(DialogDisplay.CANCEL_INSTALLATION)
            return
        }
    }

    fun onClickInstall() {
        session.userSelection.setUserDataSize(uiState.value.userDataCard.content)
        session.userSelection.setImageSize(uiState.value.imageSizeCard.content)
        updateDialogState(DialogDisplay.CONFIRM_INSTALLATION)
    }

    fun onConfirmInstallationDialog() {
        _uiState.update { it.copy(dialogDisplay = DialogDisplay.NONE, isInstalling = true) }
        installationJob = Job()
        viewModelScope.launch(Dispatchers.IO + installationJob) {
            readBoolPref(UserPreferences.UMOUNT_SD) {
                session.preferences.isUnmountSdCard = it
            }
            updateInstallationCard { it.copy(isShowingProgressBar = true) }
            object : Preparation(
                storageAccess,
                session.userSelection.selectedFileUri,
                session.userSelection.userSelectedImageSize,
                installationJob,
            ) {
                override fun onInstallationStepChange(step: InstallationSteps) {
                    if (step == InstallationSteps.WAITING_USER_CONFIRMATION)
                        updateInstallationCard { it.copy(isShowingProgressBar = false) }
                    updateInstallationCard { it.copy(installationStep = step) }
                }

                override fun onProgressChange(progress: Float) {
                    updateInstallationCard { it.copy(installationProgress = progress) }
                }

                override fun onPreparationSuccess(preparedDSUInstallation: DSUInstallation) {
                    session.dsuInstallation = preparedDSUInstallation
                    startInstallation()
                    if (session.operationMode != OperationMode.UNROOTED)
                        startLogging()
                }
            }
        }
    }

    fun startInstallation() = InstallationHandler(
        session,
        storageAccess,
        this@HomeViewModel::onRootlessAdbScriptGenerated
    ).invoke()

    fun onRootlessAdbScriptGenerated(scriptPath: String) {
        session.installationScript = scriptPath
        resetInstallationCard()
        viewAction(HomeViewAction.NAVIGATE_TO_ADB_SCREEN)
    }

    private fun startLogging() {
        _uiState.update { it.copy(isLogging = true) }
        logger.startLogging()
        session.logger = logger
    }

    fun onCancelInstallationDialog() {
        _uiState.update { it.copy(isInstalling = false) }
        dismissDialog()
    }

    fun resetInstallationCard() =
        _uiState.update {
            it.copy(
                installationCard = InstallationCardState(),
                isInstalling = false,
                dialogDisplay = DialogDisplay.NONE
            )
        }

    fun onClickCancelInstallationButton() {
        if (session.operationMode != OperationMode.UNROOTED && logger.isLogging) {
            logger.destroy()
            session.logger = null
            // Since stopping installation requires MANAGE_DYNAMIC_SYSTEM
            // then, we stop installation using other way, not so polite, but works :)))
            PrivilegedProvider.getService().forceStopPackage("com.android.dynsystem")
        }

        if (installationJob.isActive)
            installationJob.cancel()
        resetInstallationCard()
        session.dsuInstallation = DSUInstallation()
        _uiState.update { it.copy(isLogging = false) }
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
        val sizeWithSuffix = FilenameUtils.appendToString(input, "GB")

        if (selectedSize.isNotEmpty() && selectedSize.toInt() > maxAllocationUserdata) {
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
        if (uri == Uri.EMPTY)
            return
        application.contentResolver.takePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        viewModelScope.launch {
            if (storageAccess.arePermissionsGrantedToFolder(uri.toString()))
                updateStringPref(CorePreferences.SAF_PATH, uri.toString()) { initialChecks() }
        }
    }

    fun onFileSelectionResult(uri: Uri) {
        if (uri == Uri.EMPTY)
            return

        val filename = FilenameUtils.queryName(application.contentResolver, uri)
        if (!FilenameUtils.isFileSupported(filename))
            return onSelectFileError()

        session.userSelection.selectedFileName = filename
        session.userSelection.selectedFileUri = uri
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

    fun onClickRebootToDynOS() {
        TODO("Not yet implemented")
    }

    fun onClickDiscardGsi() {
        TODO("Not yet implemented")
    }

    fun onClickRetryInstallation() {
        viewModelScope.launch(Dispatchers.IO + installationJob) {
            startInstallation()
            startLogging()
        }
    }

    fun onClickUnmountSdCardAndRetry() {
        session.preferences.isUnmountSdCard = true
        startInstallation()
    }

    fun onClickSetSeLinuxPermissive() {
        viewModelScope.launch(Dispatchers.IO + installationJob) {
            Shell.cmd("setenforce 0").submit()
            startInstallation()
            startLogging()
        }
    }

    fun onCheckLogsCard() {
//        updateLogsCardState { it.copy(isSelected = it.isSelected.not()) }
    }

    fun onClickViewLogs() {
        viewAction(HomeViewAction.NAVIGATE_TO_LOGCAT_SCREEN)
    }

    val logger = object : LogcatDiagnostic() {
        override fun onErrorDetected(error: InstallationSteps, errorLine: String) {
            super.onErrorDetected(error, errorLine)

            updateInstallationCard {
                it.copy(
                    isShowingProgressBar = false,
                    installationStep = error
                )
            }

            if (error == InstallationSteps.ERROR_SELINUX_A10 && !PrivilegedProvider.isRoot()) {
                updateInstallationCard { it.copy(installationStep = InstallationSteps.ERROR_SELINUX_A10_ROOTLESS) }
            }
        }

        override fun onProcessUpdate(float: Float, partition: String) {
            updateInstallationCard {
                it.copy(
                    workingPartition = partition,
                    installationStep = InstallationSteps.INSTALLING,
                    installationProgress = float,
                    isShowingProgressBar = true,
                )
            }
        }

        override fun onSuccess() {
            super.onSuccess()
            updateInstallationCard {
                it.copy(
                    installationStep = InstallationSteps.DONE,
                    isShowingProgressBar = false
                )
            }
        }
    }

}