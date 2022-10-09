package android.gsi;

import android.os.IInterface;

public interface IGsiService extends IInterface {
    int INSTALL_OK = 0;
    int INSTALL_ERROR_GENERIC = 1;
    int INSTALL_ERROR_NO_SPACE = 2;
}
