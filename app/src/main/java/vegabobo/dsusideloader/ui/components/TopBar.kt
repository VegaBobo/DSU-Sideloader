package vegabobo.dsusideloader.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    barTitle: String,
    icon: ImageVector? = null,
    iconContentDescription: String? = "Unknown",
    onClickIcon: () -> Unit = {},
) {
    LargeTopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.background),
        title = { Text(barTitle) },
        actions = {
            if (icon != null) {
                IconButton(onClickIcon) {
                    Icon(
                        imageVector = icon,
                        contentDescription = iconContentDescription
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}