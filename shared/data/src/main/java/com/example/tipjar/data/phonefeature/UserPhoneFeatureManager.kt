package com.example.tipjar.data.phonefeature

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UserPhoneFeatureManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : IUserPhoneFeatureManager {
    override fun isCameraAvailable(): Boolean {
        val pm = applicationContext.packageManager

        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

}