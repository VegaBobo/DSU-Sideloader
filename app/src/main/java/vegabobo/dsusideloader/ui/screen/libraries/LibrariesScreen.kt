package vegabobo.dsusideloader.ui.screen.libraries

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.DynamicListItem
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.TopBar
import vegabobo.dsusideloader.ui.screen.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrariesScreen(
    navigate: (String) -> Unit,
) {
    val libs = remember { mutableStateOf<Libs?>(null) }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    libs.value = Libs.Builder().withContext(context).build()
    val libraries = libs.value!!.libraries

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)

    ApplicationScreen(
        enableDefaultScrollBehavior = false,
        columnContent = false,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(start = 10.dp, end = 10.dp),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.libraries_title),
                scrollBehavior = scrollBehavior,
                onClickBackButton = { navigate(Destinations.Up) },
            )
        },
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(libraries.size) {
                val thisLibrary = libraries[it]
                val name = thisLibrary.name
                var licenses = ""
                for (license in thisLibrary.licenses) {
                    licenses += license.name
                }
                val urlToOpen = thisLibrary.website ?: ""
                DynamicListItem(listLength = libraries.size - 1, currentValue = it) {
                    PreferenceItem(
                        title = name,
                        description = licenses,
                        onClick = {
                            if (urlToOpen.isNotEmpty()) {
                                uriHandler.openUri(urlToOpen)
                            }
                        },
                    )
                }
            }
            item { Spacer(modifier = Modifier.padding(26.dp)) }
        }
    }
}
