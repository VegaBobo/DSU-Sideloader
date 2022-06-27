package vegabobo.dsusideloader.viewmodel

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import vegabobo.dsusideloader.*
import vegabobo.dsusideloader.dsuhelper.GsiDsuObject
import vegabobo.dsusideloader.util.FilenameUtils

class HomeViewModel : ViewModel() {

    var fileSelection: ActivityResultLauncher<Intent>? = null

    val userdataCard = mutableStateOf(UserdataCard()).value
    val imageSizeCard = mutableStateOf(ImageSizeCard()).value
    val installationCard = mutableStateOf(InstallationCard()).value
    val installationDialog = mutableStateOf(InstallationDialog()).value

    val gsiInstallation = mutableStateOf(GsiDsuObject()).value

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
                val z = GsiDsuObject()
                z.targetUri = uri!!
                installationCard.lock(
                    FilenameUtils.queryName(activity.contentResolver, uri)
                )
            }
        }
    }

    fun onClickInstall() {
        if (userdataCard.isEnabled() && userdataCard.isTextNotEmpty())
            gsiInstallation.setUserdataSize(userdataCard.getDigits())
        else
            gsiInstallation.userdataSize = GsiDsuObject.Constants.DEFAULT_USERDATA_SIZE_IN_GB

        if (imageSizeCard.isEnabled() && imageSizeCard.isTextNotEmpty())
            gsiInstallation.setFileSize(imageSizeCard.getDigits())
        else
            gsiInstallation.fileSize = GsiDsuObject.Constants.DEFAULT_FILE_SIZE

        installationDialog.toggle()
    }

    fun onClickClear() {
        installationCard.clear()
    }

    fun onConfirmDialog() {
    }

    fun onCancelDialog() {
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