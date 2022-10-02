package vegabobo.dsusideloader.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import vegabobo.dsusideloader.IPrivilegedService

class Connection : ServiceConnection {

    var SERVICE: IPrivilegedService? = null
    fun set(service: IPrivilegedService?) {
        if (SERVICE == null) {
            SERVICE = service
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        set(IPrivilegedService.Stub.asInterface(service))
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        SERVICE = null
    }
}
