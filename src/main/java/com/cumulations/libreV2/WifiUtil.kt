package com.cumulations.libreV2

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkSpecifier
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiConfiguration.GroupCipher
import android.net.wifi.WifiConfiguration.KeyMgmt
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.cumulations.libreV2.activity.CTDeviceDiscoveryActivity
import com.libreAlexa.constants.AppConstants
import com.libreAlexa.util.LibreLogger


class WifiUtil(private val context: Context) : CTDeviceDiscoveryActivity() {
    companion object {
        // Constants used for different security types
        const val WPA2 = "WPA2"
        const val WPA = "WPA"
        const val WEP = "WEP"
        const val OPEN = "Open"
        val TAG: String = WifiUtil::class.java.simpleName

        /* For EAP Enterprise fields */
        const val WPA_EAP = "WPA-EAP"
        const val IEEE8021X = "IEEE8021X"
    }

    private val wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    private var wifiList: List<ScanResult> = arrayListOf()
    private val myBroadCastReceiver: MyBroadcastReceiver = MyBroadcastReceiver()
    private lateinit var wifiNetworkSpecifier: NetworkSpecifier
    private lateinit var suggestionsList: List<WifiNetworkSuggestion>

    /**
     * Commented By:-  SHAIK MANSOOR
     * TRANSPORT_WIFI - Indicates this network uses a Wi-Fi transport.
     * We can check if the device is connected to wifi or a cellular network by using the
     * hasTransport() method of NetworkCapabilities class.
     * For the devices below Marshmallow (API 23 or below android 6), we can use NetworkInfo
     * .State API
     * Note: NetworkInfo has been deprecated in API 29. We are using it in marshmallow (API 23),
     * so we don’t get any problems.
     */
    fun isWifiOn(): Boolean {
        /**
         *    Equal and Above Android 6 (6,7,8,9,10,11,12,13...) version of devices and API level
         *    Above and Equal to 23 (23 to 32)
         *    If the Wifi connected "networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)"
         *    Will return TRUE or else FALSE Commented By:-  SHAIK MANSOOR
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager!!.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return if (networkCapabilities == null) {
                //Not Connected
                false
            } else {
                //Connected
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) true
                else return false
            }
        } else {
            /**
             * Commented By:-  SHAIK MANSOOR
             *    Below Android 6 version of devices and API level below 23
             *    If the Wifi connected "networkInfo.isConnected"  will return TRUE or else FALSE
             */
            return try {
                val networkInfo = connectivityManager!!.activeNetworkInfo
                networkInfo != null && networkInfo.isConnected
            } catch (e: NullPointerException) {
                e.printStackTrace()
                false

            }
        }
    }


    private fun formatIP(ip: Int): String {
        return String.format("%d.%d.%d.%d", ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff)
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            wifiList = wifiManager.scanResults

            if (wifiList.isEmpty()) {
                if (wifiManager.startScan()) return
            }

            if (wifiList.isNotEmpty()) if (context is CTDeviceDiscoveryActivity) {
                context.onWifiScanDone(wifiList)
            }

            val wifiNamesList = arrayListOf<String>()
            for (name in wifiList) {
                wifiNamesList.add(name.SSID)
            }

            unregister()
        }

    }

    fun unregister() {
        try {
            context.unregisterReceiver(myBroadCastReceiver)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Commented By:-  SHAIK MANSOOR
     * The below function will call very rarely that is when securityType(WPA,WPA2,Open) is empty
     * then only the below function will call but here again the wifiList is empty because the
     * WifiList is assigning inside the "MyBroadcastReceiver" onReceive method but the
     * "MyBroadcastReceiver" is never calling
     * Discussed with SUMA.
     */
    private fun getSecurityType(ssid: String): String {
        if (wifiList.isEmpty()) {
            return ""
        }

        for (scanResult in wifiList) {
            if (scanResult.SSID == ssid) {
                return getScanResultSecurity(scanResult)
            }
        }

        return ""
    }

    private fun getScanResultSecurity(scanResult: ScanResult): String {
        val cap = scanResult.capabilities
        val securityModes = arrayOf<String>(WEP, WPA, WPA2, WPA_EAP, IEEE8021X)
        for (i in securityModes.indices.reversed()) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i]
            }
        }
        return OPEN
    }

    /**
     * Commented By:-  SHAIK MANSOOR
     * SHAIk Observation
     * The below disconnectCurrentWifi function will disconnect the device ssid (WIFI i;e
     * Libre16BF.d) and try to connect to available WIFI, which is user selected WIFI from the
     * ScanList(WIFI-List)and PWD.
     * BUT the "disconnect" method is deprecated in 29, So I have migrated to the latest API for
     * the above Android version 10, i;e "removeNetworkSuggestions"
     * ==========The Observation is here==========
     * In the above Android version 10 devices all are working fine, tested in the 4
     * devices (Pixel-13, OnePlusNord-CE2-13,RealMe-11,Samsung TAB-12) except "OnePlusNord" device
     * (AC2001), this device behavior is very different,after disconnecting the device SSID, the
     * "removeNetworkSuggestions" method is navigating to the WIFI select screen in the mobile,
     * discussed the same with SUMA.
     */
    fun disconnectCurrentWifi(deviceName: String): Boolean {
        //LibreLogger.d(TAG, "disconnectCurrentWifi: $deviceName")
        val isDisconnected: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (deviceName == "OnePlus AC2001") {
                customStartActivityForResult(AppConstants.WIFI_SETTINGS_REQUEST_CODE, Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                })
                true
            } else {
                val suggestionsList: MutableList<WifiNetworkSuggestion> = ArrayList()
                wifiManager.removeNetworkSuggestions(suggestionsList)
                true
            }
        } else {
            wifiManager.disconnect()
        }
        return isDisconnected
    }

    fun connectWiFiToSSID(networkSSID: String, networkPass: String, networkSec: String): Int {
        var networkId = -1
        try {
            var wifiConfiguration = getExistingWifiConfig(networkSSID)
            if (wifiConfiguration == null) {
                if (Build.VERSION.SDK_INT >= 31) {
                    // Do something for 12 and above versions
                    val wifiNetworkSuggestion = WifiNetworkSuggestion.Builder().setSsid(networkSSID)
                        .setWpa2Passphrase(networkPass).build()
                    suggestionsList = listOf(wifiNetworkSuggestion)
                    wifiNetworkSpecifier = WifiNetworkSpecifier.Builder().setSsid(networkSSID)
                        .setWpa2Passphrase(networkPass).build()
                    val networkRequest = NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .setNetworkSpecifier(wifiNetworkSpecifier).build()
                    val networkCallback =
                        object : ConnectivityManager.NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {

                            override fun onAvailable(network: Network) {
                                super.onAvailable(network)
                                connectivityManager?.bindProcessToNetwork(network)
                            }

                        }
                    connectivityManager?.requestNetwork(networkRequest, networkCallback)
                } else {
                    // do something for phones running an SDK before 12
                    wifiConfiguration = WifiConfiguration()
                    wifiConfiguration.SSID =
                        "\"" + networkSSID + "\""   // Please note the quotes. String should contain ssid in quotes
                    wifiConfiguration.status = WifiConfiguration.Status.ENABLED
                    wifiConfiguration.priority = 1000
                }


                var securityType = ""

                if (securityType.isEmpty()) {
                    securityType = getSecurityType(networkSSID)
                    if (securityType.isEmpty()) {
                        return -1
                    }
                }

                when {
                    securityType.contains("WEP") -> {
                        if (wifiConfiguration != null) {
                            wifiConfiguration.allowedKeyManagement.set(KeyMgmt.NONE)
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.WEP40)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.WEP104)

                            if (networkPass.matches("^[0-9a-fA-F]+$".toRegex())) {
                                wifiConfiguration.wepKeys[0] = networkPass
                            } else {
                                wifiConfiguration.wepKeys[0] = "\"" + networkPass + "\""
                            }

                            wifiConfiguration.wepTxKeyIndex = 0
                        }

                    }

                    securityType.contains("WPA") || securityType.contains("WPA2") -> {
                        if (wifiConfiguration != null) {
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                            wifiConfiguration.allowedKeyManagement.set(KeyMgmt.WPA_PSK)
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.WEP40)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.WEP104)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.CCMP)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.TKIP)
                            wifiConfiguration.preSharedKey = "\"" + networkPass + "\""
                        }

                    }

                    else -> {
                        if (wifiConfiguration != null) {
                            wifiConfiguration.allowedKeyManagement.set(KeyMgmt.NONE)
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                            wifiConfiguration.allowedAuthAlgorithms.clear()
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.WEP40)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.WEP104)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.CCMP)
                            wifiConfiguration.allowedGroupCiphers.set(GroupCipher.TKIP)
                        }
                    }
                }

                networkId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    wifiManager.addNetworkSuggestions(suggestionsList)
                } else {
                    wifiManager.addNetwork(wifiConfiguration)

                }
            } else {
                networkId = wifiConfiguration.networkId
            }
            LibreLogger.d(TAG, "addNetworkSuggestions:FINAL networkId  $networkId")
            /**
             * The Below IF and ELSE code just to debug the flow and we are not using it anywhere
             *//* if (networkId != -1) {

                val isDisconnected = wifiManager.disconnect()
                LibreLogger.d(TAG, "isDisconnected : $isDisconnected")

                val isEnabled = wifiManager.enableNetwork(*//*i.networkId*//*networkId, true)
                LibreLogger.d(TAG, "isEnabled : $isEnabled")

                val isReconnected = wifiManager.reconnect()
                LibreLogger.d(TAG, "isReconnected : $isReconnected")
            } else {
                LibreLogger.d(TAG, "failed to connect wifi")
            }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return networkId
    }

    private fun getExistingWifiConfig(ssid: String): WifiConfiguration? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            for (wifiConfiguration in wifiManager.configuredNetworks) {
                if (wifiConfiguration.SSID == "\"$ssid\"") {
                    return wifiConfiguration
                }
            }

        }
        return null
    }
        // Can be used in the future for scan result
    /**
     * @return The security of a given [WifiConfiguration].
     *//*
    fun getWifiConfigurationSecurity(wifiConfig: WifiConfiguration): String {
        when {
            wifiConfig.allowedKeyManagement.get(KeyMgmt.NONE) -> // If we never set group ciphers, wpa_supplicant puts all of them.
                // For open, we don't set group ciphers.
                // For WEP, we specifically only set WEP40 and WEP104, so CCMP
                // and TKIP should not be there.
                return if (!wifiConfig.allowedGroupCiphers.get(GroupCipher.CCMP) && (wifiConfig.allowedGroupCiphers.get(GroupCipher.WEP40) || wifiConfig.allowedGroupCiphers.get(GroupCipher.WEP104))) {
                    WEP
                } else {
                    OPEN
                }

            wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_EAP) -> return WPA_EAP
            wifiConfig.allowedKeyManagement.get(KeyMgmt.IEEE8021X) -> return IEEE8021X
            wifiConfig.allowedProtocols.get(WifiConfiguration.Protocol.RSN) -> return WPA2
            wifiConfig.allowedProtocols.get(WifiConfiguration.Protocol.WPA) -> return WPA
            else -> {
                return OPEN
            }
        }
    }

    fun getWifiSupplicantState(): SupplicantState {
        val supplicantState = wifiManager.connectionInfo.supplicantState
        return supplicantState
    }

    fun getConnectedRouterIp(): String {
        val dhcpInfo = wifiManager.dhcpInfo
        val ipAddress = dhcpInfo.ipAddress
        val formattedIp = formatIP(ipAddress)
        return formattedIp
    }

    fun startWifiScan() {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }
        wifiManager.startScan()
        context.registerReceiver(myBroadCastReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    fun isSACModeOn(): Boolean {
        for (scanResult in wifiList) {
            if (scanResult.SSID == Constants.RIVAA_WAC_SSID) return true
        }
        return false
    }

    fun forgetWifiSSid(ssid: String): Boolean {
        val wifiConfiguration = getExistingWifiConfig(ssid)
        if (wifiConfiguration != null && wifiConfiguration.networkId != -1) {
            return wifiManager.removeNetwork(wifiConfiguration.networkId)
        }
        return true
    }*/
}
