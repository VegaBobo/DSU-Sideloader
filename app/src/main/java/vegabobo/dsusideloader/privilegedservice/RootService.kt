package vegabobo.dsusideloader.privilegedservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.topjohnwu.superuser.ipc.RootService

class RootService : RootService() {
    override fun onBind(intent: Intent): IBinder {
        return PrivilegedService()
    }
}

class SystemService : Service() {
    override fun onBind(intent: Intent): IBinder {
        return PrivilegedService()
    }
}