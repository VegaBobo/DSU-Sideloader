package vegabobo.dsusideloader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.checks.CompatibilityCheck
import vegabobo.dsusideloader.ui.Navigation
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme
import vegabobo.dsusideloader.util.SetupStorageAccess
import vegabobo.dsusideloader.util.StorageUtils
import vegabobo.dsusideloader.viewmodel.HomeViewModel

class NewMainActivity : ComponentActivity() {

    companion object {
        init {
            Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_REDIRECT_STDERR)
                    .setTimeout(10)
            )
        }
    }

    object Action {
        const val FINISH_APP = 0
        const val SETUP_FILE_ACCESS = 1
        const val OPEN_FILE_SELECTION = 2
        const val INSTALL_GSI = 3
    }

    private val hVm: HomeViewModel by viewModels()

    private lateinit var fileSelection: ActivityResultLauncher<Intent>
    private lateinit var setupStorageAccess: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shell.getShell {}
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupStorageAccess()
        fileSelectionResult()

        hVm.deviceSupport.hasSetupStorageAccess.value =
            SetupStorageAccess.arePermissionsGranted(this@NewMainActivity)
        hVm.deviceSupport.hasDynamicPartitions.value = CompatibilityCheck.checkDynamicPartitions()
        hVm.deviceSupport.hasFreeStorage.value = StorageUtils.hasAvailableStorage()

        setContent {
            DSUHelperTheme {
                Navigation(hVm)
            }
        }

        lifecycleScope.launch {
            hVm.activityAction.collect {
                when (it) {
                    Action.FINISH_APP ->
                        hVm.finishApp(this@NewMainActivity)
                    Action.SETUP_FILE_ACCESS ->
                        hVm.setupStorage(setupStorageAccess)
                    Action.OPEN_FILE_SELECTION ->
                        hVm.onClickSelectFile(fileSelection)
                    Action.INSTALL_GSI ->
                        hVm.onConfirmInstallationDialog(this@NewMainActivity)
                }
                hVm.activityAction.value = -1
            }
        }

    }

    private fun setupStorageAccess() {
        setupStorageAccess =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK)
                    hVm.onSetupStorageResult(result.data!!, this)
            }
    }

    private fun fileSelectionResult() {
        fileSelection = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                hVm.onFileSelectionResult(result.data!!.data!!, this)
        }
    }

    private fun startInstallation() {
        hVm.onConfirmInstallationDialog(this)
    }

}