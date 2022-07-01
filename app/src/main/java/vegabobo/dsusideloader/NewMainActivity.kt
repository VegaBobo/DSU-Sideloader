package vegabobo.dsusideloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import vegabobo.dsusideloader.ui.Navigation
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme
import vegabobo.dsusideloader.util.SetupStorageAccess
import vegabobo.dsusideloader.viewmodel.HomeViewModel

class NewMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val homeViewModel = HomeViewModel()
        homeViewModel.fileSelectionResult(this)

        setContent {
            DSUHelperTheme {
                Navigation(homeViewModel)
            }
        }

        SetupStorageAccess(this@NewMainActivity)

    }
}