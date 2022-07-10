package vegabobo.dsusideloader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.checks.CompatibilityCheck
import vegabobo.dsusideloader.ui.Navigation
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme
import vegabobo.dsusideloader.util.DataStoreUtils
import vegabobo.dsusideloader.util.SetupStorageAccess
import vegabobo.dsusideloader.util.StorageUtils
import vegabobo.dsusideloader.viewmodel.HomeViewModel
import vegabobo.dsusideloader.viewmodel.Preference
import vegabobo.dsusideloader.viewmodel.SettingsViewModel
import java.util.*
import javax.inject.Inject

object ActivityAction {
    const val NONE = -1
    const val FINISH_APP = 0
    const val SETUP_FILE_ACCESS = 1
    const val OPEN_FILE_SELECTION = 2
    const val INSTALL_GSI = 3
}

@AndroidEntryPoint
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

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    private val hVm: HomeViewModel by viewModels()
    private val sVm: SettingsViewModel by viewModels()

    private lateinit var fileSelection: ActivityResultLauncher<Intent>
    private lateinit var setupStorageAccess: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shell.getShell {}
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setupStorageAccess()
        fileSelectionResult()

        hVm.showSetupStorageCard(hasNotGrantedStorageAccess())
        hVm.showNoDynamicPartitionsCard(hasNotDynamicPartitions())
        hVm.showNoAvailStorageCard(hasNotAvailableStorage())

        setContent {
            DSUHelperTheme {
                Navigation(hVm, sVm)
            }
        }

        lifecycleScope.launch {
            hVm.activityAction.collect {
                when (it) {
                    ActivityAction.FINISH_APP ->
                        finishActivity(0)
                    ActivityAction.SETUP_FILE_ACCESS ->
                        hVm.onSetupStorageResult(setupStorageAccess)
                    ActivityAction.OPEN_FILE_SELECTION ->
                        hVm.onSelectFileResult(fileSelection)
                    ActivityAction.INSTALL_GSI ->
                        hVm.onConfirmInstallationAction(applicationContext)
                }
                hVm.activityAction.value = ActivityAction.NONE
            }
        }
    }

    private fun setupStorageAccess() {
        setupStorageAccess =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK)
                    hVm.onSetupStorageSuccessfully(result.data!!, this)
            }
    }

    private fun fileSelectionResult() {
        fileSelection = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                hVm.onSelectFileSuccessfully(result.data!!.data!!, this)
        }
    }

    private fun hasNotGrantedStorageAccess(): Boolean {
        return !SetupStorageAccess.hasGrantedStorage(this@NewMainActivity)
    }

    private fun hasNotDynamicPartitions(): Boolean {
        return !CompatibilityCheck.hasDynamicPartitions()
    }

    private fun hasNotAvailableStorage(): Boolean {
        return !StorageUtils.hasAvailableStorage()
    }

}