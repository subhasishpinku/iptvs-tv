// VersionManager.kt
package com.bacbpl.iptv.utils

import android.content.Context
import android.content.pm.PackageManager

object VersionManager {

    private const val MINIMUM_VERSION_CODE = 2
    private const val MINIMUM_VERSION_NAME = "1.1"

    fun isVersionValid(context: Context): Boolean {
        val currentVersionCode = AppVersion.getVersionCode(context)
        return currentVersionCode >= MINIMUM_VERSION_CODE
    }

    fun checkVersionAndProceed(
        context: Context,
        onValid: () -> Unit,
        onInvalid: () -> Unit
    ) {
        if (isVersionValid(context)) {
            onValid()
        } else {
            onInvalid()
        }
    }

    fun getUpdateMessage(context: Context): String {
        val currentVersion = AppVersion.getVersionName(context)
        return "Your version ($currentVersion) is out of date. Please update to version $MINIMUM_VERSION_NAME to continue using the app."
    }
}