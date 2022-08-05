package android.os.storage;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;

public interface IStorageManager extends IInterface {

    VolumeInfo[] getVolumes(int flags);

    void unmount(String volId);

    void mount(String volId);

    abstract class Stub extends Binder implements IStorageManager {

        public static IStorageManager asInterface(IBinder obj) {
            throw new UnsupportedOperationException();
        }

    }
}
