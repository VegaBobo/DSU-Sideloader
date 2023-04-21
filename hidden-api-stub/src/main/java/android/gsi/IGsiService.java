package android.gsi;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.image.IDynamicSystemService;

public interface IGsiService extends IInterface {
    int INSTALL_OK = 0;
    int INSTALL_ERROR_GENERIC = 1;
    int INSTALL_ERROR_NO_SPACE = 2;

    int enableGsi(boolean oneShot, String dsuSlot);

    abstract class Stub extends Binder implements IGsiService {

        public static IGsiService asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }

    }
}
