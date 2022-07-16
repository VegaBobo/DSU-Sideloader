package vegabobo.dsusideloader.util

import android.util.Log

class VerificationUtils {

    companion object {

        fun hasDynamicPartitions(): Boolean {
            return getSystemProperty("ro.boot.dynamic_partitions") == "true"
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