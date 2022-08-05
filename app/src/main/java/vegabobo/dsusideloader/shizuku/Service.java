package vegabobo.dsusideloader.shizuku;

import android.os.RemoteException;

import vegabobo.dsusideloader.IUserService;

public class Service extends IUserService.Stub {
    @Override
    public void destroy() throws RemoteException {}

    @Override
    public void exit() throws RemoteException {}

    @Override
    public String test() throws RemoteException {
        return "Hello!";
    }
}
