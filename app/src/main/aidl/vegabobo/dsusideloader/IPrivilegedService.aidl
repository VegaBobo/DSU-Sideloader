package vegabobo.dsusideloader;

import android.gsi.GsiProgress;
import android.os.storage.VolumeInfo;

interface IPrivilegedService {
    void exit() = 1;
    void destroy() = 16777114;
    void setDynProp() = 100;
    int getUid() = 1000;

    // Activity Manager
    void startActivity(in Intent intent) = 1001;
    void forceStopPackage(String packageName) = 1003;

    // Package Manager
    void grantPermission(String permission) = 2001;

    // Storage Manager
    List<VolumeInfo> getVolumes() = 3001;
    void unmount(String volId) = 3002;
    void mount(String volId) = 3003;

    // Dynamic System
    GsiProgress getInstallationProgress() = 4001;
    boolean abort() = 4002;
    boolean isInUse() = 4003;
    boolean isInstalled() = 4004;
    boolean isEnabled() = 4005;
    boolean remove() = 4006;
    boolean setEnable(boolean enable, boolean oneShot) = 4007;
    boolean finishInstallation() = 4008;
    boolean startInstallation(String dsuSlot) = 4009;
    int createPartition(@utf8InCpp String name, long size, boolean readOnly) = 4010;
    boolean closePartition() = 4011;
    boolean setAshmem(in ParcelFileDescriptor fd, long size) = 4012;
    boolean submitFromAshmem(long bytes) = 4013;
    long suggestScratchSize() = 4014;
}