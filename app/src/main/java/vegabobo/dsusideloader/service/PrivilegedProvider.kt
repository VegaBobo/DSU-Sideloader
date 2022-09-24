package vegabobo.dsusideloader.service

import kotlinx.coroutines.*
import vegabobo.dsusideloader.IPrivilegedService

object PrivilegedProvider {

    var connection = Connection()

    fun run(
        onFail: () -> Unit = { println("Cannot obtain service connection.") },
        onConnected: suspend IPrivilegedService. () -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            var timeout = 0
            while (!isConnected()) {
                timeout += 1000
                if (timeout > 10000) {
                    onFail()
                }
                delay(1000)
            }
            onConnected(this@PrivilegedProvider.connection.SERVICE!!)
        }
    }

    // Blocking
    fun getService(): IPrivilegedService {
        var timeout = 0
        while (!isConnected()) {
            timeout += 1000
            if (timeout > 10000) {
                throw Exception("Cannot obtain service connection.")
            }
            Thread.sleep(1000)
        }
        return this.connection.SERVICE!!
    }

    // Blocking
    fun isRoot(): Boolean {
        return this.getService().uid == 0
    }

    fun isConnected(): Boolean {
        return this.connection.SERVICE != null
    }

}