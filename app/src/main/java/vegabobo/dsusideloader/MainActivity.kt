package vegabobo.dsusideloader

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import dagger.hilt.android.AndroidEntryPoint
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import vegabobo.dsusideloader.core.InstallationSession
import vegabobo.dsusideloader.privilegedservice.*
import vegabobo.dsusideloader.ui.screen.Navigation
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.util.OperationModeUtils
import javax.inject.Inject

object ActivityAction {
    const val FINISH_APP = 1
}

@AndroidEntryPoint
class MainActivity : ComponentActivity(), Shizuku.OnRequestPermissionResultListener {

    @Inject
    lateinit var installationSession: InstallationSession

    //
    // Shizuku
    //

    val userServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            PrivilegedService::class.java.name
        )
    ).daemon(false).processNameSuffix("service").debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)

    private val SHIZUKU_REQUEST_CODE = 1000
    private val REQUEST_PERMISSION_RESULT_LISTENER =
        Shizuku.OnRequestPermissionResultListener(this::onRequestPermissionResult)

    fun addShizukuListeners() {
        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEIVED_LISTENER)
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
    }

    fun removeShizukuListeners() {
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
        Shizuku.removeBinderReceivedListener(BINDER_RECEIVED_LISTENER)
    }

    private fun setupOperationMode(checkShizuku: Boolean) {
        val operationMode = OperationModeUtils.getOperationMode(application, checkShizuku)
        installationSession.operationMode = operationMode
    }

    private val BINDER_RECEIVED_LISTENER = Shizuku.OnBinderReceivedListener {
        Log.i(BuildConfig.APPLICATION_ID, "Binder received")
        if (Shell.getShell().isRoot) return@OnBinderReceivedListener
        if (!OperationModeUtils.isShizukuPermissionGranted(this)) {
            askShizukuPermission()
            return@OnBinderReceivedListener
        }
        setupOperationMode(true)

        Shizuku.bindUserService(userServiceArgs, PrivilegedServiceProvider.connection!!)
    }

    fun askShizukuPermission() {
        if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
            requestPermissions(arrayOf(ShizukuProvider.PERMISSION), SHIZUKU_REQUEST_CODE)
        } else {
            Shizuku.requestPermission(SHIZUKU_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED && requestCode == SHIZUKU_REQUEST_CODE)
            setupOperationMode(true)
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
    }

    //
    // Root
    //

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

    private fun setupRootAIDL() {
        val e = Intent(this, PrivilegedRootService::class.java)
        RootService.bind(e, PrivilegedServiceProvider.connection!!)
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

        if (Shell.getShell().isRoot)
            setupRootAIDL()

        if (savedInstanceState == null) {
            addShizukuListeners()
            setupOperationMode(false)
        }

        if (installationSession.operationMode == OperationMode.SYSTEM) {
            val service = Intent(this, SystemService::class.java)
            bindService(service, PrivilegedServiceProvider.connection!!, Context.BIND_AUTO_CREATE)
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        HiddenApiBypass.addHiddenApiExemptions("")
        super.attachBaseContext(newBase)
    }

    override fun onDestroy() {
        removeShizukuListeners()
        if (PrivilegedServiceProvider.connection != null)
            when (installationSession.operationMode) {
                OperationMode.ROOT -> RootService.unbind(PrivilegedServiceProvider.connection!!)
                OperationMode.SYSTEM -> unbindService(PrivilegedServiceProvider.connection!!)
                OperationMode.SHIZUKU -> {
                    PrivilegedServiceProvider.getService().exit()
                    Shizuku.unbindUserService(userServiceArgs, PrivilegedServiceProvider.connection!!, true)
                }
                else -> {}
            }
        super.onDestroy()
    }

}