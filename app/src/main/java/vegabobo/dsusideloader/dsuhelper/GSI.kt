package vegabobo.dsusideloader.dsuhelper

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class GSI(
    var absolutePath: String = "",
    var targetUri: Uri = Uri.EMPTY,
    var name: String = "",
    var fileSize: Long = Constants.DEFAULT_FILE_SIZE,
    var userdataSize: Int = Constants.DEFAULT_USERDATA_SIZE_IN_GB
) : Parcelable {

    object Constants {
        const val DEFAULT_FILE_SIZE = -1L // bytes
        const val DEFAULT_USERDATA_SIZE_IN_GB = 8 // gigabytes
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(Uri::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readInt()
    ) {
    }

    fun getUserdataInBytes(): Long {
        return userdataSize.toLong() * 1024L * 1024L * 1024L
    }

    fun setFileSize(size: String) {
        if(size.isNotEmpty())
            this.fileSize = size.toLong()
    }

    fun setUserdataSize(size: String) {
        if(size.isNotEmpty())
            this.userdataSize = size.toInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(absolutePath)
        parcel.writeParcelable(targetUri, flags)
        parcel.writeString(name)
        parcel.writeLong(fileSize)
        parcel.writeInt(userdataSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GSI> {
        override fun createFromParcel(parcel: Parcel): GSI {
            return GSI(parcel)
        }

        override fun newArray(size: Int): Array<GSI?> {
            return arrayOfNulls(size)
        }
    }

}
