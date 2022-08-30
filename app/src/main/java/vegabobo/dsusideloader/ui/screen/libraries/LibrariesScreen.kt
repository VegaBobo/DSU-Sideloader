package vegabobo.dsusideloader.ui.screen.libraries

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.ui.components.ApplicationScreen
import vegabobo.dsusideloader.ui.components.PreferenceItem
import vegabobo.dsusideloader.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrariesScreen(
    navController: NavController,
) {
    val libs = remember { mutableStateOf<Libs?>(null) }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    libs.value = Libs.Builder().withContext(context).build()
    val libraries = libs.value!!.libraries

    val appBarState = rememberTopAppBarScrollState()
    val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState) }

    ApplicationScreen(
        enableDefaultScrollBehavior = false,
        columnContent = false,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                barTitle = stringResource(id = R.string.libraries),
                showBackButton = true,
                scrollBehavior = scrollBehavior,
                onClickBackButton = { navController.navigateUp() })
        }
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(libraries.size) {
                val thisLibrary = libraries[it]
                val name = thisLibrary.name
                var licenses = ""
                for (license in thisLibrary.licenses) {
                    licenses += license.name
                }
                val urlToOpen = thisLibrary.website ?: ""
                PreferenceItem(
                    title = name,
                    description = licenses,
                    onClick = {
                        if(urlToOpen.isNotEmpty())
                            uriHandler.openUri(urlToOpen)
                    }
                )
            }
        }
    }
}