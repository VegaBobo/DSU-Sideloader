package vegabobo.dsusideloader.ui.screen.about

enum class AboutViewAction {
    NO_ACTION,
    NAVIGATE_TO_LIBRARIES
}

enum class UpdateStatus {
    NOT_CHECKED,
    CHECKING_FOR_UPDATES,
    NO_UPDATE_FOUND,
    UPDATE_FOUND
}

data class UpdaterCardState(
    val updateStatus: UpdateStatus = UpdateStatus.NOT_CHECKED,
    val isDownloading: Boolean = false,
    val updateVersion: String = "",
    val progressBar: Float = -1F
)

data class AboutScreenUiState(
    val updaterCardState: UpdaterCardState = UpdaterCardState()
)