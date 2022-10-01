package android.gsi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

public interface IGsiService extends IInterface {

    public static int STATUS_NO_OPERATION = 0;
    public static int STATUS_WORKING = 1;
    public static int STATUS_COMPLETE = 2;
    public static int INSTALL_OK = 0;
    public static int INSTALL_ERROR_GENERIC = 1;
    public static int INSTALL_ERROR_NO_SPACE = 2;
    public static int INSTALL_ERROR_FILE_SYSTEM_CLUTTERED = 3;

    GsiProgress getInstallProgress();
    int enableGsi(boolean oneShot, String dsuSlot);
    boolean isGsiEnabled();
    boolean cancelGsiInstall();
    boolean isGsiInstallInProgress();
    boolean removeGsi();
    boolean isGsiInstalled();

    abstract class Stub extends Binder implements IGsiService {

        public static IGsiService asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }

    }
}
