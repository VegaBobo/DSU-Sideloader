package vegabobo.dsusideloader

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnBinderReceivedListener
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener
import rikka.shizuku.ShizukuProvider
import vegabobo.dsusideloader.core.InstallationSession
import vegabobo.dsusideloader.ui.screen.Navigation
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme
import vegabobo.dsusideloader.util.OperationModeUtils
import javax.inject.Inject


object ActivityAction {
    const val FINISH_APP = 1
}

@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnRequestPermissionResultListener {

    @Inject
    lateinit var installationSession: InstallationSession

    private val SHIZUKU_REQUEST_CODE = 1000

    private val REQUEST_PERMISSION_RESULT_LISTENER:
            OnRequestPermissionResultListener =
        OnRequestPermissionResultListener(this::onRequestPermissionResult)

    override fun onDestroy() {
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
    }

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

    private fun setupOperationMode(checkShizuku: Boolean) {
        val operationMode = OperationModeUtils.getOperationMode(application, checkShizuku)
        installationSession.operationMode = operationMode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shell.getShell {}
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val activityRequest: (Int) -> Unit = {
            when (it) {
                ActivityAction.FINISH_APP -> this.finishAffinity()
            }
        }

        setContent {
            DSUHelperTheme {
                Navigation(activityRequest)
            }
        }

        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEIVED_LISTENER)
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
        setupOperationMode(false)
    }

    private val BINDER_RECEIVED_LISTENER = OnBinderReceivedListener {
        Log.i(BuildConfig.APPLICATION_ID, "Binder received")
        if (Shell.getShell().isRoot) return@OnBinderReceivedListener
        if (!OperationModeUtils.isShizukuPermissionGranted(this))
            askShizukuPermission()
        else
            setupOperationMode(true)
    }

    fun askShizukuPermission() {
        if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
            requestPermissions(arrayOf(ShizukuProvider.PERMISSION), SHIZUKU_REQUEST_CODE)
        } else {
            Shizuku.requestPermission(SHIZUKU_REQUEST_CODE)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        HiddenApiBypass.addHiddenApiExemptions("L")
        super.attachBaseContext(newBase)
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED && requestCode == SHIZUKU_REQUEST_CODE)
            setupOperationMode(true)
    }

}