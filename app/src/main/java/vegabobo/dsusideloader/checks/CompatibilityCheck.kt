package vegabobo.dsusideloader.checks

import android.util.Log

class CompatibilityCheck {

    companion object {

        fun isBootloaderLocked(): Boolean {
            return getSystemProperty("ro.boot.vbmeta.device_state")!! == "locked"
        }

        fun signOfCustomOS(): Boolean {
            return getSystemProperty("ro.build.type")!! != "user"
        }

        fun checkDynamicPartitions(): Boolean {
            return getSystemProperty("ro.boot.dynamic_partitions") == "true"
        }

        fun isUsingIncompatibleMagisk(): Boolean {
            return OperationMode.getOperationMode() == OperationMode.Constants.MAGISK_UNSUPPORTED
        }

        private fun getSystemProperty(key: String?): String? {
            var value: String? = null
            try {
                value = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String::class.java).invoke(null, key) as String
            } catch (e: Exception) {
                Log.e("getSystemProperty", e.stackTraceToString())
            }
            return value
        }

    }


}