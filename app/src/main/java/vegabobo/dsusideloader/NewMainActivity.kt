package vegabobo.dsusideloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import vegabobo.dsusideloader.ui.Navigation
import vegabobo.dsusideloader.ui.screens.Home
import vegabobo.dsusideloader.ui.theme.DSUHelperTheme

class NewMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DSUHelperTheme {
                Navigation()
            }
        }
    }
}