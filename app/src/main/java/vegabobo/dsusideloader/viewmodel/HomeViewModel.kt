package vegabobo.dsusideloader.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vegabobo.dsusideloader.dsuhelper.GSI
import vegabobo.dsusideloader.dsuhelper.PrepareDsu
import vegabobo.dsusideloader.model.*
import vegabobo.dsusideloader.util.FilenameUtils

class HomeViewModel : ViewModel() {

    var fileSelection: ActivityResultLauncher<Intent>? = null

    val userdataCard = mutableStateOf(UserdataCard()).value
    val imageSizeCard = mutableStateOf(ImageSizeCard()).value
    val installationCard = mutableStateOf(InstallationCard()).value
    val installationDialog = mutableStateOf(InstallationDialog()).value

    val gsiInstallation = mutableStateOf(GSI()).value

    val installationProgress = mutableStateOf(InstallationProgress()).value
    val isInstalling = mutableStateOf(false)

    fun onClickSelectFile() {
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
        fileSelection!!.launch(chooseFile)
    }

    fun onTouchToggle(toggle: Int) {
        when (toggle) {
            Toggles.USERDATA -> userdataCard.toggle()
            Toggles.IMGSIZE -> imageSizeCard.toggle()
        }
    }

    fun fileSelectionResult(activity: ComponentActivity) {
        fileSelection = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data!!.data
                gsiInstallation.targetUri = uri!!
                gsiInstallation.name = FilenameUtils.queryName(activity.contentResolver, uri)
                installationCard.lock(
                    gsiInstallation.name
                )
            }
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

    fun onConfirmInstallationDialog(context: Context) {
        installationDialog.toggle()
        isInstalling.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                PrepareDsu(context, gsiInstallation, this@HomeViewModel).run()
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
}