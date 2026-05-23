package com.bacbpl.iptv.utils

import android.content.Context

object AppVersion {

    fun getVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                0
            )
            packageInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    fun getVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                0
            )

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }

        } catch (e: Exception) {
            1
        }
    }
}