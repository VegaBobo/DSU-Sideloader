package vegabobo.dsusideloader.model

import android.net.Uri

object DSUConstants {
    const val DEFAULT_IMAGE_SIZE = -1L
    const val DEFAULT_USERDATA = 2L * 1024L * 1024L * 1024L
}

open class ImagePartition(
    var partitionName: String,
    var uri: Uri = Uri.EMPTY,
    var fileLength: Long = DSUConstants.DEFAULT_IMAGE_SIZE,
)

enum class Type {
    NONE, SINGLE_SYSTEM_IMAGE, DSU_PACKAGE, URL, MULTIPLE_IMAGES
}

open class DSUInstallation(
    open var type: Type = Type.NONE,
    open var uri: Uri = Uri.EMPTY,
    open var fileLength: Long = DSUConstants.DEFAULT_IMAGE_SIZE,
    open val images: List<ImagePartition> = listOf(),
) {

    data class SingleSystemImage(
        override var uri: Uri = Uri.EMPTY,
        override var fileLength: Long = DSUConstants.DEFAULT_IMAGE_SIZE,
        override var type: Type = Type.SINGLE_SYSTEM_IMAGE,
    ) : DSUInstallation(Type.SINGLE_SYSTEM_IMAGE)

    data class DsuPackage(
        override var uri: Uri = Uri.EMPTY,
        override var type: Type = Type.DSU_PACKAGE,
    ) : DSUInstallation(Type.DSU_PACKAGE)

    data class Url(
        override var uri: Uri,
        override var type: Type = Type.URL,
    ) : DSUInstallation(Type.URL)

    data class MultipleImages(
        override val images: List<ImagePartition>,
        override var type: Type = Type.MULTIPLE_IMAGES,
    ) : DSUInstallation(Type.MULTIPLE_IMAGES)

}