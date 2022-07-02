package vegabobo.dsusideloader.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vegabobo.dsusideloader.NewMainActivity
import vegabobo.dsusideloader.dsuhelper.GSI
import vegabobo.dsusideloader.dsuhelper.PrepareDsu
import vegabobo.dsusideloader.model.*
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.SPUtils

class HomeViewModel : ViewModel() {

    val activityAction: MutableStateFlow<Int> = MutableStateFlow(-1)

    val userdataCard = mutableStateOf(UserdataCard()).value
    val imageSizeCard = mutableStateOf(ImageSizeCard()).value
    val installationCard = mutableStateOf(InstallationCard()).value
    val installationDialog = mutableStateOf(InstallationDialog()).value

    val deviceSupport = mutableStateOf(DeviceSupport()).value

    val gsiInstallation = mutableStateOf(GSI()).value

    val installationProgress = mutableStateOf(InstallationProgress()).value
    val isInstalling = mutableStateOf(false)

    fun onTouchToggle(toggle: Int) {
        when (toggle) {
            Toggles.USERDATA -> userdataCard.toggle()
            Toggles.IMGSIZE -> imageSizeCard.toggle()
        }
    }

    fun onClickInstall() {
        if (userdataCard.isEnabled() && userdataCard.isTextNotEmpty())
            gsiInstallation.setUserdataSize(userdataCard.getDigits())
        else
            gsiInstallation.userdataSize = GSI.Constants.DEFAULT_USERDATA_SIZE_IN_GB

        if (imageSizeCard.isEnabled() && imageSizeCard.isTextNotEmpty())
            gsiInstallation.setFileSize(imageSizeCard.getDigits())
        else
            gsiInstallation.fileSize = GSI.Constants.DEFAULT_FILE_SIZE

        installationDialog.toggle()
    }

    fun onClickClear() {
        installationCard.clear()
    }

    fun onConfirmInstallationDialogAction(){
        activityAction.value = NewMainActivity.Action.INSTALL_GSI
    }

    fun onConfirmInstallationDialog(activity: NewMainActivity) {
        installationDialog.toggle()
        isInstalling.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                PrepareDsu(activity, gsiInstallation, this@HomeViewModel).run()
                onClickClear()
            }
        }

    }

    fun onCancelInstallationDialog() {
        installationDialog.toggle()
    }

    fun updateUserdataSize(input: String) {
        val inputWithSuffix = userdataCard.addSuffix(input, "GB")
        userdataCard.setText(inputWithSuffix)
    }

    fun updateImageSize(input: String) {
        val inputWithSuffix = imageSizeCard.addSuffix(input, "b")
        imageSizeCard.setText(inputWithSuffix)
    }

    fun onSetupStorageResult(data: Intent, activity: NewMainActivity) {
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        activity.contentResolver.takePersistableUriPermission(
            data.data!!,
            takeFlags
        )
        SPUtils.writeSafRwPath(activity, data.data.toString())
        deviceSupport.hasSetupStorageAccess.value = true
    }

    fun setupStorage(arl: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        arl.launch(intent)
    }

    fun setupStorageAction(){
        activityAction.value = NewMainActivity.Action.SETUP_FILE_ACCESS
    }

    fun onClickSelectFileAction(){
        activityAction.value = NewMainActivity.Action.OPEN_FILE_SELECTION
    }

    fun onClickSelectFile(arl: ActivityResultLauncher<Intent>) {
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

    fun finishApp(newMainActivity: NewMainActivity) {
        newMainActivity.finishAffinity()
    }

    fun actionFinishApp(){
        activityAction.value = NewMainActivity.Action.FINISH_APP
    }

    fun onFileSelectionResult(uri: Uri, activity: NewMainActivity) {
        gsiInstallation.targetUri = uri
        gsiInstallation.name = FilenameUtils.queryName(activity.contentResolver, uri)
        installationCard.lock(
            gsiInstallation.name
        )
    }
}