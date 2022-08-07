package vegabobo.dsusideloader;

interface IPrivilegedService {
    void exit() = 1;
    void destroy() = 16777114;

    int getUid() = 1000;
    void startActivity(in Intent intent) = 1001;
    void grantPermission(String permission) = 1002;
    List getVolumes() = 1003;
    void unmount(String volId) = 1004;
    void mount(String volId) = 1005;
}