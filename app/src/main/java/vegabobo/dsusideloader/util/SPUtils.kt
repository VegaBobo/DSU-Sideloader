package vegabobo.dsusideloader.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import vegabobo.dsusideloader.BuildConfig

class SPUtils {

    object Constants {
        const val PREF_RW_PATH = "saf_rw_path"
        const val PREF_HAS_USER_SEEN_INFO_DIALOGS = "has_seen_information_dialogs"
        const val PREF_DEBUG_MODE = "debug_mode"
        const val PREF_USERDATA_SIZE = "userdata_size"
        const val PREFERENCE_FILE = "${BuildConfig.APPLICATION_ID}_preferences"
    }

    companion object {

        fun getUserdataSize(c: Context): Int {
            return getIntegerFromSP(c, Constants.PREF_USERDATA_SIZE, 8)
        }

        fun isDebugModeEnabled(c: Context): Boolean {
            return getBooleanFromSP(c, Constants.PREF_DEBUG_MODE)
        }

        fun hasUserSeenDialogsBefore(c: Context): Boolean {
            return getBooleanFromSP(c, Constants.PREF_HAS_USER_SEEN_INFO_DIALOGS)
        }

        fun setUserHasSeenDialogsBefore(c: Context) {
            writeBooleanToSP(c, Constants.PREF_HAS_USER_SEEN_INFO_DIALOGS, true)
        }

        fun getSafRwPath(c: Context): String {
            return getStringFromSP(c, Constants.PREF_RW_PATH)
        }

        fun writeSafRwPath(c: Context, path: String) {
            writeStringToSP(c, Constants.PREF_RW_PATH, path)
        }

        private fun getStringFromSP(c: Context, key: String): String {
            val prefs =
                c.getSharedPreferences(Constants.PREFERENCE_FILE, AppCompatActivity.MODE_PRIVATE)
            return prefs.getString(key, "")!!
        }

        private fun writeStringToSP(c: Context, key: String, value: String) {
            val editor =
                c.getSharedPreferences(Constants.PREFERENCE_FILE, AppCompatActivity.MODE_PRIVATE)
                    .edit()
            editor.putString(key, value)
            editor.apply()
        }

        private fun getBooleanFromSP(c: Context, key: String): Boolean {
            val prefs =
                c.getSharedPreferences(Constants.PREFERENCE_FILE, AppCompatActivity.MODE_PRIVATE)
            return prefs.getBoolean(key, false)
        }

        private fun writeBooleanToSP(c: Context, key: String, value: Boolean) {
            val editor =
                c.getSharedPreferences(Constants.PREFERENCE_FILE, AppCompatActivity.MODE_PRIVATE)
                    .edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        private fun getIntegerFromSP(c: Context, key: String, fallback: Int): Int {
            val prefs =
                c.getSharedPreferences(Constants.PREFERENCE_FILE, AppCompatActivity.MODE_PRIVATE)
            return prefs.getInt(key, fallback)
        }

        private fun writeIntegerToSP(c: Context, key: String, value: Int) {
            val editor =
                c.getSharedPreferences(Constants.PREFERENCE_FILE, AppCompatActivity.MODE_PRIVATE)
                    .edit()
            editor.putInt(key, value)
            editor.apply()
        }

    }

}