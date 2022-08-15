package vegabobo.dsusideloader.preparation

enum class InstallationSteps {
    // Initial value
    NONE,

    // Preparation steps
    COPYING_FILE,
    DECOMPRESSING_XZ,
    COMPRESSING_TO_GZ,
    DECOMPRESSING_GZIP,
    EXTRACTING_FILE,

    // Once preparation is done
    READY_TO_INSTALL,

    // Installation Steps
    DISCARD_CURRENT_GSI,
    WAITING_USER_CONFIRMATION,
    INSTALLING,

    // Installation Errors
    ERROR_EXTERNAL_SDCARD_ALLOC,
    ERROR_NO_AVAIL_STORAGE,
    ERROR_F2FS_WRONG_PATH,
    ERROR_EXTENTS,
    ERROR_SELINUX_A10,
    ERROR_SELINUX_A10_ROOTLESS,
    ERROR_GENERIC_ERROR,
    CANCELED,

    // Once installation is finished with success
    DONE,
}