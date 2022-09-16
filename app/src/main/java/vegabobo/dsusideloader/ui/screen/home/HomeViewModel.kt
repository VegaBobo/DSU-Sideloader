package vegabobo.dsusideloader.ui.screen.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import vegabobo.dsusideloader.core.BaseViewModel
import vegabobo.dsusideloader.core.StorageManager
import vegabobo.dsusideloader.installer.adb.AdbInstallationHandler
import vegabobo.dsusideloader.installer.privileged.DsuInstallationHandler
import vegabobo.dsusideloader.installer.privileged.LogcatDiagnostic
import vegabobo.dsusideloader.installer.root.RootedInstallationHandler
import vegabobo.dsusideloader.model.DSUInstallation
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.preferences.AppPrefs
import vegabobo.dsusideloader.preparation.InstallationStep
import vegabobo.dsusideloader.preparation.Preparation
import vegabobo.dsusideloader.service.PrivilegedProvider
import vegabobo.dsusideloader.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val application: Application,
    override val dataStore: DataStore<Preferences>,
    private val storageManager: StorageManager,
    var session: Session,
) : BaseViewModel(dataStore) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var checkDynamicPartitions = true
    var checkUnavaiableStorage = true
    var checkReadLogsPermission = true
    var installationJob: Job = Job()

    var logger: LogcatDiagnostic? = null

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

    private fun updateSheetState(sheetDisplay: SheetDisplay) =
        _uiState.update { it.copy(sheetDisplay = sheetDisplay) }

    fun dismissSheet() = updateSheetState(SheetDisplay.NONE)

    //
    // Home startup and checks
    //

    fun initialChecks() {
        updateAdditionalCardState(AdditionalCard.NONE)

        if (checkDynamicPartitions && !VerificationUtils.hasDynamicPartitions()) {
            updateAdditionalCardState(AdditionalCard.NO_DYNAMIC_PARTITIONS)
            return
        }

        if (checkUnavaiableStorage && !StorageUtils.hasAvailableStorage()) {
            updateAdditionalCardState(AdditionalCard.UNAVAIABLE_STORAGE)
            return
        }

        if (session.getOperationMode() == OperationMode.SHIZUKU
            && !OperationModeUtils.isReadLogsPermissionGranted(application)
            && checkReadLogsPermission
        ) {
            updateAdditionalCardState(AdditionalCard.MISSING_READ_LOGS_PERMISSION)
            return
        }

        viewModelScope.launch {
            val result = readStringPref(AppPrefs.SAF_PATH)
            if (!storageManager.arePermissionsGrantedToFolder(result))
                updateAdditionalCardState(AdditionalCard.SETUP_STORAGE)
            else
                _uiState.update { it.copy(canInstall = true) }
        }

        if (session.getOperationMode() == OperationMode.ROOT) {
            viewModelScope.launch(Dispatchers.IO) {
                if (PrivilegedProvider.isRoot() &&
                    PrivilegedProvider.getService().isInstalled
                ) {
                    updateInstallationCard {
                        it.copy(
                            installationStep = InstallationStep.DSU_ALREADY_INSTALLED,
                        )
                    }
                }
            }
        }

    }

    fun setupUserPreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(shouldKeepScreenOn = readBoolPref(AppPrefs.KEEP_SCREEN_ON)) }
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

    //
    // Installation
    //

    fun obtainSelectedFilename(): String = session.userSelection.selectedFileName

    fun onClickCancel() {
        if (uiState.value.isInstalling()) {
            updateSheetState(SheetDisplay.CANCEL_INSTALLATION)
        }
    }

    fun onClickInstall() {
        session.userSelection.setUserDataSize(uiState.value.userDataCard.content)
        session.userSelection.setImageSize(uiState.value.imageSizeCard.content)
        updateSheetState(SheetDisplay.CONFIRM_INSTALLATION)
    }

    fun onConfirmInstallationSheet() {
        _uiState.update { it.copy(sheetDisplay = SheetDisplay.NONE) }
        installationJob = Job()
        viewModelScope.launch(Dispatchers.IO + installationJob) {
            session.preferences.isUnmountSdCard = readBoolPref(AppPrefs.UMOUNT_SD)
            session.preferences.useBuiltinInstaller =
                readBoolPref(AppPrefs.USE_BUILTIN_INSTALLER)
            Preparation(
                storageManager = storageManager,
                session = session,
                job = installationJob,
                onStepUpdate = this@HomeViewModel::onStepUpdate,
                onPreparationProgressUpdate = this@HomeViewModel::onPreparationProgressUpdate,
                onCanceled = this@HomeViewModel::onClickCancelInstallationButton,
                onPreparationFinished = this@HomeViewModel::onPreparationFinished
            ).invoke()
        }
    }

    private fun onPreparationFinished(dsuInstallation: DSUInstallation) {
        session.dsuInstallation = dsuInstallation
        startInstallation()
    }

    private fun startInstallation() {
        updateInstallationCard { it.copy(installationStep = InstallationStep.PROCESSING) }
        if (session.getOperationMode() == OperationMode.UNROOTED) {
            startRootlessInstallation()
            return
        }

        if (session.preferences.useBuiltinInstaller && PrivilegedProvider.isRoot()) {
            startRootInstallation()
            return
        }

        startPrivilegedInstallation()
    }

    private fun startPrivilegedInstallation() {
        updateInstallationCard { it.copy(installationStep = InstallationStep.WAITING_USER_CONFIRMATION) }
        DsuInstallationHandler(session).startInstallation()
        if (OperationModeUtils.isReadLogsPermissionGranted(application)
            || session.getOperationMode() == OperationMode.ROOT
        )
            viewModelScope.launch { startLogging() }
        else
            updateInstallationCard { it.copy(installationStep = InstallationStep.INSTALL_SUCCESS) }
    }

    private fun startRootInstallation() {
        RootedInstallationHandler(
            application = application,
            userdataSize = session.userSelection.userSelectedUserdata,
            dsuInstallation = session.dsuInstallation,
            installationJob = installationJob,
            onInstallationError = this::onInstallationError,
            onInstallationProgressUpdate = this::onInstallationProgressUpdate,
            onCreatePartition = this::onCreatePartition,
            onInstallationStepUpdate = this::onStepUpdate,
            onInstallationSuccess = this::onRootInstallationSuccess
        ).invoke()
    }

    private fun onRootInstallationSuccess() {
        updateInstallationCard { it.copy(installationStep = InstallationStep.INSTALL_SUCCESS_REBOOT_DYN_OS) }
    }

    private fun startRootlessInstallation() {
        AdbInstallationHandler(storageManager, session).generate {
            session.installationScript = it
            resetInstallationCard()
            updateInstallationCard { it.copy(installationStep = InstallationStep.REQUIRES_ADB_CMD_TO_CONTINUE) }
        }
    }

    private fun startLogging() {
        if (logger != null && logger!!.isLogging) {
            logger!!.destroy()
        } else {
            logger = LogcatDiagnostic(
                onInstallationError = this::onInstallationError,
                onStepUpdate = this::onStepUpdate,
                onInstallationProgressUpdate = this::onInstallationProgressUpdate,
                onInstallationSuccess = this::onInstallationSuccess,
                onLogLineReceived = this::onLogLineReceived,
            )
        }
        logger!!.startLogging()
    }

    private fun onLogLineReceived() {
        _uiState.update { it.copy(installationLogs = logger!!.logs) }
    }

    private fun onInstallationSuccess() {
        updateInstallationCard { it.copy(installationStep = InstallationStep.INSTALL_SUCCESS) }
    }

    fun resetInstallationCard() =
        _uiState.update {
            it.copy(
                installationCard = InstallationCardState(),
                sheetDisplay = SheetDisplay.NONE
            )
        }

    fun onClickCancelInstallationButton() {
        if (session.getOperationMode() != OperationMode.UNROOTED && logger != null && logger!!.isLogging) {
            logger!!.destroy()
            logger = null
            // Since stopping installation requires MANAGE_DYNAMIC_SYSTEM
            // then, we stop installation using other way, not so polite, but works :)))
            PrivilegedProvider.getService().forceStopPackage("com.android.dynsystem")
        }

        if (installationJob.isActive)
            installationJob.cancel()
        resetInstallationCard()
        session.dsuInstallation = DSUInstallation()
    }

    //
    // Installation Card actions
    //

    fun onClickRebootToDynOS() {
        PrivilegedProvider.getService().setEnable(true, true)
        Shell.cmd("reboot").submit()
    }

    fun onClickDiscardGsiAndStartInstallation() {
        viewModelScope.launch(Dispatchers.IO + installationJob) {
            PrivilegedProvider.getService().remove()
            startRootInstallation()
        }
    }

    fun onClickDiscardGsi() {
        viewModelScope.launch {
            PrivilegedProvider.getService().remove()
        }
        dismissSheet()
        resetInstallationCard()
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

    fun showDiscardSheet() {
        updateSheetState(SheetDisplay.DISCARD_DSU)
    }

    //
    // Progress tracking
    //

    private fun onStepUpdate(step: InstallationStep) =
        updateInstallationCard {
            it.copy(installationStep = step)
        }

    private fun onPreparationProgressUpdate(progress: Float) =
        updateInstallationCard {
            it.copy(installationProgress = progress)
        }

    private fun onInstallationError(error: InstallationStep, errorContent: String) =
        updateInstallationCard {
            if (error == InstallationStep.ERROR_SELINUX_A10 && !PrivilegedProvider.isRoot()) {
                it.copy(
                    installationStep = InstallationStep.ERROR_SELINUX_A10_ROOTLESS,
                    errorContent = errorContent
                )
            } else {
                it.copy(installationStep = error, errorContent = errorContent)
            }
        }

    private fun onInstallationProgressUpdate(progress: Float, partition: String) =
        updateInstallationCard {
            it.copy(workingPartition = partition, installationProgress = progress)
        }

    private fun onCreatePartition(partition: String) =
        updateInstallationCard {
            it.copy(
                installationStep = InstallationStep.CREATING_PARTITION,
                workingPartition = partition
            )
        }

    //
    // Userdata card
    //

    fun onCheckUserdataCard() {
        updateUserdataCard { it.copy(isSelected = it.isSelected.not(), content = "") }
    }

    fun updateUserdataSize(input: String) {
        val maxAllocationUserdata = StorageUtils.maximumAllowedAllocation()
        val selectedSize = FilenameUtils.getDigits(input)
        val sizeWithSuffix = FilenameUtils.appendToString(input, "GB")

        if (selectedSize.isNotEmpty() && selectedSize.toInt() > maxAllocationUserdata) {
            val fixedSize = FilenameUtils.appendToString("$maxAllocationUserdata", "GB")
            updateUserdataCard {
                it.copy(
                    content = fixedSize,
                    isError = true,
                    maximumAllowed = maxAllocationUserdata,
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                delay(5000)
                updateUserdataCard { it.copy(isError = false) }
            }
            return
        }

        updateUserdataCard { it.copy(content = sizeWithSuffix) }
    }

    //
    // Imagesize card
    //

    fun onCheckImageSizeCard() {
        if (!uiState.value.imageSizeCard.isSelected)
            updateSheetState(SheetDisplay.IMAGESIZE_WARNING)
        else
            dismissSheet()
        updateImageSizeCard { it.copy(isSelected = it.isSelected.not(), content = "") }
    }

    fun updateImageSize(input: String) {
        val inputWithSuffix = FilenameUtils.appendToString(input, "b")
        updateImageSizeCard { it.copy(content = inputWithSuffix) }
    }

    //
    // File selection
    //

    fun takeUriPermission(uri: Uri) {
        application.contentResolver.takePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        viewModelScope.launch {
            if (storageManager.arePermissionsGrantedToFolder(uri.toString()))
                updateStringPref(AppPrefs.SAF_PATH, uri.toString()) { initialChecks() }
        }
    }

    fun onFileSelectionResult(uri: Uri) {
        val filename = FilenameUtils.queryName(application.contentResolver, uri)
        if (!FilenameUtils.isFileSupported(filename)) {
            viewModelScope.launch {
                updateInstallationCard { it.copy(isError = true, isTextFieldEnabled = false) }
                delay(2000)
                updateInstallationCard { it.copy(isError = false, isTextFieldEnabled = true) }
            }
            return
        }

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

    fun grantReadLogs() {
        updateAdditionalCardState(AdditionalCard.GRANTING_READ_LOGS_PERMISSION)
        val intent = Intent()
        intent.setClassName(
            "vegabobo.dsusideloader",
            "vegabobo.dsusideloader.MainActivity"
        )
        intent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
        viewModelScope.launch(Dispatchers.IO) {
            PrivilegedProvider.getService().grantPermission("android.permission.READ_LOGS")
            PrivilegedProvider.getService().startActivity(intent)
        }
    }

    fun refuseReadLogs() {
        checkReadLogsPermission = false
        initialChecks()
    }

    fun toggleLogsView() {
        updateSheetState(SheetDisplay.VIEW_LOGS)
    }

    fun saveLogs(uriToSaveLogs: Uri) {
        storageManager.writeStringToUri(uiState.value.installationLogs, uriToSaveLogs)
    }

}