package vegabobo.dsusideloader.privapi

import vegabobo.dsusideloader.IPrivilegedService

object PrivilegedProvider {

    var connection: Connection? = Connection()

    fun getService(): IPrivilegedService {
        while(this.connection!!.SERVICE == null){}
        return this.connection!!.SERVICE!!
    }

    fun isRoot(): Boolean {
        if (isConnected())
            return this.connection!!.SERVICE!!.uid == 0
        return false
    }

    private fun isConnected(): Boolean {
        return this.connection != null
    }

    fun set(connection: Connection) {
        if (this.connection == null)
            this.connection = connection
    }

}