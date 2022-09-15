package vegabobo.dsusideloader.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogLikeBottomSheet(
    title: String,
    icon: ImageVector,
    onClose: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )

    // Initial state of BottomSheet is "Hidden"
    // * we can change it to "Expanded", however, animation would be lost.
    // This workaround detects if BottomSheet is being called by its first time
    // if so, then, we call "show()" (Hidden => Expanded)
    // after that, if state changes again to Hidden, it means user dismissed our sheet
    // then we call "onClose()", that may do the job to make us gone away.
    val isFirst = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }
            .collect {
                if (it == ModalBottomSheetValue.Hidden) {
                    if (isFirst.value) {
                        sheetState.show()
                        isFirst.value = false
                    } else {
                        onClose()
                    }
                }
            }
    }

    val coroutineScope = rememberCoroutineScope()
    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            BottomSheet(
                title = title,
                icon = icon
            ) {
                Column(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    content()
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
    ) {}

}