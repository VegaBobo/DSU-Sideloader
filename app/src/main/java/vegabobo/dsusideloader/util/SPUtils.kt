package vegabobo.dsusideloader.util

import android.content.Context
import android.content.SharedPreferences
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

        private fun getSharedPrefs(ctx: Context): SharedPreferences {
            return ctx.getSharedPreferences(
                Constants.PREFERENCE_FILE,
                AppCompatActivity.MODE_PRIVATE
            )
        }

        private fun getStringFromSP(ctx: Context, key: String): String {
            val prefs = getSharedPrefs(ctx)
            return prefs.getString(key, "")!!
        }

        private fun writeStringToSP(ctx: Context, key: String, value: String) {
            val editor = getSharedPrefs(ctx).edit()
            editor.putString(key, value)
            editor.apply()
        }

        private fun getBooleanFromSP(ctx: Context, key: String): Boolean {
            val prefs = getSharedPrefs(ctx)
            return prefs.getBoolean(key, false)
        }

        private fun writeBooleanToSP(ctx: Context, key: String, value: Boolean) {
            val editor = getSharedPrefs(ctx).edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        private fun getIntegerFromSP(ctx: Context, key: String, fallback: Int): Int {
            val prefs = getSharedPrefs(ctx)
            return prefs.getInt(key, fallback)
        }

        private fun writeIntegerToSP(ctx: Context, key: String, value: Int) {
            val editor = getSharedPrefs(ctx).edit()
            editor.putInt(key, value)
            editor.apply()
        }
    }
}
