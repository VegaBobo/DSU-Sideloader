package vegabobo.dsusideloader

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import vegabobo.dsusideloader.ui.Navigation
import vegabobo.dsusideloader.ui.screens.home.HomeViewModel
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme

object ActivityAction {
    const val FINISH_APP = 1
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
    }
}