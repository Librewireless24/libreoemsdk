package com.cumulations.libreV2

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build

import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.libreAlexa.R
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.netty.NettyDecoder
import com.libreAlexa.util.LibreLogger

fun IntArray.containsOnly(num: Int): Boolean = filter { it == num }.isNotEmpty()

@SuppressLint("RestrictedApi")
fun removeShiftMode(view: BottomNavigationView) {
    val menuView = view.getChildAt(0) as BottomNavigationMenuView
    try {
        val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
        shiftingMode.isAccessible = true
        shiftingMode.setBoolean(menuView, false)
        shiftingMode.isAccessible = false
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView
           // item.setShiftingMode(false)
            item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            // set once again checked value, so view will be updated
            item.setChecked(item.itemData!!.isChecked)
        }

    } catch (e: NoSuchFieldException) {
        LibreLogger.d("AppExtensions","ERROR NO SUCH FIELD Unable to get shift mode field")
    } catch (e: IllegalAccessException) {
        LibreLogger.d("AppExtensions","ERROR ILLEGAL ALG Unable to change value of shift mode")
    }
}

fun String.toHtmlSpanned(): Spanned =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(this)
        }

fun unbindWifiNetwork(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val manager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager.bindProcessToNetwork(null)
        LibreLogger.d("AppExtensions","unbindWifiNetwork done")
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        ConnectivityManager.setProcessDefaultNetwork(null)
        LibreLogger.d("AppExtensions","unbindWifiNetwork done")
    }
}

fun Context.hasPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun appInForeground(context: Context): Boolean {
    val activityManager = context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningAppProcesses = activityManager.runningAppProcesses ?: return false
    return runningAppProcesses
            .any {
                it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
}

fun redirectToPlayStore(context: Context, appPackageName: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.parse("market://details?id=$appPackageName")
        })
    } catch (exception: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
        })
    }
}

fun launchTheApp(context: Context,appPackageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(appPackageName)
    if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } else {
        redirectToPlayStore(context,appPackageName)
    }
}

fun getAppVersion(context: Context): String {
    var version = context.getString(R.string.title_activity_welcome)
    var packageInfo: PackageInfo? = null
    try {
        packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    if (packageInfo != null)
        version = packageInfo.versionName

    return version
}

fun writeAwayModeSettingsToDevice(ssid:String,pwd:String,ipAddress:String){
    val luciControl = LUCIControl(ipAddress)
    luciControl.SendCommand(MIDCONST.MID_ENV_READ, LUCIMESSAGES.WRITE_+"ddms_SSID"+","+ssid, LSSDPCONST.LUCI_SET)
    luciControl.SendCommand(MIDCONST.MID_ENV_READ, LUCIMESSAGES.WRITE_+"ddms_password"+","+pwd, LSSDPCONST.LUCI_SET)
}

fun closeKeyboard(context: Context, view: View?) {
    try {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun openKeyboard(context: Context, view: View?) {
    try {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) {
            inputManager.toggleSoftInputFromWindow(view.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0)
            view.requestFocus()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.isVisibleToUser():Boolean{
    return this.window.decorView.isShown
}

fun isConnectedToSAMode(ssid: String):Boolean{
    return (ssid.contains(Constants.SA_SSID_RIVAA_CONCERT) || ssid.contains(Constants.SA_SSID_RIVAA_STADIUM))
            && !ssid.contains("_")
}

