package vegabobo.dsusideloader.util

import android.app.Application
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import vegabobo.dsusideloader.preferences.AppPrefs

/**
 * Check if app was signed using original author's keystore.
 *
 * Update feature should fetch only updates from builds
 * signed with the same keystore as build author.
 *
 * This is done to avoid updater to fetch conflicting signed apks
 * which may lead into installation errors anyway.
 */
fun Application.isBuildSignedByAuthor(): Boolean {
    val signatures = getSignatures(this.packageManager, packageName) ?: return false
    val authorDigest = AppPrefs.AUTHOR_SIGN_DIGEST
    signatures.forEach { digest -> if (digest == authorDigest) return true }
    return false
}

private fun getSignatures(pm: PackageManager, packageName: String): List<String?>? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val packageInfo = pm.getPackageInfo(
            packageName,
            PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong()),
        )
        if (packageInfo.signingInfo.hasMultipleSigners()) {
            return signatureDigest(packageInfo.signingInfo.apkContentsSigners)
        }
        return signatureDigest(packageInfo.signingInfo.signingCertificateHistory)
    }

    @Suppress("DEPRECATION")
    val packageInfo =
        pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES) ?: return null
    if (packageInfo.signingInfo.hasMultipleSigners()) {
        return signatureDigest(packageInfo.signingInfo.apkContentsSigners)
    }
    return signatureDigest(packageInfo.signingInfo.signingCertificateHistory)
}

private fun signatureDigest(sig: Signature): String? {
    val signature = sig.toByteArray()
    return try {
        val md = MessageDigest.getInstance("SHA1")
        val digest = md.digest(signature)
        return digest.joinToString("") { "%02x".format(it) }
    } catch (e: NoSuchAlgorithmException) {
        null
    }
}

private fun signatureDigest(sigList: Array<Signature>): List<String?> {
    return sigList.map { s -> signatureDigest(s) }
}
