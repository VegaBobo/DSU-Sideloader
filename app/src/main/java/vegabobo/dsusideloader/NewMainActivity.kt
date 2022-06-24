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

class NewMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val insets = WindowInsets
                .systemBars
                .only(WindowInsetsSides.Vertical)
                .asPaddingValues()

            DSUHelperTheme {
                Surface(modifier = Modifier.padding(insets)) {
                    Navigation()
                }
            }

        }
    }
}