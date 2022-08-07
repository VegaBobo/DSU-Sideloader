package vegabobo.dsusideloader.privilegedservice

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import vegabobo.dsusideloader.IPrivilegedService

object PrivilegedServiceProvider {

    var connection: AIDLConnection? = null

    fun getService(): IPrivilegedService {
        return this.connection!!.SERVICE!!
    }

    fun set(connection: AIDLConnection) {
        if (this.connection == null)
            this.connection = connection
    }

    init {
        set(AIDLConnection())
    }

}

class AIDLConnection : ServiceConnection {

    var SERVICE: IPrivilegedService? = null
    fun set(service: IPrivilegedService?) {
        if (SERVICE == null)
            SERVICE = service
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        set(IPrivilegedService.Stub.asInterface(service))
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        SERVICE = null
    }

}