package vegabobo.dsusideloader

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vegabobo.dsusideloader.checks.OperationMode

class PreferencesFragment : PreferenceFragmentCompat() {

    private var OP_MODE = ""

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)

        val myPref: Preference? = findPreference("op_mode") as Preference?
        val z = retrieveOpModeString()
        myPref!!.summary = OP_MODE
        with(myPref) {
            this.setOnPreferenceClickListener {
                dialogInfo(z)
                true
            }
        }

        val about: Preference? = findPreference("about") as Preference?
        with(about) {
            this!!.setOnPreferenceClickListener {
                val myIntent = Intent(activity, AboutActivity::class.java)
                startActivity(myIntent)
                true
            }
        }
    }

    private fun dialogInfo(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.info)
            .setMessage(message)
            .setPositiveButton(R.string.got_it, null)
            .create()
            .show()
    }

    private fun retrieveOpModeString(): String {
        return when (OperationMode.getOperationMode()) {
            OperationMode.Constants.UNROOTED -> {
                OP_MODE = "ADB"
                getString(
                    R.string.operation_mode,
                    "NON-ROOT"
                ) + "\n" + getString(R.string.operation_nonroot)
            }
            OperationMode.Constants.ROOT_MAGISK -> {
                OP_MODE = "ROOT"
                getString(
                    R.string.operation_mode,
                    "MAGISK"
                ) + "\n" + getString(R.string.operation_magisk)
            }
            OperationMode.Constants.OTHER_ROOT_SOLUTION -> {
                OP_MODE = "ROOT"
                getString(
                    R.string.operation_mode,
                    "ROOT_OTHER"
                ) + "\n" + getString(R.string.operation_root_other)
            }
            else -> {
                "Error"
            }
        }
    }
}
