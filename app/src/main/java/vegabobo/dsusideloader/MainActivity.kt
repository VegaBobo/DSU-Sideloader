package vegabobo.dsusideloader

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
import javax.inject.Inject
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import vegabobo.dsusideloader.model.Session
import vegabobo.dsusideloader.service.PrivilegedProvider
import vegabobo.dsusideloader.service.PrivilegedRootService
import vegabobo.dsusideloader.service.PrivilegedService
import vegabobo.dsusideloader.service.PrivilegedSystemService
import vegabobo.dsusideloader.ui.screen.Navigation
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme
import vegabobo.dsusideloader.util.OperationMode
import vegabobo.dsusideloader.util.OperationModeUtils

@AndroidEntryPoint
class MainActivity : ComponentActivity(), Shizuku.OnRequestPermissionResultListener {

    @Inject
    lateinit var session: Session

    private val tag = this.javaClass.simpleName

    private var shouldCheckShizuku = false

    private fun setupSessionOperationMode() {
        val operationMode = OperationModeUtils.getOperationMode(application, shouldCheckShizuku)
        session.setOperationMode(operationMode)
        Log.d(tag, "Operation mode is: $operationMode")
    }

    //
    // Shizuku
    //

    val userServiceArgs =
        Shizuku.UserServiceArgs(
            ComponentName(BuildConfig.APPLICATION_ID, PrivilegedService::class.java.name),
        )
            .daemon(false)
            .processNameSuffix("service")
            .debuggable(BuildConfig.DEBUG)
            .version(BuildConfig.VERSION_CODE)

    private val SHIZUKU_REQUEST_CODE = 1000
    private val REQUEST_PERMISSION_RESULT_LISTENER = this::onRequestPermissionResult

    private fun addShizukuListeners() {
        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEIVED_LISTENER)
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
    }

    private fun removeShizukuListeners() {
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
        Shizuku.removeBinderReceivedListener(BINDER_RECEIVED_LISTENER)
    }

    private val BINDER_RECEIVED_LISTENER = Shizuku.OnBinderReceivedListener {
        if (!OperationModeUtils.isShizukuPermissionGranted(this)) {
            askShizukuPermission()
            return@OnBinderReceivedListener
        }
        bindShizuku()
    }

    private fun askShizukuPermission() {
        if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
            requestPermissions(arrayOf(ShizukuProvider.PERMISSION), SHIZUKU_REQUEST_CODE)
        } else {
            Shizuku.requestPermission(SHIZUKU_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED && requestCode == SHIZUKU_REQUEST_CODE) {
            bindShizuku()
        }
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER)
    }

    fun bindShizuku() {
        Shizuku.bindUserService(userServiceArgs, PrivilegedProvider.connection)
        shouldCheckShizuku = true
        setupSessionOperationMode()
    }

    //
    // Root
    //

    companion object {
        init {
            // Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_REDIRECT_STDERR)
                    .setTimeout(10),
            )
        }
    }

    private fun setupService() {
        if (session.isRoot()) {
            val privRootService = Intent(this, PrivilegedRootService::class.java)
            RootService.bind(privRootService, PrivilegedProvider.connection)
            return
        }

        if (session.getOperationMode() == OperationMode.SYSTEM) {
            val service = Intent(this, PrivilegedSystemService::class.java)
            bindService(service, PrivilegedProvider.connection, Context.BIND_AUTO_CREATE)
            return
        }

        addShizukuListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shell.getShell {}
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DSUHelperTheme {
                Navigation()
            }
        }

        if (savedInstanceState == null) {
            setupSessionOperationMode()
            setupService()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        HiddenApiBypass.addHiddenApiExemptions("")
        super.attachBaseContext(newBase)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isChangingConfigurations) {
            return
        }
        when (session.getOperationMode()) {
            OperationMode.ROOT, OperationMode.SYSTEM_AND_ROOT ->
                RootService.unbind(PrivilegedProvider.connection)

            OperationMode.SYSTEM ->
                applicationContext.unbindService(PrivilegedProvider.connection)

            OperationMode.SHIZUKU -> {
                removeShizukuListeners()
                Shizuku.unbindUserService(userServiceArgs, PrivilegedProvider.connection, true)
            }

            else -> {}
        }
    }
}
