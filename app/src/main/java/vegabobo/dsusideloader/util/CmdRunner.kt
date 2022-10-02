package vegabobo.dsusideloader.util

import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.Shell
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object CmdRunner {

    var process: Process? = null

    fun run(cmd: String): String {
        return if (Shell.getShell().isRoot) {
            Shell.cmd(cmd).exec().out.toString()
        } else {
            runCommand(cmd)
        }
    }

    fun runReadEachLine(cmd: String, onReceive: (String) -> Unit) {
        if (Shell.getShell().isRoot) {
            val callbackList: CallbackList<String> = object : CallbackList<String>() {
                override fun onAddElement(s: String) {
                    onReceive(s)
                }
            }
            Shell.cmd(cmd).to(callbackList).submit()
        } else {
            runCommand(cmd, onReceive)
        }
    }

    private fun runCommand(cmd: String, onReceive: (String) -> Unit) {
        process = ProcessBuilder("/bin/sh", "-c", cmd).start()
        val bufferedReader = BufferedReader(InputStreamReader(process!!.inputStream))
        try {
            var line: String
            while (bufferedReader.readLine().also { line = it ?: "" } != null) {
                if (line.isNotEmpty()) onReceive(line)
            }
        } catch (_: IOException) {
        }
    }

    private fun runCommand(cmd: String): String {
        var output = ""
        runCommand(cmd) {
            output += "$it\n"
        }
        return output
    }

    fun destroy() {
        if (Shell.getShell().isRoot) {
            Shell.getShell().close()
            Shell.getShell()
            return
        }
        if (process != null) {
            process!!.destroy()
            process = null
        }
    }
}
