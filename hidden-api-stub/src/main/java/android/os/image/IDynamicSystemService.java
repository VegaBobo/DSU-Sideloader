package android.os.image;

import android.gsi.GsiProgress;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;

public interface IDynamicSystemService extends IInterface {

    GsiProgress getInstallationProgress();

    boolean abort();

    boolean isInUse();

    boolean isInstalled();

    boolean isEnabled();

    boolean remove();

    boolean setEnable(boolean enable, boolean oneShot);

    boolean finishInstallation();

    boolean startInstallation(String dsuSlot);

    int createPartition(String name, long size, boolean readOnly);

    boolean closePartition();

    boolean setAshmem(ParcelFileDescriptor fd, long size);

    boolean submitFromAshmem(long bytes);

    long suggestScratchSize();

    abstract class Stub extends Binder implements IDynamicSystemService {

        public static IDynamicSystemService asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }

    }
}
