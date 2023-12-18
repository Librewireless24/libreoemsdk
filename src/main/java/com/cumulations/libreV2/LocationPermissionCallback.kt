package com.cumulations.libreV2

interface LocationPermissionCallback {
    fun onPermissionGranted()
    fun onPermissionDenied()
    fun onGPSGranted()
}