package vegabobo.dsusideloader.dsuhelper

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class GsiDsuObject(
    var absolutePath: String? = "",
    var targetUri: Uri = Uri.EMPTY,
    var fileSize: Long = Constants.DEFAULT_FILE_SIZE,
    var userdataSize: Int = Constants.DEFAULT_USERDATA_SIZE_IN_GB,
) : Parcelable {

    object Constants {
        const val DEFAULT_FILE_SIZE = -1L // bytes
        const val DEFAULT_USERDATA_SIZE_IN_GB = 8 // gigabytes
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readLong(),
        parcel.readInt()
    ) {
    }

    fun setFileSize(size: String) {
        this.fileSize = size.toLong()
    }

    fun setUserdataSize(size: String) {
        this.userdataSize = size.toInt()
    }
    fun getUserdataInBytes(): Long {
        return userdataSize.toLong() * 1024L * 1024L * 1024L
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(absolutePath)
        parcel.writeParcelable(targetUri, flags)
        parcel.writeLong(fileSize)
        parcel.writeInt(userdataSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GsiDsuObject> {
        override fun createFromParcel(parcel: Parcel): GsiDsuObject {
            return GsiDsuObject(parcel)
        }

        override fun newArray(size: Int): Array<GsiDsuObject?> {
            return arrayOfNulls(size)
        }
    }

}
