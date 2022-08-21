package vegabobo.dsusideloader.service

import vegabobo.dsusideloader.IPrivilegedService

object PrivilegedProvider {

    var connection = Connection()

    fun getService(): IPrivilegedService {
        while (!isConnected()) {
            Thread.sleep(1000)
        }
        return this.connection.SERVICE!!
    }

    fun isRoot(): Boolean {
        return this.getService().uid == 0
    }

    fun isConnected(): Boolean {
        return this.connection.SERVICE != null
    }

}