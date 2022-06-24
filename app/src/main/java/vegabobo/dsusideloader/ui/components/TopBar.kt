package vegabobo.dsusideloader.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun TopBar(
    title: String,
    icon: ImageVector? = null,
    contentDescription: String? = "Unknown",
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onClickIcon: () -> Unit = {},
) {
    LargeTopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.background),
        title = { Text(title) },
        actions = {
            if (icon != null) {
                IconButton(onClickIcon) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}