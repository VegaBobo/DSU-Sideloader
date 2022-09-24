package vegabobo.dsusideloader.util

class DevicePropUtils {

    companion object {

        /**
         * Check if custom gsid binary prop is set, when this prop is set
         * we will return the minimum percentage allocation allowed by custom gsid binary.
         * If not set, then return default values from gsid (which is 40%)
         *
         * @return Minimum percentage allowed by gsid binary (default is 0.40F)
         * @see :magisk-module
         */
        fun getGsidBinaryAllowedPerc(): Float {
            val minAllowed = getSystemProperty("ro.vegabobo.dsusideloader.gsid_min_alloc")
            return if (minAllowed.isNotEmpty()) minAllowed.toFloat() else 0.40F
        }

        fun hasDynamicPartitions(): Boolean {
            return getSystemProperty("ro.boot.dynamic_partitions") == "true"
        }

        private fun getSystemProperty(key: String): String {
            return Class.forName("android.os.SystemProperties")
                .getMethod("get", String::class.java).invoke(null, key) as String
        }

    }

}