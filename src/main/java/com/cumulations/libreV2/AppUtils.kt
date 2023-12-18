package com.cumulations.libreV2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.cumulations.libreV2.activity.CTWifiListActivity
import com.cumulations.libreV2.model.SceneObject
import com.libreAlexa.LibreApplication
import com.libreAlexa.R
import com.libreAlexa.Scanning.ScanningHandler
import com.libreAlexa.constants.Constants
import com.libreAlexa.constants.ControlConstants
import com.libreAlexa.constants.LSSDPCONST
import com.libreAlexa.constants.LUCIMESSAGES
import com.libreAlexa.constants.MIDCONST
import com.libreAlexa.luci.LUCIControl
import com.libreAlexa.util.LibreLogger
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.regex.Pattern


object AppUtils {

    var TAG: String = AppUtils::class.java.simpleName

    fun getConnectedSSID(context: Context): String {
        var connectedSSID = ""
        val wifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        connectedSSID = wifiInfo.ssid.replace("\"", "", false)
        LibreLogger.d(TAG, "connectedSSID $connectedSSID")

        if (connectedSSID == "<unknown ssid>") {
            return ""
        }

        return connectedSSID
    }


    fun getConnectedSSIDAndSecurityType(context: Context): Pair<String, String?> {
        var connectedSSID = ""
        var securityType: String? = null
        try {
            LibreLogger.d(TAG, "getConnectedSSIDAndSecurityType try ")
            // Check if the app has the necessary location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Handle the case where permission is not granted
                // You can request permission here or handle it in the calling code
                LibreLogger.d(TAG, "getConnectedSSIDAndSecurityType No Permission ")
                return Pair("", "Permission not granted")
            }
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val networkList: List<ScanResult> = wifiManager.scanResults
            val wi: WifiInfo = wifiManager.connectionInfo
            connectedSSID = getConnectedSSID(context)
            LibreLogger.d(TAG, "getConnectedSSIDAndSecurityType networkList " + networkList.size)
            if (networkList.isNotEmpty()) {
                for (network in networkList) {
                    LibreLogger.d(TAG, "getConnectedSSIDAndSecurityType network.SSID " + "before if " + network.SSID + " currentSSID " + connectedSSID)
                    if (connectedSSID == network.SSID) {
                        // Get capabilities of the current connection
                        val capabilities: String = network.capabilities

                        when {
                            capabilities.contains("WPA3") -> {
                                securityType = "WPA3"
                            }

                            capabilities.contains("WPA2") -> {
                                securityType = "WPA2"
                            }

                            capabilities.contains("WPA") -> {
                                securityType = "WPA"
                            }

                            capabilities.contains("WEP") -> {
                                securityType = "WEP"
                            }

                            capabilities.contains("Open") -> {
                                securityType = "Open"
                            }
                        }
                    }
                }
            } else {
                LibreLogger.d(TAG, "getConnectedSSIDAndSecurityType network list empty")
            }
        } catch (ex: Exception) {
            LibreLogger.d(TAG, "getConnectedSSIDAndSecurityType:Exception ${ex.message}")
        }
        return Pair(connectedSSID, securityType)

    }

    fun isPermissionGranted(context: Context,
        permission: String): Boolean = ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun shouldShowPermissionRationale(context: Context,
        permission: String): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)

    fun requestPermission(context: Context,
        permission: String,
        requestId: Int) = ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), requestId)

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isContainsSpecialCharacters(value: String): Boolean {
        val special = Pattern.compile("[!@#$%&*()_+:;/><=|<>.?{}\\[\\]~-]")
        val hasSpecial = special.matcher(value)
        return hasSpecial.find()
    }

    fun isContainsNumber(value: String): Boolean {
        val digit = Pattern.compile("[0-9]")
        val hasDigit = digit.matcher(value)
        return hasDigit.find()
    }

    fun isOnline(context: Context): Boolean {
        val connectivityMananger: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityMananger.activeNetworkInfo != null && connectivityMananger.activeNetworkInfo!!.isConnected
    }

    fun isLocationServiceEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            // For devices prior to Android 10, use a combination of providers
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
       /* val locationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)*/
    }
    fun getWifiIp(context: Context): String {
        val wifiMan = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        val ipAddress = wifiInf.ipAddress
        return String.format("%d.%d.%d.%d", ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff, ipAddress shr 24 and 0xff)
    }

    fun showAlertMessageRegardingSAC(context: Context, title: String, message: String) {
        if (!(context as AppCompatActivity).isFinishing) {
            val builder = AlertDialog.Builder(context)

            builder.setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(context.getString(R.string.ok)) { dialog, id ->
                    dialog.dismiss()
                }
            builder.show()
        }
    }

    fun showAlertForNotConnectedToSAC(context: Context) {
        if (!(context as AppCompatActivity).isFinishing) {
            val builder = AlertDialog.Builder(context)
            val message = context.getString(R.string.title_error_sac_message) + "\n(" + Constants.WAC_SSID_RIVAA_CONCERT + "XXXXXX/" + Constants.WAC_SSID_RIVAA_STADIUM + "XXXXXX)"
            builder.setMessage(message).setCancelable(false).setPositiveButton(context.getString(R.string.ok)) { dialog, id ->
                    dialog.dismiss()
                    if (context is CTWifiListActivity) {
                        context.onBackPressed()
                    }
                }
            builder.show()
        }
    }

    fun storeSSIDInfoToSharedPreferences(context: Context, deviceSSID: String, password: String) {
        context.getSharedPreferences("Your_Shared_Prefs", Context.MODE_PRIVATE).apply {
                edit().putString(deviceSSID, password).apply()
            }
    }

    fun updateSceneObjectWithPlayJsonWindow(window: JSONObject,
        oldSceneObject: SceneObject): SceneObject {
        if (window.has("TrackName")) {
            oldSceneObject.trackName = window.getString("TrackName")
        } else {
            oldSceneObject.trackName = "Track Name not available"
        }
        if (window.has("CoverArtUrl")) {
            oldSceneObject.album_art = window.getString("CoverArtUrl")
        } else {
            oldSceneObject.album_art
        }
        if (window.has("PlayState")) {
            oldSceneObject.playstatus = window.getInt("PlayState")
        }
        LibreLogger.d(TAG, "suma in play music player status" + oldSceneObject.currentSource)
        var mPlayURL: String? = null
        if (window.has("PlayUrl")) {
            mPlayURL = window.getString("PlayUrl")
        }
        if (mPlayURL != null) oldSceneObject.playUrl = mPlayURL/*For favourite*/
        if (window.has("Favourite")) {
            oldSceneObject.setIsFavourite(window.getBoolean("Favourite"))
        } else {
            oldSceneObject.setIsFavourite(false)
        }/*Added for Shuffle and Repeat, update only if it's not playing from local DMR
        * Because we need to sync with playback helper in that case not MB*/
        if (!isLocalDMRPlaying(oldSceneObject)) {
            if (window.has("Shuffle")) {
                oldSceneObject.shuffleState = window.getInt("Shuffle")
            }
            if (window.has("Repeat")) {
                oldSceneObject.repeatState = window.getInt("Repeat")
            }

        }
        if (window.has("Album")) {
            oldSceneObject.album_name = window.getString("Album")
        }
        if (window.has("Artist")) {
            oldSceneObject.artist_name = window.getString("Artist")
        }
        if (window.has("Genre")) {
            oldSceneObject.genre = window.getString("Genre")
        }
        if (window.has("TotalTime")) {
            oldSceneObject.totalTimeOfTheTrack = window.getLong("TotalTime")
        }
        if (window.has("Current Source")) {
            oldSceneObject.currentSource = window.getInt("Current Source")
        }
        if (oldSceneObject.currentSource == Constants.AUX_SOURCE || oldSceneObject.currentSource == Constants.EXTERNAL_SOURCE) {
            oldSceneObject.artist_name = "Aux Playing"
        }

        /*if (oldSceneObject.currentSource == Constants.BT_SOURCE) {
            oldSceneObject.artist_name = "Bluetooth Playing"
        }*/
        parseControlJsonForAlexa(window, oldSceneObject)

        return oldSceneObject
    }

    private fun parseControlJsonForAlexa(window: JSONObject, currentSceneObject: SceneObject) {
        try {
            val controlJson: String = if (window.has("ControlsJson")) {
                window.getString("ControlsJson")
            } else {
                ""
            }
            LibreLogger.d(TAG, "JSON recieved for control JSON $controlJson")

            if (controlJson == null || controlJson.isEmpty() || controlJson.equals("null", true)) return

            val controlsJsonArr = JSONArray(controlJson)
            if (controlsJsonArr != null) {
                val flags = booleanArrayOf(false, false, false)
                for (i in 0 until controlsJsonArr.length()) {
                    LibreLogger.d(TAG, "JSON recieved for Alexa controls " + controlsJsonArr.get(i))
                    // sample JSON {\"enabled\":true,\"name\":\"PLAY_PAUSE\",\"selected\":false,\"type\":\"BUTTON\"}
                    val jsonObject = controlsJsonArr.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val enabled = jsonObject.getBoolean("enabled")
                    when (name) {
                        ControlConstants.PLAY_PAUSE -> if (enabled) {
                            flags[0] = true
                        }

                        ControlConstants.NEXT -> if (enabled) {
                            flags[1] = true
                        }

                        ControlConstants.PREVIOUS -> if (enabled) {
                            flags[2] = true
                        }
                    }
                }
                currentSceneObject.setAlexaControls(flags)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isAnyDevicePlaying(): Boolean {
        if (ScanningHandler.getInstance().sceneObjectMapFromRepo.isEmpty()) return false

        ScanningHandler.getInstance().sceneObjectMapFromRepo.forEach { (ipAddress: String?, sceneObject: SceneObject?) ->
            LibreLogger.d("isAnyDevicePlaying", "$ipAddress, ${sceneObject.sceneName}")/*Wrong!! we should return only after iterating through whole map objects*/
//            return sceneObject?.playstatus == SceneObject.CURRENTLY_PLAYING

            if (sceneObject?.playstatus == SceneObject.CURRENTLY_PLAYING) return true
        }

        return false
    }

    fun stopAllDevicesPlaying() {
        ScanningHandler.getInstance().sceneObjectMapFromRepo.forEach { (ipAddress: String?, sceneObject: SceneObject?) ->
            LibreLogger.d("stopAllDevicesPlaying", "$ipAddress, playStatus = ${sceneObject.playstatus}")
            LUCIControl(ipAddress).SendCommand(MIDCONST.MID_PLAYCONTROL.toInt(), LUCIMESSAGES.STOP, LSSDPCONST.LUCI_SET)
        }
    }

    fun isActivePlaylistNotAvailable(sceneObject: SceneObject?): Boolean {
        LibreLogger.d(TAG, "isActivePlaylistNotAvailable, ${sceneObject?.currentSource}")/* For Playing , If DMR is playing then we should give control for Play/Pause*/
        return sceneObject?.currentSource == Constants.NO_SOURCE || sceneObject?.currentSource == Constants.DDMSSLAVE_SOURCE || (sceneObject?.currentSource == Constants.DMR_SOURCE && (sceneObject.playstatus == SceneObject.CURRENTLY_STOPPED || sceneObject.playstatus == SceneObject.CURRENTLY_NOTPLAYING))
    }

    fun isDMRPlayingFromOtherPhone(sceneObject: SceneObject?): Boolean {
        LibreLogger.d(TAG, "isDMRPlayingFromOtherPhone, ${sceneObject?.sceneName}")/* For Playing , If DMR is playing then we should give control for Play/Pause*/
        return sceneObject?.currentSource == Constants.DMR_SOURCE && sceneObject.playstatus == SceneObject.CURRENTLY_PLAYING && !sceneObject.playUrl.isNullOrEmpty() && !sceneObject.playUrl.contains(LibreApplication.LOCAL_IP)
    }

    fun isLocalDMRPlaying(sceneObject: SceneObject?): Boolean {/* For Playing , If DMR is playing then we should give control for Play/Pause*/
        val isLocalDMRPlaying = sceneObject?.currentSource == Constants.DMR_SOURCE && !sceneObject.playUrl.isNullOrEmpty() && sceneObject.playUrl?.contains(LibreApplication.LOCAL_IP)!! && (sceneObject.playstatus == SceneObject.CURRENTLY_PLAYING || sceneObject.playstatus == SceneObject.CURRENTLY_PAUSED)
        LibreLogger.d(TAG, "isLocalDMRPlaying, ${sceneObject?.sceneName} $isLocalDMRPlaying")
        return isLocalDMRPlaying
    }

    inline fun <reified T : Serializable?> Intent.getSerializableIntent(intent: Intent,
        key: String): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getSerializableExtra(key, T::class.java)!!
        else intent.getSerializableExtra(key) as T
    }

    inline fun <reified T : Serializable> Bundle.serializableBundle(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)

        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
    }

    /**
     * Created By Shaik Mansoor
     */
    fun providesSharedPreference(context: Context): SharedPreferences {
        val sharedPreferences: SharedPreferences
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sharedPreferences = EncryptedSharedPreferences.create(context, "libre", getMasterKey(context), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

        } else {
            sharedPreferences = context.getSharedPreferences("libre", Context.MODE_PRIVATE)
        }
        return sharedPreferences
    }

    /**
     * Created By Shaik Mansoor
     */
    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }

    /**
     * Created By Shaik Mansoor
     */
    fun isFirstTimeLaunch(context: Context, isFirstTime: Boolean) {
        context.getSharedPreferences("Libre", MODE_PRIVATE).apply {
            edit().putBoolean("Libre_key", isFirstTime).commit()
        }
    }

    fun getIsFirstTimeLaunch(appContext: Context?): Boolean {
        val isFirst: Boolean?
        val pref = appContext!!.getSharedPreferences("Libre", MODE_PRIVATE)
        val editor = pref.edit()
        editor.commit()
        isFirst = pref?.getBoolean("Libre_key", false) ?: false
        return isFirst
    }

    /**
     * Shaik The Below Function is custom made function for the playPause icon with loader
     * Discussed with Suma
     */
    fun setPlayPauseLoader(ivPlayPauseView: ProgressButtonImageView,
        isEnabled: Boolean,
        isLoader: Boolean,
        image: Int) {
        ivPlayPauseView.setLoading(isLoader)
        ivPlayPauseView.isEnabled = isEnabled
        if (image != 0 || isLoader) {
            ivPlayPauseView.visibility = View.VISIBLE
            ivPlayPauseView.setImageResource(image)
        } else {
            ivPlayPauseView.visibility = View.GONE
        }
    }

    /**
     * Created By Shaik
     * 06/OCT/2023
     */
    fun getVersion(context: Context, flags: Int = 1): String {
        var version = context.getString(R.string.title_activity_welcome)
        var pInfo: PackageInfo? = null
        try {
            pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                context.packageManager.getPackageInfo(context.packageName, flags)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (pInfo != null) version = pInfo.versionName
        return version
    }

    fun isValidPassword(password: String): Boolean {
        return password.length < 8
    }

    fun isValidWifiPassword64(password: String?): Boolean {
        val wifiPasswordMaxLength = 64
        return !password.isNullOrBlank() && password.length <= wifiPasswordMaxLength
    }

    /*   Your phone is currently connected to 'Epic5' Wi-Fi network, but trying to configure the device to 'ORBIMESH' Wi-Fi network.
       Make sure to be on the same network
       for discovering the speaker.*/
    fun networkMismatchSsidMessage(connectedSSID: String?, connectingSSID: String): String {
        return StringBuilder().apply {
            append("Your phone is currently connected to ")
                .append("'$connectedSSID'")
                .append(" WI-Fi network, but you are trying to configure the device to ")
                .append("'$connectingSSID'")
                .append(" WI-Fi network. Make sure to be on the same network to discover the " +
                        "speaker.")
        }.toString()
    }
    fun networkMismatchMessage(connectedSSID: String?, connectingSSID: String): String {
        return StringBuilder().apply {
            append("You're currently connected to ")
                .append("'$connectedSSID'")
                .append(" WI-fi network, but configured the device to ")
                .append("'$connectingSSID'")
                .append(" WI-Fi network. Please connect to ")
                .append("'$connectingSSID' WI-fi network.")
        }.toString()
    }


}
