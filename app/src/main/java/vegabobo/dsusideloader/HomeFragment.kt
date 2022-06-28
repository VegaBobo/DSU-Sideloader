package vegabobo.dsusideloader

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import vegabobo.dsusideloader.checks.CompatibilityCheck
import vegabobo.dsusideloader.checks.OperationMode
import vegabobo.dsusideloader.dsuhelper.GsiDsuObject
import vegabobo.dsusideloader.dsuhelper.PrepareDsu
import vegabobo.dsusideloader.util.FilenameUtils
import vegabobo.dsusideloader.util.SPUtils
import vegabobo.dsusideloader.util.SetupStorageAccess
import vegabobo.dsusideloader.util.WorkspaceFilesUtils
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private val gsiDsuObject = GsiDsuObject()
    var selectedGsi: Uri = Uri.EMPTY

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isEnvCompatible(true))
            SetupStorageAccess(requireContext())

        // gsid refuses to start installation when < 40% free storage
        // prevent user from using app on this circumstances
        if (!hasAvailableStorage())
            showNoAvaiableStorageDialog()

        gsiDsuObject.userdataSize = SPUtils.getUserdataSize(requireActivity())

        val edGsiPath = requireView().findViewById<TextInputEditText>(R.id.ed_gsi_path)
        val btnInstall = requireView().findViewById<MaterialButton>(R.id.btn_install)
        val btnIncrease = requireView().findViewById<MaterialButton>(R.id.bt_increase)
        val btnDecrease = requireView().findViewById<MaterialButton>(R.id.btn_decrease)
        val cbDSsize = requireView().findViewById<MaterialCheckBox>(R.id.cb_ds_size)
        val edDSsize = requireView().findViewById<TextInputEditText>(R.id.ed_ds_size)
        val cbGSIsize = requireView().findViewById<MaterialCheckBox>(R.id.cb_gsi_size)
        val edGSIsize = requireView().findViewById<TextInputEditText>(R.id.ed_gsi_size)
        val tc = requireView().findViewById<MaterialTextView>(R.id.tv_defaultuserdata)
        val txDebugBuildInfo = requireView().findViewById<MaterialTextView>(R.id.text_debugbuild)

        if(BuildConfig.DEBUG) {
            txDebugBuildInfo.visibility = View.VISIBLE
            txDebugBuildInfo.text = getString(R.string.debug_build_info, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        }

        tc.text = getString(R.string.default_userdata_help, gsiDsuObject.userdataSize)

        edDSsize.setText(getString(R.string.gigabyte_holder, gsiDsuObject.userdataSize))

        val fileSelection =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data!!.data
                    selectedGsi = uri!!
                    btnInstall.isEnabled = true
                    edGsiPath.setText(FilenameUtils.queryName(requireContext().contentResolver, uri))
                    btnInstall.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.white_alpha
                        )
                    )
                    btnInstall.setIconTintResource(R.color.white_alpha)
                }
            }

        edGsiPath.setOnClickListener {
            var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
            chooseFile.type = "*/*"
            val mimetypes = arrayOf(
                "application/gzip",
                "application/x-gzip",
                "application/x-xz",
                "application/zip",
                "application/octet-stream"
            )
            chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
            chooseFile = Intent.createChooser(chooseFile, getString(R.string.saf_choose_file))
            fileSelection.launch(chooseFile)
        }

        btnIncrease.setOnClickListener {
            gsiDsuObject.userdataSize++
            edDSsize.setText(getString(R.string.gigabyte_holder, gsiDsuObject.userdataSize))

        }

        btnDecrease.setOnClickListener {
            if (gsiDsuObject.userdataSize >= 2)
                gsiDsuObject.userdataSize--
            edDSsize.setText(getString(R.string.gigabyte_holder, gsiDsuObject.userdataSize))
        }

        cbDSsize.setOnClickListener {
            if (cbDSsize.isChecked) {
                gsiDsuObject.userdataSize = SPUtils.getUserdataSize(requireContext())
                edDSsize.setText(getString(R.string.gigabyte_holder, gsiDsuObject.userdataSize))
                edDSsize.isEnabled = false
                btnIncrease.visibility = View.GONE
                btnDecrease.visibility = View.GONE
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                edDSsize.layoutParams = params
            } else {
                edDSsize.isEnabled = true
                edDSsize.keyListener = null
                btnIncrease.visibility = View.VISIBLE
                btnDecrease.visibility = View.VISIBLE
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                edDSsize.layoutParams = params
            }
        }

        cbGSIsize.setOnClickListener {
            if (cbGSIsize.isChecked) {
                edGSIsize.setText(getString(R.string.auto))
                edGSIsize.isEnabled = false
            } else {
                edGSIsize.setText("")
                edGSIsize.isEnabled = true
                edGSIsize.requestFocus()
                edGSIsize.hint = getString(R.string.type_bytes)
            }
        }

        btnInstall.setOnClickListener {

            if (!cbGSIsize.isChecked) {
                gsiDsuObject.fileSize = if (edGSIsize.toString().isNotEmpty()) {
                    edGSIsize.text.toString().toLong()
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.invalid_gsi_size, getString(R.string.auto)),
                        Toast.LENGTH_SHORT
                    ).show()
                    -1
                }
            }

            if (!cbDSsize.isChecked) {
                gsiDsuObject.userdataSize = edDSsize.text.toString().split("GB")[0].toInt()
            }

            beginInstall(selectedGsi, gsiDsuObject)
        }

        val cb = requireView().findViewById<MaterialCheckBox>(R.id.cb_keepawake)
        cb.setOnClickListener {
            if (cb.isChecked) requireActivity().window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
            else requireActivity().window.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        if (selectedGsi != Uri.EMPTY)
            btnInstall.isEnabled = true

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    private fun checkDialog(title: String, text: String, finish: Boolean) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setMessage(text)
            .setPositiveButton(if (finish) getString(R.string.close_app) else getString(R.string.got_it)) { _, _ -> if (finish) requireActivity().finish() }
            .setCancelable(false)
            .show()
    }

    private fun hasAvailableStorage(): Boolean {
        val statFs = StatFs(Environment.getDataDirectory().absolutePath)
        val blockSize = statFs.blockSizeLong
        val totalSize = statFs.blockCountLong * blockSize
        val availableSize = statFs.availableBlocksLong * blockSize
        return ((availableSize.toFloat() / totalSize.toFloat()) * 100).roundToInt() > 40
    }

    private fun isEnvCompatible(showDialogs: Boolean): Boolean {
        return (isMagiskVersionCompatible(showDialogs) && isPropsValid(showDialogs))
    }

    private fun isPropsValid(showDialogs: Boolean): Boolean {
        if (!CompatibilityCheck.checkDynamicPartitions()) {
            if (showDialogs) {
                checkDialog(
                    getString(R.string.unsupported),
                    getString(R.string.device_unsupported),
                    true
                )
            }
            return false
        } else if (CompatibilityCheck.isBootloaderLocked() && CompatibilityCheck.signOfCustomOS()) {
            if (showDialogs && !SPUtils.hasUserSeenDialogsBefore(requireContext())) {
                checkDialog(
                    getString(R.string.notice),
                    getString(R.string.notice_lockedbl_custom),
                    false
                )
            }
        } else if (CompatibilityCheck.isBootloaderLocked()) {
            if (showDialogs && !SPUtils.hasUserSeenDialogsBefore(requireContext())) {
                checkDialog(
                    getString(R.string.notice),
                    getString(R.string.notice_lockedbl),
                    false
                )
            }
        } else if (CompatibilityCheck.signOfCustomOS()) {
            if (showDialogs && !SPUtils.hasUserSeenDialogsBefore(requireContext())) {
                checkDialog(
                    getString(R.string.notice),
                    getString(R.string.notice_custom),
                    false
                )
            }
        }
        SPUtils.setUserHasSeenDialogsBefore(requireActivity())
        return true
    }

    private fun isMagiskVersionCompatible(showDialogs: Boolean): Boolean {
        if (CompatibilityCheck.isUsingIncompatibleMagisk()) {
            if (showDialogs) {
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(getString(R.string.unsupported))
                    .setMessage(
                        getString(
                            R.string.outdated_magisk,
                            OperationMode.obtainMagiskVersion()
                        )
                    )
                    .setPositiveButton(getString(R.string.close_app)) { _, _ ->
                        requireActivity().finish()
                    }
                    .setCancelable(false)
                    .show()
            }
            return false
        }
        return true
    }

    private fun beginInstall(selectedGsi: Uri, gsiDsuObject: GsiDsuObject) {

        val selectedFile = FilenameUtils.queryName(requireActivity().contentResolver, selectedGsi)

        // file need to have a extension, if not, show error dialog.
        if (selectedFile.contains(".")) {

            when (selectedFile.substring(selectedFile.lastIndexOf("."))) {
                ".xz", ".gz", ".img", ".zip" -> {

                    MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.info)
                        .setMessage(getString(R.string.warning))
                        .setPositiveButton(getString(R.string.proceed)) { _, _ ->

                            MaterialAlertDialogBuilder(requireActivity())
                                .setTitle(getString(R.string.installation))
                                .setMessage(
                                    getString(
                                        R.string.installation_details,
                                        selectedFile,
                                        gsiDsuObject.userdataSize.toString(),
                                        if (gsiDsuObject.fileSize == -1L) getString(R.string.auto) else gsiDsuObject.fileSize
                                    )
                                )
                                .setPositiveButton(getString(R.string.proceed)) { _, _ ->
                                    WorkspaceFilesUtils.cleanWorkspaceFolder(
                                        requireActivity(),
                                        true
                                    )
                                    Thread(
                                        PrepareDsu(
                                            requireActivity(),
                                            selectedGsi,
                                            gsiDsuObject
                                        )
                                    ).start()
                                }
                                .setNegativeButton(getString(R.string.cancel), null)
                                .setCancelable(true)
                                .show()

                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                }
                else -> {
                    showUnsupportedDialog()
                }
            }
        } else {
            showUnsupportedDialog()
        }
    }

    private fun showUnsupportedDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.unsupported)
            .setMessage(getString(R.string.file_unsupported))
            .setPositiveButton(getString(R.string.got_it), null)
            .setCancelable(true)
            .show()
    }

    private fun showNoAvaiableStorageDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.error)
            .setMessage(getString(R.string.storage_warning))
            .setPositiveButton(getString(R.string.close_app)) { _, _ -> requireActivity().finish() }
            .setCancelable(false)
            .show()
    }

}