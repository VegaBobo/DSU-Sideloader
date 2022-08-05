package vegabobo.dsusideloader.installation

import vegabobo.dsusideloader.model.GSI

class InstallationCmdline(
    val gsi: GSI
) {

    fun getCmd(): String {
        return "am start-activity " +
                "-n com.android.dynsystem/com.android.dynsystem.VerificationActivity " +
                "-a android.os.image.action.START_INSTALL " +
                "" + genInstallationArguments()
    }

    private fun genInstallationArguments(): String {
        val gsiFileAbsolutePath = gsi.absolutePath
        val userdataSize = gsi.userdataSize
        val imageFileSize = gsi.fileSize

        var arguments = ""

        arguments += addArgument("-d", gsiFileAbsolutePath)
        arguments += addArgument("--el", "KEY_USERDATA_SIZE", userdataSize)
        if (imageFileSize != -1L)
            arguments += addArgument("--el", "KEY_SYSTEM_SIZE", imageFileSize)

        return arguments.trim()
    }

    private fun addArgument(argument: String, property: String, value: Any?): String {
        return "$argument $property $value "
    }

    private fun addArgument(argument: String, value: Any?): String {
        return "$argument $value "
    }

}