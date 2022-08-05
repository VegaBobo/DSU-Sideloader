package vegabobo.dsusideloader.preparation

enum class PreparationSteps {
    NONE,
    FINISHED,
    COPYING_FILE,
    DECOMPRESSING_XZ,
    COMPRESSING_TO_GZ,
    DECOMPRESSING_GZIP
}